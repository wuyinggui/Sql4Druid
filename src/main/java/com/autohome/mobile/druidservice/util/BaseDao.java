package com.autohome.mobile.druidservice.util;

import com.autohome.mobile.druidservice.annotation.Column;
import com.autohome.mobile.druidservice.annotation.sql.Select;
import com.autohome.mobile.druidservice.dao.BaseDaoInterface;
import com.autohome.mobile.druidservice.exception.AnnotationException;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BaseDao<T> {
    @Autowired
    private HikariDataSource hikariDataSource;
    @Autowired
    private ServiceGroupService serviceGroupService;
    public List<Map<String,Object>> handleSearch(BaseDaoInterface baseDaoInterface,String methodName, T t) throws NoSuchMethodException, AnnotationException, IllegalAccessException, NoSuchFieldException {
        Class clazz = t.getClass();
        Method method = baseDaoInterface.getClass().getDeclaredMethod(methodName, clazz);

        if (!method.isAnnotationPresent(Select.class)){
            throw new AnnotationException("select annotation not found ...");
        }
        List<Map<String,Object>> result = new ArrayList<>();
        Select select = method.getDeclaredAnnotation(Select.class);
        String baseSql = select.value();

        int tableIndex = -1;
        //是否包含where查询条件
        boolean flag = false;

        Field[] fields = clazz.getDeclaredFields();
        StringBuffer buffer = new StringBuffer();

        if (baseSql.indexOf("where") != -1){
            flag = true;
        }
        if (flag){
            buffer.append("and");
        }else{
            if (baseSql.indexOf("#{dynamic}")!= -1){
                buffer.append("where");
            }
        }
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Column.class)){
                continue;
            }

            String fieldName = field.getName();


            Column column = field.getDeclaredAnnotation(Column.class);
            String columnName = column.value();

            field.setAccessible(true);
            String colValue = column.value();
            String referColumn = column.refercolumn();
            if ("".equals(colValue) && "".equals(referColumn)){
                throw new AnnotationException("column annotation must contains value or refercolumn tag ....");
            }
            Object fieldValue = field.get(t);
            if (fieldValue == null || "".equals(fieldName)){
                continue;
            }
            if (!"".equals(colValue)){
                //完全匹配
                if (fieldValue != null){
                    buffer.append(" \"" + columnName + "\" = '"+fieldValue+"' and ");
                }
            }else if (!"".equals(referColumn)){
                //其他操作
                String columnOperator = column.operator();
                if ("".equals(columnOperator)){
                    throw new AnnotationException(fieldName + " column must have an operator ...");
                }
                if ("like".equals(columnOperator)){
                    buffer.append(" \"" + referColumn + "\" like '%"+fieldValue+"%' and ");
                }else if ("in".equals(columnOperator)){
                    if ("module".equals(referColumn)){
                        Field platformField = t.getClass().getDeclaredField("platform");
                        platformField.setAccessible(true);
                        String platform = platformField.get(t).toString();
                        Integer platformValue = "".equals(platform) ? null : new Integer(platform);
                        List<String> pluginNameList = serviceGroupService.getPluginName(fieldValue.toString(), platformValue);
                        if (pluginNameList != null && !pluginNameList.isEmpty()){
                            StringBuffer moduleBuffer = new StringBuffer();
                            moduleBuffer.append(" \"" + referColumn + "\" in (");
                            for (int i = 0; i < pluginNameList.size(); i++) {
                                if (i != pluginNameList.size()-1){
                                    moduleBuffer.append("'" + pluginNameList.get(i) + "',");
                                }else{
                                    moduleBuffer.append("'" + pluginNameList.get(i) + "'");
                                }
                            }
                            moduleBuffer.append(") and ");
                            buffer.append(moduleBuffer.toString());
                        }
                    }else{
                        fieldValue = fieldValue.toString().replaceAll(",","','");
                        fieldValue = "('" + fieldValue + "')";
                        buffer.append(" \"" + referColumn + "\" in "+fieldValue+" and ");
                    }
                }
            }

        }

        buffer.delete(buffer.lastIndexOf("and"), buffer.length());
        baseSql = baseSql.replace(select.dynamicReplaceMark(),buffer.toString());
        if (baseSql.indexOf("begin") != -1){
            Field beginField = t.getClass().getDeclaredField("begin");
            beginField.setAccessible(true);
            Object beginTime = beginField.get(t);
            if (beginTime == null){
                throw new IllegalAccessException("sql中指定了begin占位符，但未传递相关参数值。。。");
            }
            baseSql = baseSql.replace("#{begin}",beginTime.toString());
        }
        if (baseSql.indexOf("end") != -1){
            Field endField = t.getClass().getDeclaredField("end");
            endField.setAccessible(true);
            Object endTime = endField.get(t);
            if (endTime == null){
                throw new IllegalAccessException("sql中指定了end占位符，但未传递相关参数值。。。");
            }
            baseSql = baseSql.replace("#{end}",endTime.toString());
        }
        System.out.println("+++++:" + baseSql);
        try {
            Connection connection = hikariDataSource.getConnection();
            Statement statement = connection.createStatement();
            Class[] resultTypes = select.resultTypes();
            ResultSet resultSet = statement.executeQuery(baseSql);
            if (resultTypes.length == 0){
                //默认查询全部字段
                while(resultSet.next()) {
                    Map<String,Object> info = new HashMap<>();
                    initObject(info, fields, resultSet);
                    result.add(info);
                }
            }else{
                String[] columnLabels = select.columnLabels();
                Map<Integer,String> columnIndexPropertyMap = new HashMap<>();
                if (columnLabels.length != 0){
                    int index = 1;
                    for (String columnLabel : columnLabels) {
                        columnIndexPropertyMap.put(index,columnLabel);
                        index ++;
                    }
                }
                while (resultSet.next()){
                    Map<String,Object> info = new HashMap<>();
                    initMap(columnIndexPropertyMap,info,resultTypes, resultSet);
                    result.add(info);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initMap(Map<Integer, String> columnIndexPropertyMap, Map<String, Object> info, Class[] resultTypes, ResultSet rs) throws SQLException {

        int index = 1;
        for (Class clazzField : resultTypes) {
            Object paramVal = null;
            if (clazzField == String.class) {
                paramVal = rs.getString(index);
            } else if (clazzField == short.class || clazzField == Short.class) {
                paramVal = rs.getShort(index);
            } else if (clazzField == int.class || clazzField == Integer.class) {
                paramVal = rs.getInt(index);
            } else if (clazzField == long.class || clazzField == Long.class) {
                paramVal = rs.getLong(index);
            } else if (clazzField == float.class || clazzField == Float.class) {
                paramVal = rs.getFloat(index);
            } else if (clazzField == double.class || clazzField == Double.class) {
                paramVal = rs.getDouble(index);
            } else if (clazzField == boolean.class || clazzField == Boolean.class) {
                paramVal = rs.getBoolean(index);
            } else if (clazzField == byte.class || clazzField == Byte.class) {
                paramVal = rs.getByte(index);
            } else if (clazzField == char.class || clazzField == Character.class) {
                paramVal = rs.getCharacterStream(index);
            }  else if (clazzField.isArray()) {
                paramVal = rs.getString(index).split(",");	//以逗号分隔的字符串
            }
            info.put(columnIndexPropertyMap.get(index),paramVal);
            index ++;
        }

    }

    /**
     * 根据结果集初始化对象
     */
    private void initObject(Map<String,Object> info, Field[] fields, ResultSet rs)
            throws SQLException, IntrospectionException,
            IllegalAccessException, InvocationTargetException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        for (Field field : fields) {
            String propertyName = field.getName();
            Object paramVal = null;
            Class<?> clazzField = field.getType();
            if(!field.isAnnotationPresent(Column.class)){
                continue;
            }
            Column column = field.getDeclaredAnnotation(Column.class);
            if (!"".equals(column.refercolumn())){
                continue;
            }
            String columnName = column.value();
            if (clazzField == String.class) {
                paramVal = rs.getString(columnName);
            } else if (clazzField == short.class || clazzField == Short.class) {
                paramVal = rs.getShort(columnName);
            } else if (clazzField == int.class || clazzField == Integer.class) {
                paramVal = rs.getInt(columnName);
            } else if (clazzField == long.class || clazzField == Long.class) {
                paramVal = rs.getLong(columnName);
            } else if (clazzField == float.class || clazzField == Float.class) {
                paramVal = rs.getFloat(columnName);
            } else if (clazzField == double.class || clazzField == Double.class) {
                paramVal = rs.getDouble(columnName);
            } else if (clazzField == boolean.class || clazzField == Boolean.class) {
                paramVal = rs.getBoolean(columnName);
            } else if (clazzField == byte.class || clazzField == Byte.class) {
                paramVal = rs.getByte(columnName);
            } else if (clazzField == char.class || clazzField == Character.class) {
                paramVal = rs.getCharacterStream(columnName);
            } else if (clazzField == Date.class) {
                try {
                    paramVal = format.parse(rs.getString(columnName));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (clazzField.isArray()) {
                paramVal = rs.getString(columnName).split(",");	//以逗号分隔的字符串
            }
            info.put(propertyName,paramVal);
        }
    }
}
