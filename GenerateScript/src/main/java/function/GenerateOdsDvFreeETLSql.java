package function;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigCenter;
import utils.Constants;
import utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * CheckModule
 * Created by ccwei on 2018/11/20.
 */
public class GenerateOdsDvFreeETLSql {

    private static Logger logger = LoggerFactory.getLogger(GenerateOdsDvFreeETLSql.class);

    private static int COLS = 15;

    private static String SRC_TEMPLATE = "from  _src_table_ \n_insert_statement_ ;\n\n";
    private static String DES_TEMPLATE = "    insert into table  _schema_._des_table_(_des_attributes_)\n" +
            "    select _src_attributes_ \n" +
            "    where 1=1 _condition_ \n";


    public static void loadXLSXFile(String path,String name){
        logger.info("begin to loadXLSXFile()...");
        File excelFile = new File(path);

        try {
            InputStream is = new FileInputStream(excelFile);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            Sheet sheet = xssfWorkbook.getSheetAt(0);

            HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> xlsx_tables
                    = getXlsxInfo(sheet,Constants.SRC_TABLE_CODE,Constants.DES_TABLE_CODE);

            //输出sql文件
            File sqlOutput = new File(path.replace(name,sheet.getSheetName()));
            OutputStream output = new FileOutputStream(sqlOutput);

            //统计信息
            String staticsMsg = getStaticsMsg(xlsx_tables);
            output.write(staticsMsg.getBytes());

            //生成脚本
            generateSQL(xlsx_tables, output);

            output.flush();
            output.close();
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("failed : " + e.toString());
        }
        logger.info("exit loadXLSXFile()...");

    }



    private static void generateSQL(HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> xlsx_tables,
                                    OutputStream output) throws IOException {

        for(String key : xlsx_tables.keySet()){
            String res = SRC_TEMPLATE;
            HashMap<String, ArrayList<HashMap<String,String>>>
                    desTables = xlsx_tables.get(key);
            String _src_table_ = getSrcSchemas(key);
            _src_table_ += "." + key;
            res = res.replace("_src_table_",_src_table_);
            String insertStatements = "";
            for(String k : desTables.keySet()){
                ArrayList<HashMap<String,String>> table = desTables.get(k);
                String insertStatement = DES_TEMPLATE;
                String desTable = Utils.convert3Dimension(k);
                String _des_table_ = Utils.convertPinyin2Char(desTable,desTable);
                String _des_attributes_ = "";
                String _src_attributes_ = "";

                //增加的主键放在第一位ID_ROW_MD5
                _des_attributes_ += "ID_ROW_MD5, ";
                _src_attributes_ += getMD5Mapping(table);

                // TODO: handle attributes
                for(int i = 0 ; i < table.size(); ++i){
                    HashMap<String,String> line = table.get(i);
                    String src = line.get(Constants.SRC_COL_CODE).equalsIgnoreCase("NO-MAPPING") ? null : line.get(Constants.SRC_COL_CODE);
                    String des = line.get(Constants.DES_COL_CODE);
                    if(i == table.size() -1){
                        _src_attributes_ += src;
                        _des_attributes_ += des;
                    }else{
                        _src_attributes_ += src + ", ";
                        _des_attributes_ += des + ", ";
                    }
                }

                //特殊处理,增加标记字段
                _des_attributes_ += ", del_flag";
                _src_attributes_ += ", '0'";

                insertStatement = insertStatement.replace("_des_table_",_des_table_);
                insertStatement = insertStatement.replace("_des_attributes_",_des_attributes_);
                insertStatement = insertStatement.replace("_src_attributes_",_src_attributes_);
//                insertStatement = insertStatement.replace("_condition_",getPrimaryKeysCondition(table));
                insertStatement = insertStatement.replace("_condition_","");
                insertStatements += insertStatement;
            }
            res = res.replace("_insert_statement_",insertStatements);
            output.write(res.replace("#","'").toLowerCase().getBytes());
        }
    }


