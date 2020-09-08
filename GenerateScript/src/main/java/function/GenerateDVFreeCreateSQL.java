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
import java.util.*;

/**
 * CheckModule
 * Created by ccwei on 2018/11/16.
 */
public class GenerateDVFreeCreateSQL {

    private static Logger logger = LoggerFactory.getLogger(GenerateDVFreeCreateSQL.class);

    private static boolean ADD_EXCEPTION = false;
    private static boolean NEED_TYPE_CONVERSION = true;
    private static boolean CREATE_INTERNAL_TABLE = false;
    private static boolean TEMP_PERMISSION = true;

    private static int COLNUM = 7;
    private static String ss = "line,目标表,目标表代码,目标列代码,COL_COMMENT,COLUMN_ID,目标列数据类型";

    private static String S_SUB_TABLE = "DROP TABLE IF EXISTS _sch_pre___sch_sub_._sub_table_;\n" +
            "CREATE _table_type_ TABLE IF NOT EXISTS _sch_pre___sch_sub_._sub_table_\n" +
            "( \n" +
            "_attributes_ \n"+
            ")\n" +
            "COMMENT _comment_ \n" +
            "ROW FORMAT DELIMITED \n" +
            "FIELDS TERMINATED BY ','\n" +
            "COLLECTION ITEMS TERMINATED BY '|'\n" +
            "MAP KEYS TERMINATED BY ':'\n" +
            "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'  \n" +
            "WITH SERDEPROPERTIES ( \n" +
            "\"hbase.columns.mapping\" = \":key,\n" +
            "_HBase_attr_"+
            "\n" +
            "\" \n" +
            ") \n" +
            "TBLPROPERTIES (\"hbase.table.name\" = \"_sch_pre___sch_sub_._father_table_\", \"hbase.mapred.output.outputtable\" = \"_sch_pre___sch_sub_._father_table_\"); \n\n";

