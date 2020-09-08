import DataStructure.Relationship;
import DataStructure.ResourcePool;
import function.GenerateCreateTableSQL;
import function.ParsingCDMFile;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;

/**
 * CheckModule
 * Created by ccwei on 2018/10/10.
 */
public class TestGenerateCreateTableSQL {

    @Test
    public void readXmlFile() throws DocumentException {

        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = reader.read(new File("src/main/resources/GXJTT_ODS-DV_ETL_2.0.cdm"));
        //获取根节点元素对象
        Element node = document.getRootElement();
        //遍历所有的元素节点
        ParsingCDMFile.scanNodes(node);

        Relationship.initHub2SatRelationships();
//        for(String key : ResourcePool.getHub2SatMap().keySet()){
//            System.out.print(ResourcePool.getEntitiy(key).getName() + "  --->  ");
//            for(String name : ResourcePool.getHub2SatMap().get(key)) {
//                System.out.print(ResourcePool.getEntitiy(name).getName() + "|");
//            }
//            System.out.println();
//        }


        GenerateCreateTableSQL.loadXLSXFile("src/main/resources/gsclcz.xlsx","gsclcz.xlsx");



        ///ArrayList<Mapping> diagramMappings = initRelationShip();

//        ConvertODS2DVModel.handleMappings(diagramMappings);

//        PoiTools.writeExcel(diagramMappings,15, "E:\\code\\tool\\CheckModule\\output.xlsx");


//        function.Check.checkNameAndCode();
        //修改
//        modifyNodes(node);
//        writeToXml(document);
//
        System.out.println("readXmlFile finished!");
    }
}
