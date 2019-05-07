package com.autohome.mobile.druidservice.util;

import com.autohome.mobile.druidservice.annotation.Column;
import com.autohome.mobile.druidservice.annotation.sql.Select;
import com.autohome.mobile.druidservice.dao.BaseDaoInterface;
import com.autohome.mobile.druidservice.exception.AnnotationException;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Component
public class BaseDao<T> {
    @Autowired
    private HikariDataSource hikariDataSource;
    @Autowired
    private ServiceGroupService serviceGroupService;
    public List<Map<String,Object>> handleSearch(BaseDaoInterface baseDaoInterface,String methodName, Class clazz,T t) throws NoSuchMethodException, AnnotationException, IllegalAccessException, NoSuchFieldException {
        Method method = baseDaoInterface.getClass().getDeclaredMethod(methodName, clazz);

        if (!method.isAnnotationPresent(Select.class)){
            throw new AnnotationException("select annotation not found ...");
        }
        Select select = method.getDeclaredAnnotation(Select.class);
        String baseSql = select.value();


        int tableIndex = -1;
        //是否包含where查询条件
        boolean flag = false;
        System.out.println(baseSql);
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer buffer = new StringBuffer();

        if (baseSql.indexOf("where") != -1){
            flag = true;
        }
        if (flag){
            buffer.append("and");
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
        System.out.println(baseSql);
        try {
            Connection connection = hikariDataSource.getConnection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(baseSql);
            while (resultSet.next()){
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