    /**
     *      从excel读取数据
     */
    private static HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> getXlsxInfo(Sheet sheet,String src,String des) {
        HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> mapping = new HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
        int col_num = Tools.getColNum("ETL_ODS_DV_FREE",COLS);
        HashMap<Integer,List<String>> configMap = ConfigCenter.config.get("ETL_ODS_DV_FREE");
        boolean firstLine = true;
        int cnt = 0;
        for(Row row : sheet){
            logger.info("scanning excel,line = " +  ++cnt);
            if(firstLine){
                firstLine = false;
                continue;
            }
            HashMap<String,String> line = new HashMap<String, String>();
            String srcTableName = "";
            String desTableName = "";
            for(int i = 1 ; i < col_num; ++i){
                for(String str : configMap.get(i+1)) {
                    line.put(str, row.getCell(i).getStringCellValue());
                    if (src.equals(str)) {
                        srcTableName = row.getCell(i).getStringCellValue();
                    }
                    if (des.equals(str)) {
                        desTableName = row.getCell(i).getStringCellValue();
                    }
                }
            }
            //存入hash表
            if(mapping.containsKey(srcTableName)){
                HashMap<String, ArrayList<HashMap<String,String>>> desTables = mapping.get(srcTableName);
                if(desTables.containsKey(desTableName)){
                    desTables.get(desTableName).add(line);
                }else{
                    ArrayList<HashMap<String,String>> table = new ArrayList<HashMap<String,String>>();
                    table.add(line);
                    desTables.put(desTableName,table);
                }
            }else{
                HashMap<String, ArrayList<HashMap<String,String>>> desTables = new HashMap<String, ArrayList<HashMap<String, String>>>();
                ArrayList<HashMap<String,String>> table = new ArrayList<HashMap<String,String>>();

                table.add(line);
                desTables.put(desTableName,table);
                mapping.put(srcTableName,desTables);
            }
        }
        return mapping;
    }

    private static String getStaticsMsg(HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> mapping) {
        HashMap<String,HashSet<String>> check = new HashMap<String, HashSet<String>>();
        for(String src : mapping.keySet()){
            HashMap<String, ArrayList<HashMap<String,String>>>
                    desTables = mapping.get(src);
            for(String des : desTables.keySet()){
                if(check.containsKey(des)){
                    check.get(des).add(src);
                }else{
                    HashSet<String> set = new HashSet<String>();
                    set.add(src);
                    check.put(des,set);
                }
            }
        }
        logger.info("table counts : " + check.keySet().size());
        for(String key : check.keySet()){
            if(check.get(key).size() > 1){
                logger.error(key + "-->" + check.get(key).toString());
            }
        }

        String msg = "--**********************统计信息**************************\n\n";
        int srcTableCount = 0;
        int desTableCount = 0;
        for(String key : mapping.keySet()){
            srcTableCount++;
            desTableCount += mapping.get(key).size();
        }
        msg += " Source Table : " + srcTableCount + "    Destination Table : " + desTableCount +"  \n";

        return msg + "--*******************************************************\n\n";

    }

    private static String getSrcSchemas(String code){
        String res = "";
        if(code.contains("_BASIS_")){
            res = "SRC_ZYZHRAC_" + "BASIS";
        }
        if(code.contains("_BUSINESS_")){
            res = "SRC_ZYZHRAC_" + "BUSINESS";
        }
        if(code.contains("_META_")){
            res = "SRC_ZYZHRAC_" + "META";
        }
        return res;
    }

    /**
     * create by: ccwei
     * create time: 8:51 2018/11/21
     * description: 获取所有主键用逗号分隔
     * @return
     */
    private static String getMD5Mapping(ArrayList<HashMap<String,String>> table){
        String res = "";
        boolean isFirst = true;
        for(int i = 0 ; i < table.size(); ++i){
            HashMap<String,String> hashMap = table.get(i);
            String isPrimary = hashMap.get(Constants.DES_IS_PRIMARY);
            if(isPrimary.equals("1")){
                if(isFirst){
                    isFirst = false;
                    res += "cast(" + hashMap.get(Constants.SRC_COL_CODE) + " as String)";
                }else {
                    res += ", cast(" + hashMap.get(Constants.SRC_COL_CODE) + " as String)";
                }
            }
        }

        return isFirst ? "uuid(), " : (res.contains(",")? "md5(concat_ws(','," + res + ")), " : "md5(" + res + "), ");
    }

    /**
     * create by: ccwei
     * create time: 8:56 2018/11/21
     * description: 获取所有主键构成where的条件
     * @return
     */
    private static String getPrimaryKeysCondition(ArrayList<HashMap<String,String>> table){
        String res = "";
        for(int i = 0 ; i < table.size(); ++i){
            HashMap<String,String> hashMap = table.get(i);
            String isPrimary = hashMap.get(Constants.DES_IS_PRIMARY);
            if(isPrimary.equals("1")){
                res += "and " + hashMap.get(Constants.SRC_COL_CODE) + " is not null ";
            }
        }

        return res;
    }

}
