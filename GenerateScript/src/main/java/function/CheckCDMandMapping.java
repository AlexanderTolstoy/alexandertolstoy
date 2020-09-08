package function;

import DataStructure.Entity;
import DataStructure.ResourcePool;
import OutputStructure.Mapping;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import utils.ConfigCenter;
import utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * CheckModule
 * Created by ccwei on 2018/11/16.
 */
public class CheckCDMandMapping {
    private static Logger logger = Logger.getLogger(CheckCDMandMapping.class.getClass());

    private static int COLNUM = 7;

    @Test
    public static void loadXLSXFile(String path,String name){
        File excelFile = new File(path);

        try {
            InputStream is = new FileInputStream(excelFile);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            Sheet sheet = xssfWorkbook.getSheetAt(0);

            HashMap<String, ArrayList<HashMap<String,String>>> xlsx_tables = getXlsxMsg(sheet);


            HashMap<String,HashSet<String>> excel_tbl_map = new HashMap<String,HashSet<String>>();
            for(String key : xlsx_tables.keySet()) {
                ArrayList<HashMap<String, String>> list = xlsx_tables.get(key);
                String src = list.get(0).get(Constants.SRC_TABLE_CODE);
                if(excel_tbl_map.containsKey(src)){
                    excel_tbl_map.get(src).add(key);
                }else {
                    HashSet<String> set = new HashSet<String>();
                    set.add(key);
                    excel_tbl_map.put(src,set);
                }
            }
            logger.debug("excel read finish!");

            //CDM中的映射关系
            HashMap<String, ArrayList<Mapping>> mappings = ResourcePool.getDiagramMappingMaps();
            HashMap<String,HashSet<String>> cdm_tbl_map = new HashMap<String,HashSet<String>>();
            for(String key : mappings.keySet()){
                logger.debug("cdm : handle a diagram!");
                ArrayList<Mapping> ms = mappings.get(key);
                for(Mapping m : ms){
                    String src = m.getSrcTable();
                    Entity entitySrc = ResourcePool.getEntitiy(src);
                    HashSet<String> desSet = new HashSet<String>();
                    for(String des : m.getDesTable()){
                        Entity entityDes = ResourcePool.getEntitiy(des);
                        desSet.add(entityDes.getCode());
                    }
                    String srcCode = entitySrc.getCode();
                    if(cdm_tbl_map.containsKey(srcCode)){
                        cdm_tbl_map.get(srcCode).addAll(desSet);
                    }else {
                        cdm_tbl_map.put(srcCode,desSet);
                    }
                }
            }


            //输出sql文件
            File sqlOutput = new File(path.replace(name,sheet.getSheetName()));
            OutputStream output = new FileOutputStream(sqlOutput);

            output.flush();
            output.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private static HashMap<String, ArrayList<HashMap<String,String>>> getXlsxMsg(Sheet sheet) {
        HashMap<String, ArrayList<HashMap<String,String>>> tables = new HashMap<String, ArrayList<HashMap<String, String>>>();
        int col_num = getColNum();
        boolean firstLine = true;
        int cnt = 1;
        HashMap<Integer,List<String>> configMap = ConfigCenter.config.get("CHECK_CDM_EXCEL");
        for(Row row : sheet){
            if(firstLine){
                firstLine = false;
                continue;
            }
            HashMap<String,String> line = new HashMap<String, String>();
            String tableName = "";
            for(int i = 0 ; i < col_num; ++i){
                if(!configMap.keySet().contains(i+1)){
                    continue;
                }
                String res = "";
                res = row.getCell(i) == null ? "" : row.getCell(i).getStringCellValue();
                System.out.println(cnt + "," + i);

                for(String key : configMap.get(i+1)) {

                    line.put(key, res);

//                if(Constants.DES_TABLE_NAME.equals(key)){
//                    tableName = row.getCell(i) == null ? "" : row.getCell(i).getStringCellValue();
//                }
                    if (Constants.DES_TABLE_CODE.equals(key) && tableName.length() == 0) {
                        tableName = row.getCell(i) == null ? "" : row.getCell(i).getStringCellValue();
                    }
                }

            }
            ++cnt;
            //存入hash表
            if(tables.containsKey(tableName)){
                tables.get(tableName).add(line);
            }else{
                ArrayList<HashMap<String,String>> table = new ArrayList<HashMap<String,String>>();
                table.add(line);
                tables.put(tableName,table);
            }
        }
        return tables;
    }

    private static int getColNum() {
        if(ConfigCenter.config.get("CHECK_CDM_EXCEL") != null && ConfigCenter.config.get("CHECK_CDM_EXCEL").get(Constants.INDEX_COLNUM) != null){
            return Integer.parseInt(ConfigCenter.config.get("CHECK_CDM_EXCEL").get(Constants.INDEX_COLNUM).get(0));
        }
        return COLNUM;
    }


}
