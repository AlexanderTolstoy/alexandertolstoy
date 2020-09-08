package function;

import DataStructure.*;
import OutputStructure.Line;
import OutputStructure.Mapping;
import utils.Constants;
import utils.Utils;

import java.util.*;

/**
 * CheckModule
 * Created by ccwei on 2018/9/20.
 */
public class ConvertODS2DVModel {

    private static boolean convertDesColCode = true;
    public static ArrayList<Mapping> initMappingTables(String diagramID){
        ArrayList<Mapping> diagramMappings = new ArrayList<Mapping>();
        Diagram diagram = ResourcePool.getDiagram().get(diagramID);
        if(diagram == null ){

        }
        HashMap<String, EntitySymbol> entityHashMap = diagram.getEntitySymbolHashMap();
        HashMap<String,String> entityMapping = new HashMap<String, String>();
        //获取diagram里面的所有entity
        for(String key : entityHashMap.keySet()){
            EntitySymbol entitySymbol = entityHashMap.get(key);
            entityMapping.put(key,entitySymbol.getRef());
        }
        //获取源表到目标表的映射关系
        HashMap<String, DiagramLink> diagramLinkHashMap = ResourcePool.getDiagramLinks().get(diagramID);
        HashMap<String, HashSet<String>> linkMapping = new HashMap<String, HashSet<String>>();
        for(String key : diagramLinkHashMap.keySet()){
            DiagramLink diagramLink = diagramLinkHashMap.get(key);
            //在diagram里面的id是代理id
            String srcSid = diagramLink.getSourceEntity();
            String desSid = diagramLink.getDestinationEntity();
            if(entityMapping.keySet().contains(srcSid) && entityMapping.keySet().contains(desSid)){
                if(linkMapping.containsKey(entityMapping.get(srcSid))){
                    HashSet<String> tmp = linkMapping.get(entityMapping.get(srcSid));
                    tmp.add(entityMapping.get(desSid));
                }else {
                    HashSet<String> tmp = new HashSet<String>();
                    tmp.add(entityMapping.get(desSid));
                    linkMapping.put(entityMapping.get(srcSid),tmp);
                }
            }
        }
        //将映射关系放入mapping
        for(String key : linkMapping.keySet()){
            Mapping mapping = new Mapping();
            mapping.setSrcTable(key);
            mapping.setDesTable(linkMapping.get(key));
            diagramMappings.add(mapping);
        }
        return diagramMappings;
    }
    /**
     * create by: ccwei
     * create time: 20:19 2018/11/13
     * description:
     * @return
     */

