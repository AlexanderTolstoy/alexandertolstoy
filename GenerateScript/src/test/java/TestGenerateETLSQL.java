import function.GenerateETLSQL;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;

/**
 * CheckModule
 * Created by ccwei on 2018/10/13.
 */
public class TestGenerateETLSQL {

    @Test
    public void readXmlFile() throws DocumentException {

        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        //Document document = reader.read(new File("src/main/resources/GXJTT_ODS-DV_ETL_2.0.cdm"));
        //获取根节点元素对象
        //Element node = document.getRootElement();
        //遍历所有的元素节点
        //ParsingCDMFile.scanNodes(node);

        //Relationship.initHub2SatRelationships();

        GenerateETLSQL.loadXLSXFile("src/main/resources/gsclcz.xlsx","gsclcz.xlsx");


        System.out.println("TEST finished!");
    }
}
