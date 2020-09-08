import function.CheckCDMandMapping;
import org.dom4j.DocumentException;
import org.junit.Test;
import utils.ConfigCenter;

/**
 * CheckModule
 * Created by ccwei on 2018/11/17.
 */
public class TestCheckCDM {

    @Test
    public void readXmlFile() throws DocumentException {

        ConfigCenter.initXmlConfig();
        CheckCDMandMapping.loadXLSXFile("src/main/resources/ETL_ODS-DV_COL_3.5.xlsx","ETL_ODS-DV_COL_3.5.xlsx");


        System.out.println("TEST finished!");
    }
}