    public static void loadXLSXFile(String path,String name){
        File excelFile = new File(path);

        try {
            InputStream is = new FileInputStream(excelFile);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            Sheet sheet = xssfWorkbook.getSheetAt(0);

            HashMap<String, ArrayList<HashMap<String,String>>> xlsx_tables = getXlsxMsg(sheet);


            HashMap<String, HashSet<String>> hub2Sat = getHub2SatMapping(xlsx_tables);

//            handlePrimaryKey(xlsx_tables);
            addPrimaryKey(xlsx_tables);

            for(String key : xlsx_tables.keySet()) {
                ArrayList<HashMap<String, String>> list = xlsx_tables.get(key);
                sortTableAttributes(list);

            }
            //输出sql文件
            File sqlOutput = new File(path.replace(name,sheet.getSheetName()));
            OutputStream output = new FileOutputStream(sqlOutput);

            //统计信息
            String staticsMsg = getStaticsMsg(xlsx_tables,hub2Sat);
            output.write(staticsMsg.getBytes());

            Set<String> names = new HashSet<String>(xlsx_tables.keySet());
            for(String tableName : xlsx_tables.keySet()){

                output.write(getCommonSQL(xlsx_tables,tableName).getBytes());
                names.remove(tableName);
            }

            String end = "";
            for(String n : names){
                end += n + "\n";
            }

            String msg = "table : \n" + end + " are not converted !\n";
            if(!end.equals("")) {
                output.write(msg.getBytes());
            }
            output.flush();
            output.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void sortTableAttributes(ArrayList<HashMap<String, String>> list) {
        for(int i = list.size()-1 ; i > 0; i--){
            for(int j = 0;j < i; j++){
                    if(isGE(list.get(j),list.get(j+1))){
                        HashMap<String, String> tmp = list.get(j);
                        list.set(j,list.get(j+1));
                        list.set(j+1,tmp);
                    }
            }
        }
    }

    private static void addPrimaryKey(HashMap<String, ArrayList<HashMap<String, String>>> xlsx_tables) {
        //处理hbase表映射rowkey
        for(String key : xlsx_tables.keySet()) {
            ArrayList<HashMap<String,String>> list = xlsx_tables.get(key);
            HashMap<String, String> m = list.get(0);
            HashMap<String, String> t = new HashMap<String, String>();
            t.put(Constants.DES_TABLE_NAME, m.get(Constants.DES_TABLE_NAME));
            t.put(Constants.DES_TABLE_CODE, m.get(Constants.DES_TABLE_CODE));
            t.put(Constants.DES_SCHEMA_POSTFIX, m.get(Constants.DES_SCHEMA_POSTFIX));
            t.put(Constants.DES_TABLE_COMMENT, m.get(Constants.DES_TABLE_COMMENT));
            t.put(Constants.DES_COL_CODE, "ID_ROW_MD5");
            t.put(Constants.DES_COL_TYPE, "varchar(64)");
            t.put(Constants.DES_COL_COMMENT, "代理键");
            t.put(Constants.DES_IS_PRIMARY, "1");
            list.add(t);
        }
    }

    private static void handlePrimaryKey(HashMap<String, ArrayList<HashMap<String, String>>> xlsx_tables) {
        //处理hbase表映射rowkey
        for(String key : xlsx_tables.keySet()){
            ArrayList<HashMap<String,String>> list = xlsx_tables.get(key);
            ArrayList<HashMap<String,String>> plist = new ArrayList<HashMap<String, String>>();
            int cnt = 0;
            for(HashMap<String,String> m : list){
                if(m.get(Constants.DES_IS_PRIMARY).equals("1")){
                    ++cnt;
                }
            }
            //找出所有主键
            for(int i = 0 ; cnt > 1 && i < cnt; ++i){
                for(int j = 0; j < list.size();++j){
                    HashMap<String,String> m = list.get(j);
                    if(m.get(Constants.DES_IS_PRIMARY).equals("1")){
                        plist.add(m);
                        list.remove(m);
                        break;
                    }
                }
            }
            //主键列排序
            sortTableAttributes(plist);

            //插入主键
            if(cnt > 1){
                HashMap<String,String> t = new HashMap<String, String>();
                String pType = "";
                String pKey = "";
                String pComment = "";
                for(HashMap<String,String> m : plist){
                    String t1 = m.get(Constants.DES_COL_CODE) + ":" + m.get(Constants.DES_COL_TYPE) + " COMMENT '" + m.get(Constants.DES_COL_COMMENT) + "'";
                    t.put(Constants.DES_TABLE_NAME,m.get(Constants.DES_TABLE_NAME));
                    t.put(Constants.DES_TABLE_CODE,m.get(Constants.DES_TABLE_CODE));
                    t.put(Constants.DES_SCHEMA_POSTFIX,m.get(Constants.DES_SCHEMA_POSTFIX));
                    t.put(Constants.DES_TABLE_COMMENT,m.get(Constants.DES_TABLE_COMMENT));
                    t.put(Constants.DES_IS_PRIMARY,"1");
                    if(m.get(Constants.DES_IS_PRIMARY).equals("1")) {
                        pType += pType.equals("") ? t1 : ", " + t1;
                        pKey += pKey.equals("") ? m.get(Constants.DES_COL_CODE) : "_" + m.get(Constants.DES_COL_CODE);
                        pComment += pComment.equals("") ? m.get(Constants.DES_COL_COMMENT) : "_" + m.get(Constants.DES_COL_COMMENT);
                    }
                }
                pType = "struct<" + pType + ">";
                t.put(Constants.DES_COL_CODE,pKey);
                t.put(Constants.DES_COL_TYPE,pType);
                t.put(Constants.DES_COL_COMMENT,pComment);
                list.add(t);
            }

        }
    }

    private static HashMap<String, HashSet<String>> getHub2SatMapping(HashMap<String, ArrayList<HashMap<String, String>>> xlsx_tables) {
        HashMap<String, HashSet<String>> hub2Sat = new HashMap<String, HashSet<String>>();
        String warming = "";
        HashMap<String,String> hub2key = new HashMap<String, String>();
        for(String key : xlsx_tables.keySet()){
            ArrayList<HashMap<String,String>> table = xlsx_tables.get(key);
            if(!key.startsWith("H_") && !key.startsWith("L_")){
                continue;
            }
            for(HashMap<String,String> line : table){
                String colCode = line.get(Constants.DES_COL_CODE);
                if(colCode.startsWith("SK_H_")){
                    if(hub2key.containsKey(colCode)) {
                        System.out.println("duplicated:" + colCode);
                        warming +="duplicated:" + colCode + "\n";
                    }
                    hub2key.put(colCode,key);
                    break;
                }
                if(colCode.startsWith("SK_L_")){
                    if(hub2key.containsKey(colCode)) {
                        System.out.println("duplicated:" + colCode);
                        warming +="duplicated:" + colCode + "\n";
                    }
                    hub2key.put(colCode,key);
                    break;
                }
            }
        }

        for(String key : xlsx_tables.keySet()){
            if(hub2key.containsValue(key)){
                HashSet<String> set = new HashSet<String>();
                hub2Sat.put(key,set);
            }
        }


        for(String key : xlsx_tables.keySet()){
            ArrayList<HashMap<String,String>> table = xlsx_tables.get(key);
            if(!key.startsWith("S_")){
                continue;
            }
            for(HashMap<String,String> line : table){
                String colCode = line.get(Constants.DES_COL_CODE);
                if((colCode.startsWith("SK_H_")||colCode.startsWith("SK_L_")) && hub2key.containsKey(colCode)){
                    String tblName = hub2key.get(colCode);
                    String satTblName = line.get(Constants.DES_TABLE_NAME);
                    if(hub2Sat.containsKey(tblName)){
                        hub2Sat.get(tblName).add(satTblName);
                    }
                    break;
                }
            }
        }

        /** *************/

        return hub2Sat;
    }

    private static String getSchemaPrefix(ArrayList<HashMap<String,String>> table,String defaultValue) {
        String tblComment = "";
        for(HashMap<String, String> tmp : table){
            tblComment = tmp.get(Constants.DES_SCHEMA_POSTFIX);
            break;
        }
        tblComment = tblComment == null ? defaultValue : tblComment;
        return Utils.convertPinyin2Char(tblComment,tblComment);
    }

    private static String getTableComment(ArrayList<HashMap<String,String>> table,String defaultValue) {
        String tblComment = "";
        for(HashMap<String, String> tmp : table){
            tblComment = tmp.get(Constants.DES_TABLE_COMMENT);
            break;
        }
        return tblComment == null ? defaultValue : tblComment;
    }

    private static String getTableCode(ArrayList<HashMap<String,String>> table) {
        String tblCode = "";
        for(HashMap<String, String> tmp : table){
            tblCode = tmp.get(Constants.DES_TABLE_CODE);
            break;
        }
        if(!tblCode.equals(Utils.convertPinyin2Char(tblCode,tblCode))){
            tblCode = Utils.convert3Dimension(tblCode);
        }
        return Utils.convertPinyin2Char(tblCode,tblCode);
    }


    /**
     *      从excel读取数据
     */
    private static HashMap<String, ArrayList<HashMap<String,String>>> getXlsxMsg(Sheet sheet) {
        HashMap<String, ArrayList<HashMap<String,String>>> tables = new HashMap<String, ArrayList<HashMap<String, String>>>();
        int col_num = getColNum();
        boolean firstLine = true;
        int cnt = 1;
        HashMap<Integer,List<String>> configMap = ConfigCenter.config.get("DV_TABLE_CREATION");
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
                res = (row.getCell(i) == null ? "" : row.getCell(i).getStringCellValue()).trim();
                //System.out.println(cnt + "," + i);
                logger.info(cnt + "," + i);

                for(String str : configMap.get(i+1)){
                    line.put(str,res);

                    if(str.equals(Constants.DES_COL_TYPE)){
                        line.put(str,NEED_TYPE_CONVERSION ? Utils.convertTypeOrcl2Hive(res):res);
                    }else {
                        line.put(str,res);
                    }
                    if(Constants.DES_TABLE_CODE.equals(str) && tableName.length() == 0){
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
        if(ConfigCenter.config.get("DV_TABLE_CREATION") != null && ConfigCenter.config.get("DV_TABLE_CREATION").get(Constants.INDEX_COLNUM) != null){
            return Integer.parseInt(ConfigCenter.config.get("DV_TABLE_CREATION").get(Constants.INDEX_COLNUM).get(0));
        }
        return COLNUM;
    }

    /**
     *   获取拼音转的表名
     * @param input
     * @return
     */

    private static String getTblCodeOrNameOnly(String input){

        String[] strs = input.split("_");
        String res = "";
        //

        //
        if(strs.length < 4){
            return input;
        }
        for(int i = 0;i < strs.length - 3; i++ ){
            if(i==0 && (strs[i].equals("H")||strs[i].equals("HL")||strs[i].equals("HS")||strs[i].equals("L")||strs[i].equals("S"))){
                continue;
            }
            res += res.equals("") ? strs[i] : "_" + strs[i];
        }
        return Utils.convertPinyin2Char(res,res);
    }



    private static String getTableName(ArrayList<HashMap<String,String>> table_S) {
        String tblName = "";
        for(HashMap<String, String> tmp : table_S){
            tblName = tmp.get(Constants.DES_TABLE_NAME);
            break;
        }
        return getTblCodeOrNameOnly(tblName);
    }

    /**
     *      L_ H_ 表的Hive表脚本生成
     */
    private static String getCommonSQL(HashMap<String, ArrayList<HashMap<String, String>>> tables, String tableName) {
        String prefix = "--------------------------- " + tableName + " -------------------------------\n";
        ArrayList<HashMap<String, String>> table = tables.get(tableName);
        String res = S_SUB_TABLE;

        //替换模式后缀
        String schemaPrefix = getSchemaPrefix(table,"_sch_sub_");
        res=res.replace("_sch_sub_",schemaPrefix);

        //替换hive表名
        String hiveTable = getTableCode(table);
        res=res.replace("_sub_table_",hiveTable);

        //替换HBase表名
        String hbaseTable = getTableCode(table);
        res = res.replace("_father_table_",hbaseTable);

        //替换表注释
        String tblComment = getTableComment(table,tableName);
        res = res.replace("_comment_","'"+tblComment+"'");

        //替换Hive表属性
        String _attributes_ = "";
        for(HashMap<String, String> line : table){
            if(!_attributes_.equals("")){
                _attributes_ +=",\n";
            }
            _attributes_ += line.get(Constants.DES_COL_CODE) + "   " + line.get(Constants.DES_COL_TYPE) + "   COMMENT '" + line.get(Constants.DES_COL_COMMENT) + "'";
        }
        if (TEMP_PERMISSION) {
            //增加一个标记字段
            _attributes_ +=",\n";
            _attributes_ += "DEL_FLAG" + "   " + "varchar(2)" + "   COMMENT '标记是否删除字段，1标记删除，0标记有效'";
        }

        res = res.replace("_attributes_",_attributes_);

        //替换HBase表属性
        String _HBase_attr_ = "";
        HashSet<String> cfNames = new HashSet<String>();
        String cfName = "";
        for(int i = 1 ; i < table.size();i++){
            HashMap<String, String> line = table.get(i);
            if(!_HBase_attr_.equals("")){
                _HBase_attr_ +=",\n";
            }
            cfName = getTblCodeOrNameOnly(line.get(Constants.DES_TABLE_CODE));
            cfNames.add(cfName);
            _HBase_attr_ += cfName + ":" +line.get(Constants.DES_COL_CODE) ;
        }
        if (TEMP_PERMISSION) {
            //增加一个标记字段
            _HBase_attr_ +=",\n";
            _HBase_attr_ += cfName + ":DEL_FLAG";
        }

        res = res.replace("_HBase_attr_",_HBase_attr_);

        String hbCreate = getHbaseCreateSQL(hbaseTable, cfNames);
        hbCreate = hbCreate.replace("_sch_sub_",schemaPrefix);
        String _table_type_ = CREATE_INTERNAL_TABLE ? "" : "external";
        res = res.replace("_table_type_",_table_type_);


        return  prefix + (CREATE_INTERNAL_TABLE ? "" : hbCreate ) + res + "--------------------------- END -------------------------------\n\n\n";

    }

    private static String getHbaseCreateSQL(String tblCode_hb, Set<String> cfNames) {
        String hbCreate = "create \"_sch_pre___sch_sub_.";
        hbCreate += tblCode_hb + "\"";
        for(String s : cfNames){
            hbCreate += ",\"" + s +"\"";
        }
        hbCreate += "\n\n";
        return hbCreate;
    }

    private static String getStaticsMsg(HashMap<String, ArrayList<HashMap<String,String>>> xlsx_tables, HashMap<String, HashSet<String>> hub2Sat){
        String msg = "--**********************统计信息**************************\n";
        int hub = 0;
        int sat = 0;
        int link = 0;
        int other = 0;
        ArrayList<String> m1 = new ArrayList<String>();

        for(String key : xlsx_tables.keySet()){
            if(key.startsWith("H_")){
                hub++;
            }
            else if(key.startsWith("S_")){
                sat++;
            }
            else if(key.startsWith("L_")){
                link++;
            }else {
                other++;
                m1.add(key);
            }
        }
        msg += "                    excel中信息                \n";
        msg += "    Hub: "+hub+"    Link: "+link+"    Sat: "+sat+"  \n\n";
        msg += "    Other: " + other + " --> \n";

        for(String s : m1){
            msg+= "    " + s + "\n";
        }

        msg += "\n\n                    从excel/model中读取的S关联信息                     \n";
        hub = 0;
        sat = 0 ;
        link = 0;
        int SLink = 0;
        int SHub = 0;
        for(String key : hub2Sat.keySet()){
            if(key.startsWith("H_")){
                hub++;
                if(hub2Sat.get(key).size() > 0 )
                {
                    SHub++;
                }
            }
            if(key.startsWith("L_")){
                link++;
                if(hub2Sat.get(key).size() > 0 )
                {
                    SLink++;
                }
            }
            sat += hub2Sat.get(key).size();
        }

        msg += "    Hub: "+hub+"    SHub: "+SHub+"    Link: "+link+"    SLink: "+SLink+"    Sat: "+sat+"  \n\n";

        msg += "\n\n                    输出表数量                     \n";
        msg += "    Hbase表(Hub+Link+Other+SLink+SHub): "+(hub+link+other+SLink+SHub)+"    Hive表(Hbase表+Sat): "+(sat+hub+link+other+SLink+SHub)+"  \n\n";

        msg += "--********************** END **************************\n";

        for(String key : xlsx_tables.keySet()) {
            ArrayList<HashMap<String,String>> table = xlsx_tables.get(key);
            boolean isContain = false;
            String name = "";
            for(HashMap<String,String> line :table){
                name = line.get(Constants.DES_TABLE_NAME);
                if(line.get(Constants.DES_COL_CODE).contains("LOG_")){
                    isContain = true;
                    break;
                }
            }
            if(!isContain){
                System.out.println(name);
            }
        }

        return msg;
    }
    
    /**
     * create by: ccwei
     * create time: 11:32 2018/11/16
     * description: 分级，
     * @return 
     */

    private static int classify(HashMap<String, String> m){
        if(m.get(Constants.DES_COL_CODE).contains("ROW_MD5") && m.get(Constants.DES_IS_PRIMARY).equals("1")) {
            return 0;
        }
        if(m.get(Constants.DES_IS_PRIMARY).equals("1")) {
            return 1;
        }
        if(m.get(Constants.DES_COL_CODE).startsWith("SK_")){
            return 2;
        }
        if(m.get(Constants.DES_COL_CODE).startsWith("BK_")){
            return 3;
        }
        if(m.get(Constants.DES_COL_CODE).startsWith("LOG_")){
            return 5;
        }
        if(m.get(Constants.DES_COL_CODE).startsWith("DEL_")){
            return 6;
        }
        //普通列
        return 4;

    }
    private static boolean isGE(HashMap<String, String> a,HashMap<String, String> b){
        if(classify(a) != classify(b)){
            return classify(a) > classify(b);
        }else {
            return compareString(a.get(Constants.DES_COL_CODE),b.get(Constants.DES_COL_CODE)) == 1;
        }
    }

    /**
     * create by: ccwei
     * create time: 12:01 2018/11/16
     * description: null < (length = 0) < (length > 0)
     * @return
     */
    private static int compareString(String a,String b){
        if(a == null){
            return b == null ? 0 : -1;
        }
        if(a.length() == 0){
            if(b == null ) return -1;
            if(b.length() == 0) return 0;
            return 1;
        }

        for(int i = 0 ; i < a.length(); ++i){
            if(i>b.length()-1){
                return 1;
            }
            if(a.charAt(i) != b.charAt(i)){
                return a.charAt(i) > b.charAt(i) ? 1 : -1;
            }
        }
        return 0;
    }
}