    public static void handleMappings(ArrayList<Mapping> diagramMappings) {
        if(null == diagramMappings && diagramMappings.size() == 0){
            return;
        }

        for(int i = 0 ; i < diagramMappings.size(); i++){
            Mapping mapping = diagramMappings.get(i);
            Entity src = ResourcePool.getEntities().get(mapping.getSrcTable());
            //源表打印
            ArrayList<Line> srcTable = handleSrc(src);
            mapping.getRes().add(srcTable);

            HashSet<String> used = new HashSet<String>();
            for(String key : mapping.getDesTable()){
                Entity des = ResourcePool.getEntities().get(key);
                if(src == null || des == null){
                    continue;
                }
                //handle
                ArrayList<Line> lineArrayList = handleDestTable(src,des,used);
                    Collections.sort(lineArrayList);
                    mapping.getRes().add(lineArrayList);
//                if(des.getName().startsWith("H_")){
//                    //hub
//                    ArrayList<Line> lineArrayList = handleDestTable(src,des,used);
//                    Collections.sort(lineArrayList);
//                    mapping.getRes().add(lineArrayList);
//                }else if(des.getName().startsWith("L_")){
//                    //link
//                    ArrayList<Line> lineArrayList = handleDestTable(src,des,used);
//                    Collections.sort(lineArrayList);
//                    mapping.getRes().add(lineArrayList);
//                }else if(des.getName().startsWith("S_")){
//                    //sat
//                    ArrayList<Line> lineArrayList = handleDestTable(src,des,used);
//                    Collections.sort(lineArrayList);
//                    mapping.getRes().add(lineArrayList);
//                }
            }

            for(int idx = 0 ; idx < srcTable.size(); ++idx){
                Line line = srcTable.get(idx);
                if(!used.contains(line.getSrcColumnName())){
                    line.setSelfDifine("not used");
                }
            }
        }
    }

//    private static ArrayList<Line> handleSat(Entity src,Entity des) {
//        ArrayList<Line> lineArrayList = new ArrayList<Line>();
//        ArrayList<Attribute> attributes = (ArrayList<Attribute>) des.getAttributes();
//        for (int i = 0; i < attributes.size(); i++) {
//            Attribute attribute = attributes.get(i);
//            Item desItem = ResourcePool.getItem(attribute.getRef());
//            Line line = new Line();
//            line.setDesSchemas("DW_DV");
//            line.setDesTableName(des.getName());
//            line.setDesTableCode(convertTableCode(des.getCode()));
//            line.setDesColumnName(desItem.getName());
//            if(convertDesColCode){
//                line.setDesColumnCode(convertAttributeCode(desItem.getName()));
//            }else {
//                line.setDesColumnCode(desItem.getCode());
//            }
//            line.setDesDateType(convertType(desItem.getCode(),desItem.getDataType()));
//
//            line.setSrcSchemas(getSrcSchemas(src.getCode()));
//            line.setSrcTableName(src.getName());
//            line.setSrcTableCode(src.getCode());
//
//            justDoIt(desItem.getName(),src, line);
//
//            lineArrayList.add(line);
//        }
//        return lineArrayList;
//    }
//
//    private static ArrayList<Line> handleLink(Entity src,Entity des) {
//        ArrayList<Line> lineArrayList = new ArrayList<Line>();
//        ArrayList<Attribute> attributes = (ArrayList<Attribute>) des.getAttributes();
//        for (int i = 0; i < attributes.size(); i++) {
//            Attribute attribute = attributes.get(i);
//            Item desItem = ResourcePool.getItem(attribute.getRef());
//            if(desItem == null ){
//                System.out.println("item = " + attribute.getId());
//            }
//            Line line = new Line();
//            line.setDesSchemas("DW_DV");
//            line.setDesTableName(des.getName());
//            line.setDesTableCode(convertTableCode(des.getCode()));
//            line.setDesColumnName(desItem.getName());
//            if(convertDesColCode){
//                line.setDesColumnCode(convertAttributeCode(desItem.getName()));
//            }else {
//                line.setDesColumnCode(desItem.getCode());
//            }
//            line.setDesDateType(convertType(desItem.getCode(),desItem.getDataType()));
//
//            line.setSrcSchemas(getSrcSchemas(src.getCode()));
//            line.setSrcTableName(src.getName());
//            line.setSrcTableCode(src.getCode());
//
//            justDoIt(desItem.getName(),src, line);
//
//
//            lineArrayList.add(line);
//        }
//        return lineArrayList;
//    }

    private static ArrayList<Line> handleDestTable(Entity src,Entity des,HashSet<String> used ) {
        ArrayList<Line> lineArrayList = new ArrayList<Line>();
        ArrayList<Attribute> attributes = (ArrayList<Attribute>) des.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            Item desItem = ResourcePool.getItem(attribute.getRef());
            Line line = new Line();
            line.setDesSchemas("DW_DV");
            line.setDesTableName(des.getName());
            line.setDesTableCode(convertTableCode(des.getCode()));
            line.setDesColumnName(desItem.getName());
            if(convertDesColCode){
                line.setDesColumnCode(convertAttributeCode(desItem.getName()));
            }else {
                line.setDesColumnCode(desItem.getCode());
            }
//            line.setDesDateType(convertType(desItem.getCode(),desItem.getDataType()));
            line.setDesDateType(convertType(src.getCode(),desItem.getCode(),desItem.getDataType()));

            line.setSrcSchemas(getSrcSchemas(src.getCode()));
            line.setSrcTableName(src.getName());
            line.setSrcTableCode(src.getCode());

            justDoIt(desItem.getName(),src, line,used);

            lineArrayList.add(line);
        }
        return lineArrayList;
    }

