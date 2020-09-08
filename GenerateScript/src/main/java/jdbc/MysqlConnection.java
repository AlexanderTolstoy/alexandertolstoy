package jdbc;

import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.HashMap;

/**
 * CheckModule
 * Created by ccwei on 2018/11/26.
 */
public class MysqlConnection {
    private static Logger logger = LoggerFactory.getLogger(MysqlConnection.class);

    private static String statement1 = "SELECT \n" +
            "  t.DB_ID,\n" +
            "  t.DESC,\n" +
            "  t.NAME \n" +
            "FROM\n" +
            " hive.DBS t \n" +
           // "WHERE  t.NAME  IN ('dm_tocc','dm_mst ','dm_zyml','dm_dlhygl','dm_gljsgl','dm_glyhyxgl','dm_hsgl','dm_jnhbgl','dm_aqyjgl','dm_kjgl','dm_sygl','dm_zcfggl','dm_zljdgl')";
//            "WHERE  t.NAME  IN ('dw_dv','dw_free','meta')";
            "WHERE  t.NAME  IN ('src_zyzhrac_zcfg','src_zyzhrac_yxjc','src_zyzhrac_gzcx','src_zyzhrac_meta','src_zyzhrac_zhfx','src_zyzhrac_business','src_zyzhrac_basis')";

    private static String statement2 = "SELECT  TABLE_ID,  TABLE_CODE, TABLE_NAME\n" +
            "FROM hive.VW_TABLES\n" +
            "WHERE DATABASE_ID=_db_id_;";
    private static String statement3 = "SELECT  COL_CODE, COL_NAME, DATA_TYPE\n" +
            "FROM hive.VW_COLUMNS\n" +
            "WHERE TABLE_ID=_table_id_";


    @Test
    public void init(){
        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{


            Class.forName("com.mysql.jdbc.Driver");

            connect = DriverManager.getConnection("jdbc:mysql://10.147.113.11:3306/hive","hive","hive_123#$");


            XWPFDocument doc = new XWPFDocument();


            System.out.println(connect);
            HashMap<String,HashMap<String,String>> db_mapping = new HashMap<String, HashMap<String, String>>();

            getDB_ID(connect, db_mapping);

            for(String key : db_mapping.keySet()){
                PreparedStatement preState = connect.prepareStatement(statement2.replace("_db_id_",key));
                String db_name = db_mapping.get(key).get("NAME");
                String db_desc = db_mapping.get(key).get("DESC");
                XWPFParagraph p1 = doc.createParagraph();
                XWPFRun r1 = p1.createRun();
                r1.setText("database==" + db_name + ":" + db_desc);
                ResultSet result = preState.executeQuery();

                XWPFTable table_tbl= doc.createTable(1, 2);//创建一个表
                table_tbl.setWidth(9000);
                table_tbl.getRow(0).getCell(0).setText("表代码");
                table_tbl.getRow(0).getCell(0).setColor("AAAAAA");

                table_tbl.getRow(0).getCell(1).setText("表名称");
                table_tbl.getRow(0).getCell(1).setColor("AAAAAA");


                while (result.next()) {

                    String table_id = result.getString("TABLE_ID");
                    String table_name = result.getString("TABLE_NAME");
                    String table_code = result.getString("TABLE_CODE");
                    String table_title = "Table==  " + table_code + "  :  " + table_name;
                    PreparedStatement preState2 = connect.prepareStatement(statement3.replace("_table_id_",table_id));
                    ResultSet result2 = preState2.executeQuery();

                    XWPFTableRow row_tbl = table_tbl.createRow();
                    row_tbl.getCell(0).setText(table_code);
                    row_tbl.getCell(1).setText(table_name);


                    XWPFParagraph p = doc.createParagraph();
                    XWPFRun r = p.createRun();
                    r.setText(table_title);
                    XWPFTable table_col= doc.createTable(1, 3);//创建一个表
                    table_col.setWidth(8000);
                    table_col.getRow(0).getCell(0).setText("字段代码");
                    table_col.getRow(0).getCell(0).setColor("AAAAAA");
                    table_col.getRow(0).getCell(1).setText("字段名称");
                    table_col.getRow(0).getCell(1).setColor("AAAAAA");
                    table_col.getRow(0).getCell(2).setText("字段类型");
                    table_col.getRow(0).getCell(2).setColor("AAAAAA");

                    while (result2.next()) {
                        String COL_CODE = result2.getString("COL_CODE");
                        String COL_NAME = result2.getString("COL_NAME");
                        String  DATA_TYPE = result2.getString("DATA_TYPE");
                        XWPFTableRow row = table_col.createRow();
                        row.getCell(0).setText(COL_CODE);
                        row.getCell(1).setText(COL_NAME);
                        row.getCell(2).setText(DATA_TYPE);

                    }


                }
            }
            FileOutputStream out = new FileOutputStream("src/sample.doc");
            doc.write(out);
            out.close();

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

    private void getDB_ID(Connection connect, HashMap<String, HashMap<String, String>> db_mapping) throws SQLException {
        PreparedStatement preState = connect.prepareStatement(statement1);
        ResultSet result = preState.executeQuery();
        while (result.next()) {
            HashMap<String,String> m = new HashMap<String, String>();
            m.put("DESC",result.getString("DESC"));
            m.put("NAME",result.getString("NAME"));
            db_mapping.put(result.getString("DB_ID"),m);
        }
    }
}
