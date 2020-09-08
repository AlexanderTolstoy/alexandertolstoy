package utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * CheckModule
 * Created by ccwei on 2018/11/14.
 */
public class ConfigCenter {

    private static Logger logger = LoggerFactory.getLogger(ConfigCenter.class);

    public static HashMap<String,String> property = new HashMap<String, String>();
    public static HashMap<String,HashMap<Integer,List<String>>> config = new HashMap<String, HashMap<Integer, List<String>>>();

    public static void initProperties(){
        Properties pps = new Properties();
        try{
            InputStream in = new BufferedInputStream(new FileInputStream("config/configuration.properties"));
            pps.load(in);
            Enumeration en = pps.propertyNames(); //得到配置文件的名字

            while(en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String value = pps.getProperty(key);
                property.put(key,value);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

    }

    @Test
    public static void initXmlConfig(){
        try{
            // 创建saxReader对象
            SAXReader reader = new SAXReader();
            // 通过read方法读取一个文件 转换成Document对象
            Document document = reader.read(new File("config/config.xml"));
            //获取根节点元素对象
            Element node = document.getRootElement();
            //遍历所有的元素节点
            scanNodes(node);
            System.out.println("success!");
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

    }

    public static void scanNodes( Element node) {

        // 当前节点下面子节点迭代器
        Iterator<Element> it = node.elementIterator();

        // 遍历
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("DV_TABLE_CREATION")){
                config.put("DV_TABLE_CREATION",getDVCreateConfig(e));
            }
            if(e.getName().equals("CHECK_CDM_EXCEL")){
                config.put("CHECK_CDM_EXCEL",getCHECK_CDM_EXCELConfig(e));
            }
            if(e.getName().equals("DM_TABLE_CREATION")){
                config.put("DM_TABLE_CREATION",getCHECK_CDM_EXCELConfig(e));
            }
            if(e.getName().equals("ETL_ODS_DV")){
                config.put("ETL_ODS_DV",getETL_ODS_DVConfig(e));
            }
            if(e.getName().equals("ETL_ODS_DV_FREE")){
                config.put("ETL_ODS_DV_FREE",getETL_ODS_DV_FREEConfig(e));
            }

        }
    }

    private static HashMap<Integer,List<String>> getDVCreateConfig(Element root) {
        HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        Iterator<Element> it = root.elementIterator();
        while(it.hasNext()){
            Element e = it.next();
            if(e.getName().equals("COL_NUM")){
                putIntoMap(map, Constants.INDEX_COLNUM, getColNum(e) + "");
            }
            if(e.getName().equals("DES_SCHEMA_PREFIX")){
                putIntoMap(map, Constants.INDEX_SCHEMA, e.attribute("value").getValue());
            }
            if(e.getName().equals("DES_COL_COMMENT")){
                putIntoMap(map, getColNum(e), Constants.DES_COL_COMMENT);

            }
            if(e.getName().equals("DES_TABLE_CODE")){
                putIntoMap(map, getColNum(e),Constants.DES_TABLE_CODE);
            }
            if(e.getName().equals("DES_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_NAME);
            }
            if(e.getName().equals("DES_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_CODE);
            }
            if(e.getName().equals("DES_COL_TYPE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_TYPE);
            }
            if(e.getName().equals("DES_IS_PRIMARY")){
                putIntoMap(map,getColNum(e),Constants.DES_IS_PRIMARY);
            }
            if(e.getName().equals("DES_TABLE_COMMENT")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_COMMENT);
            }
            if(e.getName().equals("DES_SCHEMA_POSTFIX")){
                putIntoMap(map,getColNum(e),Constants.DES_SCHEMA_POSTFIX);
            }
        }
        return map;
    }

    private static void putIntoMap(HashMap<Integer, List<String>> map, int colNum, String colName) {
        if(map.get(colNum) == null){
            List<String> list = new ArrayList<String>();
            list.add(colName);
            map.put(colNum,list);
        }else{
            map.get(colNum).add(colName);
        }
    }

    /**
     * create by: ccwei
     * create time: 16:21 2018/11/20
     * description: 获取Excel映射
     * @return line,域,表名,源表代码,列名,列代码,转换规则,目标表,目标表代码,目标列名,目标列代码,目标列数据类型,COL_COMMENT
     */
    private static HashMap<Integer, List<String>> getETL_ODS_DVConfig(Element root) {
        HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        Iterator<Element> it = root.elementIterator();
        while(it.hasNext()){
            Element e = it.next();
            if(e.getName().equals("COL_NUM")){
                putIntoMap(map,Constants.INDEX_COLNUM,getColNum(e)+"");
            }
            if(e.getName().equals("SRC_DOMAIN")){
                putIntoMap(map,getColNum(e),Constants.SRC_DOMAIN);
            }
            if(e.getName().equals("SRC_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.SRC_TABLE_NAME);
            }
            if(e.getName().equals("SRC_TABLE_CODE")){
                putIntoMap(map,getColNum(e),Constants.SRC_TABLE_CODE);
            }
            if(e.getName().equals("SRC_COL_NAME")){
                putIntoMap(map,getColNum(e),Constants.SRC_COL_NAME);
            }
            if(e.getName().equals("SRC_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.SRC_COL_CODE);
            }

            if(e.getName().equals("MAP_SRC_DES")){
                putIntoMap(map,getColNum(e),Constants.MAP_SRC_DES);
            }
            if(e.getName().equals("DES_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_NAME);
            }
            if(e.getName().equals("DES_TABLE_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_CODE);
            }
            if(e.getName().equals("DES_COL_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_NAME);
            }
            if(e.getName().equals("DES_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_CODE);
            }
            if(e.getName().equals("DES_COL_TYPE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_TYPE);
            }
            if(e.getName().equals("DES_COL_COMMENT")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_COMMENT);
            }

        }
        return map;
    }
    /**
     * create by: ccwei
     * create time: 16:21 2018/11/20
     * description: 获取Excel映射
     * @return line,域,表名,源表代码,列名,列代码,转换规则,目标表,目标表代码,目标列名,目标列代码,目标列数据类型,COL_COMMENT
     */
    private static HashMap<Integer,List<String>> getETL_ODS_DV_FREEConfig(Element root) {
        HashMap<Integer,List<String>> map = new HashMap<Integer, List<String>>();
        Iterator<Element> it = root.elementIterator();
        while(it.hasNext()){
            Element e = it.next();
            if(e.getName().equals("COL_NUM")){
                putIntoMap(map,Constants.INDEX_COLNUM,getColNum(e)+"");
            }
            if(e.getName().equals("SRC_DOMAIN")){
                putIntoMap(map,getColNum(e),Constants.SRC_DOMAIN);
            }
            if(e.getName().equals("SRC_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.SRC_TABLE_NAME);
            }
            if(e.getName().equals("SRC_TABLE_CODE")){
                putIntoMap(map,getColNum(e),Constants.SRC_TABLE_CODE);
            }
            if(e.getName().equals("SRC_COL_NAME")){
                putIntoMap(map,getColNum(e),Constants.SRC_COL_NAME);
            }
            if(e.getName().equals("SRC_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.SRC_COL_CODE);
            }

            if(e.getName().equals("MAP_SRC_DES")){
                putIntoMap(map,getColNum(e),Constants.MAP_SRC_DES);
            }
            if(e.getName().equals("DES_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_NAME);
            }
            if(e.getName().equals("DES_TABLE_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_CODE);
            }
            if(e.getName().equals("DES_COL_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_NAME);
            }
            if(e.getName().equals("DES_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_CODE);
            }
            if(e.getName().equals("DES_COL_TYPE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_TYPE);
            }
            if(e.getName().equals("DES_COL_COMMENT")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_COMMENT);
            }
            if(e.getName().equals("DES_IS_PRIMARY")){
                putIntoMap(map,getColNum(e),Constants.DES_IS_PRIMARY);
            }

        }
        return map;
    }
    /**
     * create by: ccwei
     * create time: 16:20 2018/11/20
     * description: 获取Excel映射
     * @return
     */
    private static HashMap<Integer,List<String>> getCHECK_CDM_EXCELConfig(Element root) {
        HashMap<Integer,List<String>> map = new HashMap<Integer, List<String>>();
        Iterator<Element> it = root.elementIterator();
        while(it.hasNext()){
            Element e = it.next();
            if(e.getName().equals("COL_NUM")){
                putIntoMap(map,Constants.INDEX_COLNUM,getColNum(e)+"");
            }
            if(e.getName().equals("SRC_DOMAIN")){
                putIntoMap(map,getColNum(e),Constants.SRC_DOMAIN);
            }
            if(e.getName().equals("SRC_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.SRC_TABLE_NAME);
            }
            if(e.getName().equals("SRC_TABLE_CODE")){
                putIntoMap(map,getColNum(e),Constants.SRC_TABLE_CODE);
            }
            if(e.getName().equals("SRC_COL_NAME")){
                putIntoMap(map,getColNum(e),Constants.SRC_COL_NAME);
            }
            if(e.getName().equals("SRC_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.SRC_COL_CODE);
            }
            if(e.getName().equals("DES_TABLE_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_NAME);
            }
            if(e.getName().equals("DES_TABLE_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_CODE);
            }
            if(e.getName().equals("DES_COL_NAME")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_COMMENT);
            }
            if(e.getName().equals("DES_COL_CODE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_CODE);
            }
            if(e.getName().equals("DES_COL_TYPE")){
                putIntoMap(map,getColNum(e),Constants.DES_COL_TYPE);
            }
            if(e.getName().equals("DES_IS_PRIMARY")){
                putIntoMap(map,getColNum(e),Constants.DES_IS_PRIMARY);
            }
            if(e.getName().equals("DES_TABLE_COMMENT")){
                putIntoMap(map,getColNum(e),Constants.DES_TABLE_COMMENT);
            }
            if(e.getName().equals("DES_SCHEMA_POSTFIX")){
                putIntoMap(map,getColNum(e),Constants.DES_SCHEMA_POSTFIX);
            }
        }
        return map;
    }

    /**
     * create by: ccwei
     * create time: 15:54 2018/11/22
     * description: 解析列属性col，标明该属性在excel表格是对应是第几列。可以输入字母也可以输入数字
     * @return
     */
    private static int getColNum(Element e) {
        if(e.attribute("col") == null || e.attribute("col").getValue() == null){
            return -1;
        }
        String col = e.attribute("col").getValue();
        try{
            return Integer.parseInt(col);
        }catch (Exception ex){
            logger.warn(ex.getMessage());
        }
        if(col.length()>0){
            return col.toUpperCase().charAt(0) - 'A' + 1;
        }
        return -1;
    }
}
