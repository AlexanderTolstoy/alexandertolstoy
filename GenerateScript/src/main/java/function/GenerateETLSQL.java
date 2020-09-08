package function;

import DataStructure.ResourcePool;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigCenter;
import utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CheckModule
 * Created by ccwei on 2018/10/13.
 */
public class GenerateETLSQL {

    private static Logger logger = LoggerFactory.getLogger(GenerateETLSQL.class);

    static private String titles = "line,域,表名,源表代码,列名,列代码,转换规则,目标表,目标表代码,目标列名,目标列代码,目标列数据类型,是否主键,是否分区,COL_COMMENT";
    private static int COLS = 15;

    private static String SRC_TEMPLATE = "from  _src_table_ \n_insert_statement_ ;\n\n";
    private static String DES_TEMPLATE = "    insert into table  DW_DV._des_table_(_des_attributes_)\n" +
                                         "    select _src_attributes_ \n" +
                                         "    where 1=1 _condition_ \n";


    public static void loadXLSXFile(String path,String name){
        File excelFile = new File(path);

        try {
            InputStream is = new FileInputStream(excelFile);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            Sheet sheet = xssfWorkbook.getSheetAt(0);

            HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> xlsx_tables = getXlsxInfo(sheet,"源表代码","目标表代码");
//            HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> xlsx_tables2 = getXlsxInfo(sheet,"源表代码","目标表");
//
//            HashMap<String,String> temp = getWordMapping(sheet,"表名","源表代码");
//
//            for(String key : xlsx_tables.keySet()){
//                if(xlsx_tables.get(key).size() != xlsx_tables2.get(temp.get(key)).size()){
//                    System.out.println(key + "-->" + temp.get(key));
//                }
//            }

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
        }
    }

    private static HashMap<String,String> getWordMapping(Sheet sheet,String key, String value){
        HashMap<String,String> map = new HashMap<String, String>();
        String[] keys = titles.split(",");
        boolean firstLine = true;
        for(Row row : sheet){
            if(firstLine){
                firstLine = false;
                continue;
            }
            HashMap<String,String> line = new HashMap<String, String>();
            String srcTableName = "";
            String desTableName = "";
            for(int i = 1 ; i < COLS; ++i){
                line.put(keys[i],row.getCell(i).getStringCellValue());
                if(key.equals(keys[i])){
                    srcTableName = row.getCell(i).getStringCellValue();
                }
                if(value.equals(keys[i])){
                    desTableName = row.getCell(i).getStringCellValue();
                }
            }
            map.put(srcTableName,desTableName);
        }
        return map;
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
                String _des_table_ = k;
                String _des_attributes_ = "";
                String _src_attributes_ = "";

                // TODO: handle attributes
                for(int i = 0 ; i < table.size(); ++i){
                    HashMap<String,String> line = table.get(i);
                    String src = line.get(Constants.MAP_SRC_DES).equalsIgnoreCase("NO-MAPPING") ? null : line.get(Constants.MAP_SRC_DES);
                    String des = line.get(Constants.DES_COL_CODE);
                    if(i == table.size() -1){
                        _src_attributes_ += src;
                        _des_attributes_ += des;
                    }else{
                        _src_attributes_ += src + ", ";
                        _des_attributes_ += des + ", ";
                    }
                }

                insertStatement = insertStatement.replace("_des_table_",_des_table_);
                insertStatement = insertStatement.replace("_des_attributes_",_des_attributes_);
                insertStatement = insertStatement.replace("_src_attributes_",_src_attributes_);
                insertStatements += insertStatement;
            }
            res = res.replace("_condition_","");
            res = res.replace("_insert_statement_",insertStatements);
            output.write(res.replace("#","'").toLowerCase().getBytes());
        }
    }


    /**
     *      从excel读取数据
     */
    private static HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> getXlsxInfo(Sheet sheet,String src,String des) {
        HashMap<String,HashMap<String, ArrayList<HashMap<String,String>>>> mapping = new HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
        int col_num = Tools.getColNum("ETL_ODS_DV",COLS);
        HashMap<Integer,List<String>> configMap = ConfigCenter.config.get("ETL_ODS_DV");
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
}
