package function;

import DataStructure.Diagram;
import DataStructure.Item;
import DataStructure.Relationship;
import DataStructure.ResourcePool;
import OutputStructure.Mapping;
import jdbc.OracleConnection;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static DataStructure.Tool.getAttributeByKey;

/**
 * CheckModule
 * Created by ccwei on 2018/9/1.
 */
public class LoadXmlFile {

    @Test
    public void readXmlFile() throws DocumentException {

        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = reader.read(new File("src/main/resources/GXJTT_ODS-DV_ETL_CCW.cdm"));
        //获取根节点元素对象
        Element node = document.getRootElement();
        //遍历所有的元素节点
        ParsingCDMFile.scanNodes(node);

        Relationship.initHub2SatRelationships();
        for(String key : ResourcePool.getHub2SatMap().keySet()){
            System.out.print(ResourcePool.getEntitiy(key).getName() + "  --->  ");
            for(String name : ResourcePool.getHub2SatMap().get(key)) {
                System.out.print(ResourcePool.getEntitiy(name).getName() + "|");
            }
            System.out.println();
        }


        System.out.println("readXmlFile finished!");
    }

    public static void load(String path) throws DocumentException {
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = reader.read(new File(path));
        //获取根节点元素对象
        Element node = document.getRootElement();
        //遍历所有的元素节点
        ParsingCDMFile.scanNodes(node);
        //注意先后顺序，这个必须在initDiagramLinks之后执行
        ParsingCDMFile.initAdditionalRelationShips();
        Relationship.initHub2SatRelationships();
        //从数据库初始化类型数据
        OracleConnection.init();
        //初始化输出
        for(String key : ResourcePool.getDiagramMappingMaps().keySet()){
            ArrayList<Mapping> ms = ResourcePool.getDiagramMappingMaps().get(key);
            ConvertODS2DVModel.handleMappings(ms);
        }

    }

    private ArrayList<Mapping> initRelationShip() {
        HashMap<String, Diagram> diagramHashMap = ResourcePool.getDiagram();
        for(String key : diagramHashMap.keySet()){
            System.out.println(key + " --> " + diagramHashMap.get(key).getName());
        }
        System.out.println("please input:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "o4936";
//        try {
//            input = br.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ArrayList<Mapping> diagramMappings = new ArrayList<Mapping>();
        if(input != null && input.length() > 0){
            diagramMappings = ConvertODS2DVModel.initMappingTables(input.trim());
        }
        return diagramMappings;
    }



    private void writeToXml(Document document) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("src/main/resources/GXJTT_DV_ZYZH_2.0.cdm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputFormat format = OutputFormat.createPrettyPrint();//标准化布局，适合查看时显示。
        //1.创建写入文件
        format.setEncoding("utf-8");//指定文件格式
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(out,format);
            writer.write(document);//写入文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modifyNodes(Element node) {

        String id = getAttributeByKey("Id",node);
        HashMap<String, Item> map = ResourcePool.getItems();

        if(map.containsKey(id)){
            Item item = map.get(id);

            ParsingCDMFile.setNodeValue(node,"Name",item.getName());
        }

        // 当前节点下面子节点迭代器
        Iterator<Element> it = node.elementIterator();
        // 遍历
        while (it.hasNext()) {
            // 获取某个子节点对象
            Element e = it.next();
            // 对子节点进行遍历
            modifyNodes(e);
        }
    }



}