//    private static ArrayList<Line> handleHub(Entity src,Entity des) {
//        ArrayList<Line> lineArrayList = new ArrayList<Line>();
//        ArrayList<Attribute> attributes = (ArrayList<Attribute>) des.getAttributes();
//        for (int i = 0; i < attributes.size(); i++) {
//            Attribute attribute = attributes.get(i);
//            Item desItem = ResourcePool.getItem(attribute.getRef());
//            Line line = new Line();
//            line.setDesSchemas("DW_DV");
//            line.setDesTableName(des.getName());
//            line.setDesTableCode(convertTableCode(des.getCode()));
//            line.setDesColumnName(desItem.getName());
//            if(convertDesColCode){
//                line.setDesColumnCode(convertAttributeCode(desItem.getName()));
//            }else {
//                line.setDesColumnCode(desItem.getCode());
//            }
//            line.setDesDateType(convertType(desItem.getCode(),desItem.getDataType()));
//
//            line.setSrcSchemas(getSrcSchemas(src.getCode()));
//            line.setSrcTableName(src.getName());
//            line.setSrcTableCode(src.getCode());
//
//            justDoIt(desItem.getName(),src, line);
//
//            lineArrayList.add(line);
//        }
//        return lineArrayList;
//    }

    private static void justDoIt(String target,Entity src, Line line,HashSet<String> used) {
        target = target.trim().replace("BK_","");
        ArrayList<Attribute> attributes = (ArrayList<Attribute>) src.getAttributes();
        boolean notFound = true;
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            Item item = ResourcePool.getItem(attribute.getRef());
            String name = item.getName().trim();
            if(target.equals(name)){
                line.setSrcColumnName(name);
                line.setSrcColumnCode(item.getCode());
                line.setSrcDateType(item.getDataType());
                line.setConvertRule(item.getCode());
                used.add(name);
                notFound = false;
                break;
            }
        }
        if("LOG_LOAD_TIME".equals(target) || "LOG_INIT_TIME".equals(target)) {
            notFound = false;
            line.setConvertRule("current_timestamp()");
        }
        if("LOG_END_TIME".equals(target)){
            notFound = false;
            line.setConvertRule("to_timestamp('9999-01-01 00:00:00')");
        }
        if("LOG_SRC_NODE".equals(target)){
            String code = src.getCode();
            String[] tmp = code.split("_");
            if(tmp.length > 3){
                notFound = false;
                line.setConvertRule("''" + tmp[2] + "'");
            }
        }
        if("LOG_SRC_TABLE".equals(target)){
            String code = src.getCode();
            notFound = false;
            line.setConvertRule("''" + getSrcSchemas(code) + "." + code + "'");
        }
        if(target.startsWith("SK_")){
            notFound = false;
            line.setConvertRule("md5()");
        }
        if(notFound){
            line.setConvertRule("NO-MAPPING");
        }
    }

    private static ArrayList<Line> handleSrc(Entity src) {
        ArrayList<Line> lineArrayList = new ArrayList<Line>();
        ArrayList<Attribute> attributes = (ArrayList<Attribute>) src.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            Line line = new Line();
            line.setSrcSchemas(getSrcSchemas(src.getCode()));
            line.setSrcTableName(src.getName());
            line.setSrcTableCode(src.getCode());

            Item item = ResourcePool.getItem(attribute.getRef());
            String name = item.getName().trim();
            line.setSrcColumnName(name);
            line.setSrcColumnCode(item.getCode());
            line.setSrcDateType(item.getDataType());

            lineArrayList.add(line);
        }
        return lineArrayList;
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
     * create time: 20:18 2018/11/13
     * description:返回属性转换成英文的code
     * @return
     */
    private static String convertAttributeCode(String input){
        String res = input.trim();
//        if(res.startsWith("LOG_") || res.startsWith("SK_") || res.startsWith("BK_")){
//            if(Constants.Fixed.containsKey(res)){
//                res = Constants.Fixed.get(res);
//            }
//        }
        return Utils.convertPinyin2Char(res,res);
    }

    /**
     * create by: ccwei
     * create time: 20:12 2018/11/13
     * description: 返回转换成英文的表名
     *
     * @return
     */
    private static String convertTableCode(String input){
        String res = input.trim();
        if(res.startsWith("L_") || res.startsWith("S_") || res.startsWith("H_") || res.startsWith("HS_") || res.startsWith("HL_")){
            res = Utils.convert3Dimension(res);
        }
        return Utils.convertPinyin2Char(res,res);
    }


    private static String convertType(String table,String code,String type){

        if(code.equals("LOG_LOAD_TIME")||code.equals("LOG_INIT_TIME")){
            return "timestamp";
        }else if(code.startsWith("LOG_")||code.contains("_MD5")){
            return "varchar(64)";
        }

        HashMap<String, HashMap<String, String>> map = ResourcePool.getTypeHashMap();
        if(map.size() > 0 && map.containsKey(table)){
            return ResourcePool.getTypeHashMap().get(table).get(code);
        }
        String prefix = table.replace("_HIS","");
        if(map.size() > 0 && map.containsKey(prefix)){
            return ResourcePool.getTypeHashMap().get(prefix).get(code);
        }
        String res = code.trim();
        if(res.startsWith("LOG_")){
            res = res.replace("_HSH","");
            if(Constants.Fixed.containsKey(res)){
                res = Constants.Fixed.get(res);
            }
        }else {
            res = type == null ? "" : type.trim();
            if(res.equals("DT") || res.equals("D")){
                res = Constants.DV_TYPE.DATE.toString();
            }
            if(res.startsWith("VA")){
                res = Constants.DV_TYPE.STRING.toString();
            }
            if(res.startsWith("N")){
                res = Constants.DV_TYPE.INT.toString();
            }
            if(res.equals("TS")){
                res = Constants.DV_TYPE.TIMESTAMP.toString();
            }
        }
        return Utils.convertPinyin2Char(res,res);
    }


}
