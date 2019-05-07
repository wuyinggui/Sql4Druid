//package com.autohome.mobile.druidservice.dao.util;
//
//import com.autohome.mobile.druidservice.annotation.Column;
//import com.autohome.mobile.druidservice.exception.AnnotationException;
//import com.autohome.mobile.druidservice.util.DruidDbUtil;
//import org.springframework.stereotype.Component;
//
//import java.beans.IntrospectionException;
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.sql.*;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.Date;
//
//@Component
//public class GenericDaoImpl<T> implements GenericDao<T> {
//    public static String[] keywords = new String[]{"count","module","__time","time"};
//
//
//    //表的别名
//    public List<T> findAllByEntity(T t) throws Exception {
//        Class<T> clazz = (Class<T>) t.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        Map<String,Object> sqlWhereMap = new HashMap<>();
//        if (fields != null && fields.length > 0){
//            for (Field field : fields) {
//                field.setAccessible(true);
//                try {
//                    Object val = field.get(t);
//                    if (val != null){
//                        sqlWhereMap.put(field.getName(),val);
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//            return findAllByConditions(sqlWhereMap,clazz);
//        }
//        return null;
//    }
//
//    public boolean isKeyword(String keyword){
//        for (String keywd : keywords) {
//            if (keyword.equals(keywd)){
//                return true;
//            }
//        }
//        return false;
//    }
//    @Override
//    public List<T> findAllByConditions(Map<String, Object> sqlWhereMap, Class<T> clazz) throws Exception {
//        List<T> list = new ArrayList<T>();
//        Map<String,String> tableInfoMap = getTableInfo(clazz);
//        String timeColumn = tableInfoMap.get("timeColumn");
//        String timeformat = tableInfoMap.get("timeformat");
//        String timezone = tableInfoMap.get("timezone");
//        String idFieldName = "";
//        //存储所有字段的信息
//        //通过反射获得要查询的字段
//        StringBuffer fieldNames = new StringBuffer();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            String propertyName = field.getName();
//            if (field.isAnnotationPresent(Column.class)) {
//                if(field.getAnnotation(Column.class).nullable()){
//                    continue;
//                }
//                String columnName = field.getAnnotation(Column.class).value();
//                if (columnName.equals(timeColumn)){
//                    if (isKeyword(columnName)){
//                        fieldNames.append("TIME_FORMAT(\""+columnName+"\",'"+timeformat+"','"+timezone+"')");
//                    }else{
//                        fieldNames.append(columnName);
//                    }
//                }else{
//                    if (isKeyword(columnName)){
//                        fieldNames.append("\""+columnName+"\"");
//                    }else{
//                        fieldNames.append(columnName);
//                    }
//                }
//
//                if (isKeyword(propertyName)){
//                    fieldNames.append(" as \""+propertyName+"\"").append(",");
//                }else{
//                    fieldNames.append(" as ").append(propertyName).append(",");
//                }
//
//            }
//        }
//        fieldNames.deleteCharAt(fieldNames.length()-1);
//        //拼装SQL
//        String sql = "select " + fieldNames + " from " + tableInfoMap.get("tableName") ;
//        Statement ps = null;
//        List<Object> values = null;
//        boolean hasFilter = false;
//        if (sqlWhereMap != null) {
//            List<Object> sqlWhereWithValues = getSqlWhereWithValues(sqlWhereMap,tableInfoMap);
//
//            if (sqlWhereWithValues != null && sqlWhereWithValues.size() >= 2) {
//                //拼接SQL条件
//                String sqlWhere = (String)sqlWhereWithValues.get(0);
//                sql += sqlWhere;
//                //得到SQL条件中占位符的值
//                values = (List<Object>)sqlWhereWithValues.get(1);
//                if(values!= null && values.size() != 0){
//                    hasFilter = true;
//                }
//            }
//        }
//
//        if (sqlWhereMap.containsKey("begin")){
//            if (hasFilter){
//                sql += " and ";
//            }
//            sql += timeColumn + "  >= TIME_PARSE('" + sqlWhereMap.get("begin") + "','" + timeformat + "','" + timezone + "') ";
//            hasFilter = true;
//        }
//        if (sqlWhereMap.containsKey("end")){
//            if (hasFilter){
//                sql += " and ";
//            }
//            sql +=  timeColumn + "  >= TIME_PARSE('" + sqlWhereMap.get("end") + "','" + timeformat + "','" + timezone + "') ";
//        }
//        sql += " limit 100";
//        Connection connection = DruidDbUtil.getConnection();
//        System.out.println("+++++++sql:"+sql);
//        ps = connection.createStatement();
//        //执行SQL
//        ResultSet rs = ps.executeQuery(sql);
//        while(rs.next()) {
//            T t = clazz.newInstance();
//            initObject(t, fields, rs);
//            list.add(t);
//        }
//
//        //释放资源
//        DruidDbUtil.relase(connection, ps, rs);
//        return list;
//    }
//
//    /**
//     * 获得表名
//     */
//    private Map<String,String> getTableInfo(Class<?> clazz) throws AnnotationException {
//        if (clazz.isAnnotationPresent(DataSource.class)) {
//            DataSource entity = clazz.getAnnotation(DataSource.class);
//            String tableName = entity.value();
//            String timeColumn = entity.timeColumn();
//            String timeformat = entity.timeformat();
//            String timezone = entity.timezone();
//            Map<String,String> result = new HashMap<>();
//            result.put("tableName",tableName);
//            result.put("timeColumn",timeColumn);
//            result.put("timeformat",timeformat);
//            result.put("timezone",timezone);
//            return result;
//        } else {
//            throw new AnnotationException(clazz.getName() + " is not Entity Annotation.");
//        }
//    }
//    /**
//     * 根据条件，返回sql条件和条件中占位符的值
//     * @param sqlWhereMap key：字段名 value：字段值
//     * @return 第一个元素为SQL条件，第二个元素为SQL条件中占位符的值
//     */
//    private List<Object> getSqlWhereWithValues(Map<String,Object> sqlWhereMap,Map<String,String> tableInfoMap) {
//        if (sqlWhereMap.size() <1 ) return null;
//        List<Object> list = new ArrayList<Object>();
//        List<Object> fieldValues = new ArrayList<Object>();
//        StringBuffer sqlWhere = new StringBuffer(" where ");
//        Set<Map.Entry<String, Object>> entrySets = sqlWhereMap.entrySet();
//        for (Iterator<Map.Entry<String, Object>> iteraotr = entrySets.iterator(); iteraotr.hasNext();) {
//            Map.Entry<String, Object> entrySet = iteraotr.next();
//            String key = entrySet.getKey();
//            if ("begin".equals(key) || "end".equals(key)){
//                continue;
//            }
//            if (tableInfoMap.containsKey(key)){
//                continue;
//            }
//            fieldValues.add(entrySet.getValue());
//            Object value = entrySet.getValue();
//            if (value.getClass() == String.class) {
//                if (value.toString().indexOf("%") != -1){
//                    sqlWhere.append(key).append(" like ").append("'"+value+"'").append(" and ");
//                }else{
//                    sqlWhere.append(key).append("=").append("'"+value+"'").append(" and ");
//                }
//            } else {
//                sqlWhere.append(key).append("=").append("'"+value+"'").append(" and ");
//            }
//        }
//
//        if (sqlWhere.indexOf("and") != -1){
//            sqlWhere.delete(sqlWhere.lastIndexOf("and"), sqlWhere.length());
//        }
//        list.add(sqlWhere.toString());
//        list.add(fieldValues);
//
//        return list;
//    }
//
//    /**
//     * 设置SQL参数占位符的值
//     */
//    private void setParameter(List<Object> values, PreparedStatement ps) throws SQLException {
//        for (int i = 1; i <= values.size(); i++) {
//            Object fieldValue = values.get(i-1);
//            Class<?> clazzValue = fieldValue.getClass();
//            if (clazzValue == String.class) {
//                ps.setString(i, (String)fieldValue);
//            } else if (clazzValue == boolean.class || clazzValue == Boolean.class) {
//                ps.setBoolean(i, (Boolean)fieldValue);
//            } else if (clazzValue == byte.class || clazzValue == Byte.class) {
//                ps.setByte(i, (Byte)fieldValue);
//            } else if (clazzValue == char.class || clazzValue == Character.class) {
//                ps.setObject(i, fieldValue, Types.CHAR);
//            } else if (clazzValue == Date.class) {
//                ps.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));
//            } else if (clazzValue.isArray()) {
//                Object[] arrayValue = (Object[]) fieldValue;
//                StringBuffer sb = new StringBuffer();
//                for (int j = 0; j < arrayValue.length; j++) {
//                    sb.append(arrayValue[j]).append("、");
//                }
//                ps.setString(i, sb.deleteCharAt(sb.length()-1).toString());
//            } else {
//                ps.setObject(i, fieldValue, Types.NUMERIC);
//            }
//        }
//    }
//
//    /**
//     * 根据结果集初始化对象
//     */
//    private void initObject(T t, Field[] fields, ResultSet rs)
//            throws SQLException, IntrospectionException,
//            IllegalAccessException, InvocationTargetException, AnnotationException {
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        for (Field field : fields) {
//            String propertyName = field.getName();
//            Object paramVal = null;
//            Class<?> clazzField = field.getType();
//            if(!field.isAnnotationPresent(Column.class)){
//                continue;
//            }
//            if (clazzField == String.class) {
//                paramVal = rs.getString(propertyName);
//            } else if (clazzField == short.class || clazzField == Short.class) {
//                paramVal = rs.getShort(propertyName);
//            } else if (clazzField == int.class || clazzField == Integer.class) {
//                paramVal = rs.getInt(propertyName);
//            } else if (clazzField == long.class || clazzField == Long.class) {
//                paramVal = rs.getLong(propertyName);
//            } else if (clazzField == float.class || clazzField == Float.class) {
//                paramVal = rs.getFloat(propertyName);
//            } else if (clazzField == double.class || clazzField == Double.class) {
//                paramVal = rs.getDouble(propertyName);
//            } else if (clazzField == boolean.class || clazzField == Boolean.class) {
//                paramVal = rs.getBoolean(propertyName);
//            } else if (clazzField == byte.class || clazzField == Byte.class) {
//                paramVal = rs.getByte(propertyName);
//            } else if (clazzField == char.class || clazzField == Character.class) {
//                paramVal = rs.getCharacterStream(propertyName);
//            } else if (clazzField == Date.class) {
//                try {
//                    paramVal = format.parse(rs.getString(propertyName));
//                } catch (ParseException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            } else if (clazzField.isArray()) {
//                paramVal = rs.getString(propertyName).split(",");	//以逗号分隔的字符串
//            }
//            PropertyDescriptor pd = new PropertyDescriptor(propertyName,t.getClass());
//            pd.getWriteMethod().invoke(t, paramVal);
//        }
//    }
//}
