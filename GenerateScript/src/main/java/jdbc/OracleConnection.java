package jdbc;

import DataStructure.ResourcePool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * CheckModule
 * Created by ccwei on 2018/9/30.
 */
public class OracleConnection {

    private static Logger logger = LoggerFactory.getLogger(OracleConnection.class);

    static String queryType = "SELECT  OWNER,TABLE_NAME,COLUMN_NAME,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE\n" +
            "from ALL_TAB_COLS \n" +
            "WHERE OWNER IN ('BASIS','BUSINESS','META')";
    static String queryComment = "SELECT * from all_col_comments WHERE OWNER IN ('BASIS','BUSINESS','META')";
    static String queryTableName = "select table_name from dba_tables where owner IN ('BASIS','BUSINESS','META') ";


    //    public static  void main(String args[]){
    @Test
    public static void init(){
        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{

            /**
             *    1.jdbc:oracle:thin:@//<host>:<port>/<service_name>
             *    2.jdbc:oracle:thin:@<host>:<port>:<SID>
             *    3.jdbc:oracle:thin:@<TNSName>
             */
            Class.forName("oracle.jdbc.OracleDriver");
            connect = DriverManager.getConnection("jdbc:oracle:thin:@//10.147.111.11:1521/orcl","gxggcx","gxggcx");

            logger.info("Got connection : " + connect.toString());

            HashSet<String> tables = getTableNames(connect);

            HashMap<String, HashMap<String,String>> commentHashMap = getCommentMap(connect, tables);
            HashMap<String, HashMap<String,String>> typeHashMap = convertTypeOrcl2Hive(connect, tables);

            ResourcePool.setTypeHashMap(typeHashMap);

            logger.info("success!");

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //第六步：关闭资源
            try {
                if (resultSet!=null) resultSet.close();
                if (statement!=null) statement.close();
                if (connect!=null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private static HashMap<String, HashMap<String,String>> getCommentMap(Connection connect, HashSet<String> tables) throws SQLException {
        HashMap<String, HashMap<String,String>> commentHashMap = new HashMap<String, HashMap<String, String>>();

        String sql = queryComment;
        PreparedStatement preState = connect.prepareStatement(sql);
        ResultSet result = preState.executeQuery();

        while (result.next()) {
            String table_name = result.getString("TABLE_NAME");
            if(!tables.contains(table_name)){
                continue;
            }

            String owner =  result.getString("OWNER");
            String column_name = result.getString("COLUMN_NAME");
            String comments = result.getString("COMMENTS");
            putIntoMap(commentHashMap, column_name, table_name, comments);
        }

        return commentHashMap;
    }

    private static void putIntoMap(HashMap<String, HashMap<String, String>> hashMap, String column_name, String table_name, String value) {
        if(hashMap.containsKey(table_name)){
            HashMap<String,String> map = hashMap.get(table_name);
            map.put(column_name,value);
            hashMap.put(table_name,map);
        }else {
            HashMap<String,String> map = new HashMap<String, String>();
            map.put(column_name,value);
            hashMap.put(table_name,map);
        }
    }

    public static HashMap<String, HashMap<String,String>> convertTypeOrcl2Hive(Connection connect, HashSet<String> tables) throws SQLException {

        HashMap<String, HashMap<String,String>> typeHashMap = new HashMap<String, HashMap<String, String>>();

        String sql = queryType;
        PreparedStatement preState = connect.prepareStatement(sql);
        ResultSet result = preState.executeQuery();

        while (result.next()) {
            String table_name = result.getString("TABLE_NAME");
            if(!tables.contains(table_name)){
                continue;
            }

            String column_name = result.getString("COLUMN_NAME");
            String data_type = result.getString("DATA_TYPE");
            Integer data_length = result.getInt("DATA_LENGTH");
            Integer data_precision = result.getInt("DATA_PRECISION");
            Integer data_scale = result.getInt("DATA_SCALE");

            String type = "";

            if(data_type.equals("NUMBER")){
                if(data_precision == null &&  data_scale == null){
                    type = "double";
                }else if(data_precision > 9 && data_scale ==0){
                    type = "bigint";
                }else if(data_precision > 0 && data_scale > 0){
                    type = "decimal(" + data_precision + "," + data_scale + ")";
                }else if(data_precision <= 9 && data_scale ==0){
                    type = "int";
                }else if(data_precision > 0 && data_scale !=0){
                    type = "double";
                }
            }else if(data_type.equals("BLOB")||data_type.equals("LONG RAW")||data_type.equals("RAW")){
                type = "binary";
            }else if(data_type.equals("DATE") || data_type.equals("TIMESTAMP(6)")){
                type = "timestamp";
            }else if(data_type.equals("FLOAT")){
                type = "double";
            }else if(data_type.equals("CLOB")||data_type.equals("LONG")||data_type.equals("XMLTYPE")||
                    ((data_type.equals("NVARCHAR2")||data_type.equals("VARCHAR2")) && data_length > 2000)){
                type = "string";
            }else if((data_type.equals("CHAR") || data_type.equals("NVARCHAR2")||data_type.equals("VARCHAR2")) && data_length <= 2000){
                type = "varchar(" + data_length + ")";
            }else if(data_type.equals("INTEGER")){
                type = "int";
            }

            putIntoMap(typeHashMap, column_name, table_name, type);
        }
        return typeHashMap;
    }


    private static HashSet<String> getTableNames(Connection connect) throws SQLException {
        PreparedStatement preState = connect.prepareStatement(queryTableName);
        ResultSet resultSet = preState.executeQuery();
        HashSet<String> tables = new HashSet<String>();
        while (resultSet.next()) {
            String table_name = resultSet.getString("TABLE_NAME");
            if(table_name.startsWith("TB_")) {
                tables.add(table_name);
            }
        }
        return tables;
    }


}
