package function;

import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;

import java.io.FileOutputStream;

/**
 * CheckModule
 * Created by ccwei on 2018/11/26.
 */
public class OutputTableDictionary {

    @Test
    public void main(){
        XWPFDocument doc = new XWPFDocument();// 创建Word文件

        try{
//            createTable(doc,"T1");
//            createTable(doc,"T2");


            FileOutputStream out = new FileOutputStream("src/sample.doc");
            doc.write(out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }


        finally{

        }
    }

    public static void  createTable(XWPFDocument doc,String tableName,String code,String name ,String type) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("table : " + tableName);
        XWPFTable table= doc.createTable(1, 3);//创建一个表
        table.getRow(0).getCell(0).setText("code");
        table.getRow(0).getCell(1).setText("type");
        table.getRow(0).getCell(2).setText("comment");
        for(int i = 0 ; i < 4; ++i){

            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(i + "");
            row.getCell(1).setText(i + "");
            row.getCell(2).setText(i + "");
        }
    }
}
