package utils;

import DataStructure.Diagram;
import DataStructure.ResourcePool;
import OutputStructure.Line;
import OutputStructure.Mapping;
import function.ConvertODS2DVModel;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * CheckModule
 * Created by ccwei on 2018/9/18.
 */
public class PoiTools {

    private static Logger logger = Logger.getLogger(PoiTools.class.getClass());

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    private static int lineNum = 1;

//    public static void main(String[] args) {
//
//        Mapping m = new Mapping();
//        ArrayList<Line> data = new ArrayList<Line>();
//        Line line = new Line();
//        line.setDesColumnCode("5254");
//        line.setDesSchemas("oracle");
//        line.setDesTableName("ccwei ddd");
//
//        data.add(line);
//        data.add(line);
//
//        m.getRes().add(data);
//        m.getRes().add(data);
//        m.getRes().add(data);
//
//        String path = "C:\\Users\\dell\\Desktop\\colorTest.xlsx";
//        //寻找目录读取文件
//        File excelFile = new File(path);
//
//        try {
//            InputStream is = new FileInputStream(excelFile);
//            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
//            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
//            XSSFRow row = sheet.getRow(0);
//            Cell c1 = row.getCell(0);
//            c1.setCellValue(new Date().toString());
//            CellStyle coral = xssfWorkbook.createCellStyle();
//            coral.setFillForegroundColor(IndexedColors.CORAL.getIndex());
//            coral.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            c1.setCellStyle(coral);
//            System.out.println("red : " + c1.getCellStyle().getFillForegroundColor());
//
//            Cell c2 = row.getCell(1);
//            CellStyle light_green = xssfWorkbook.createCellStyle();
//            light_green.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//            light_green.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            c2.setCellStyle(light_green);
//            System.out.println("green : " + c2.getCellStyle().getFillBackgroundColor());
//
//            Cell c3 = row.getCell(2);
//            CellStyle sky_blue = xssfWorkbook.createCellStyle();
//            sky_blue.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
//            sky_blue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            c3.setCellStyle(sky_blue);
//            System.out.println("blue : " + c3.getCellStyle().getFillForegroundColor());
//
//            Cell c4 = row.getCell(3);
//            CellStyle light_yellow = xssfWorkbook.createCellStyle();
//            light_yellow.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
//            light_yellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            c4.setCellStyle(light_yellow);
//            System.out.println("yellow : " + c4.getCellStyle().getFillForegroundColor());
//
//            OutputStream out =  new FileOutputStream(path);
//            xssfWorkbook.write(out);
//            out.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
////        writeExcel(m, 15, "E:\\code\\tool\\CheckModule\\output.xlsx");
//
//    }

    public static void writeBatchSheet2Excel(HashSet<String> keys, int columnCount){
        OutputStream out = null;
        try {
            // 获取总列数
            int columnNumCount = columnCount;
            // 读取Excel文档
            String cdmName = ResourcePool.getFileName();
            String path = ResourcePool.getFilePath();
            String finalXlsxPath = path.replace(cdmName,"output.xlsx");
            File finalXlsxFile = new File(finalXlsxPath);
            if(!finalXlsxFile.exists()){
                finalXlsxFile.createNewFile();
            }

            Workbook workBook = new XSSFWorkbook();


            /**
             * 往Excel中写新数据
             */
            int sheetNum = 0;
            for(String key : keys){
                ArrayList<Mapping> ms = ResourcePool.getDiagramMappingMaps().get(key);
                Diagram diagram = ResourcePool.getDiagram().get(key);
                Sheet sheet = workBook.createSheet();
                workBook.setSheetName(sheetNum++,diagram.getName());
                setColTitle(workBook, sheet);
                lineNum = 1;
                //ConvertODS2DVModel.handleMappings(ms);
                for(Mapping m : ms) {
                    writeOneMapping2workbook(m, columnNumCount, sheet, workBook);
                }
            }

            // 创建文件输出流，准备输出电子表格
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("数据导出成功");
    }

    public static void writeExcel(ArrayList<Mapping> ms, int columnCount,String finalXlsxPath){
        OutputStream out = null;
        try {
            // 获取总列数
            int columnNumCount = columnCount;
            // 读取Excel文档
            File finalXlsxFile = new File(finalXlsxPath);
            if(!finalXlsxFile.exists()){
                finalXlsxFile.createNewFile();
            }

            Workbook workBook = new XSSFWorkbook();
            Sheet sheet = workBook.createSheet();
            workBook.setSheetName(0,"Test");

            setColTitle(workBook, sheet);

            /**
             * 往Excel中写新数据
             */
            for(Mapping m : ms) {
                writeOneMapping2workbook(m, columnNumCount, sheet, workBook);
            }
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("数据导出成功");
    }

    private static void setColTitle(Workbook workBook, Sheet sheet) {
        String title = "源模式,源表名,源表代码,列名,列代码,数据类型,转换规则,目标模式,目标表,目标表代码,目标列名,目标列代码,目标列数据类型,是否主键,是否分区,自定义";
        String columnNames[] =  title.split(",");
        CellStyle style = ExportExcelUtils.getColTopStyle(workBook);
        Row row = sheet.createRow(0);
        for (int i=0;i<columnNames.length;i++){
            Cell headCell = row.createCell(i);
            headCell.setCellValue(columnNames[i]);
            headCell.setCellStyle(style);
        }
        sheet.createFreezePane(0,1,0,1);
    }

    private static void writeOneMapping2workbook(Mapping map, int columnNumCount, Sheet sheet, Workbook workBook) {
        //输出一个源表之前空行
        lineNum++;
        for (int tbl = 0; tbl < map.getRes().size(); tbl++) {
            // 取出一个映射里面的每个表
            ArrayList<Line> table = map.getRes().get(tbl);
            if(table.size() == 1 ) {
                handleOneLine(columnNumCount, sheet, workBook, table);
            }else{
                handleMultiLine(columnNumCount, sheet, workBook, table);
            }
        }
    }

    private static void handleMultiLine(int columnNumCount, Sheet sheet, Workbook workBook, ArrayList<Line> table) {
        short color = getColor(table);
        CellStyle leftTop = workBook.createCellStyle();
        leftTop.setBorderLeft(BorderStyle.THIN);
        leftTop.setBorderTop(BorderStyle.THIN);
        if(color != -1){
            leftTop.setFillForegroundColor(color);
            leftTop.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle top = workBook.createCellStyle();
        top.setBorderTop(BorderStyle.THIN);
        if(color != -1){
            top.setFillForegroundColor(color);
            top.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle rightTop = workBook.createCellStyle();
        rightTop.setBorderRight(BorderStyle.THIN);
        rightTop.setBorderTop(BorderStyle.THIN);
        if(color != -1){
            rightTop.setFillForegroundColor(color);
            rightTop.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle right = workBook.createCellStyle();
        right.setBorderRight(BorderStyle.THIN);
        if(color != -1){
            right.setFillForegroundColor(color);
            right.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle rightBottom = workBook.createCellStyle();
        rightBottom.setBorderRight(BorderStyle.THIN);
        rightBottom.setBorderBottom(BorderStyle.THIN);
        if(color != -1){
            rightBottom.setFillForegroundColor(color);
            rightBottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle bottom = workBook.createCellStyle();
        bottom.setBorderBottom(BorderStyle.THIN);
        if(color != -1){
            bottom.setFillForegroundColor(color);
            bottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle leftBottom = workBook.createCellStyle();
        leftBottom.setBorderLeft(BorderStyle.THIN);
        leftBottom.setBorderBottom(BorderStyle.THIN);
        if(color != -1){
            leftBottom.setFillForegroundColor(color);
            leftBottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle left = workBook.createCellStyle();
        left.setBorderLeft(BorderStyle.THIN);
        if(color != -1){
            left.setFillForegroundColor(color);
            left.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle other = workBook.createCellStyle();
        if(color != -1){
            other.setFillForegroundColor(color);
            other.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        for (int l = 0; l < table.size(); l++) {
            //取出表里的每一行
            Row row = sheet.createRow(lineNum++);
            Line line = table.get(l);
            for(int n = 0 ; n < columnNumCount ;n++) {
                Cell cell = row.createCell(n);
                cell.setCellValue(line.getByIndex(n));
                CellStyle cellStyle = other;
                if(l == 0 && n == 0) {//left top:左上角
                    cellStyle = leftTop;
                }
                if(l == 0 && n > 0 && n < columnNumCount-1){//top
                    cellStyle = top;
                }
                if(l == 0 && n == columnNumCount-1){
                    cellStyle = rightTop;//right top
                }
                if(l != table.size()-1 && l != 0 && n == columnNumCount-1){//right
                    cellStyle = right;
                }
                if(l == table.size()-1 && n == columnNumCount-1){//right bottom
                    cellStyle = rightBottom;
                }
                if(l == table.size()-1 && n != 0 && n != columnNumCount-1){//bottom
                    cellStyle = bottom;
                }
                if(l == table.size()-1 && n == 0){//left bottom
                    cellStyle = leftBottom;
                }
                if(n == 0 && l != 0 && l != table.size()-1){//left
                    cellStyle = left;
                }
                if(cellStyle != null){
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }

    private static void handleOneLine(int columnNumCount, Sheet sheet, Workbook workBook, ArrayList<Line> table) {
        short color = getColor(table);
        CellStyle left = workBook.createCellStyle();
        left.setBorderBottom(BorderStyle.THIN);
        left.setBorderLeft(BorderStyle.THIN);
        left.setBorderTop(BorderStyle.THIN);
        if(color != -1){
            left.setFillForegroundColor(color);
            left.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle center = workBook.createCellStyle();
        center.setBorderBottom(BorderStyle.THIN);
        center.setBorderTop(BorderStyle.THIN);
        if(color != -1){
            center.setFillForegroundColor(color);
            center.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        CellStyle right = workBook.createCellStyle();
        right.setBorderBottom(BorderStyle.THIN);
        right.setBorderLeft(BorderStyle.THIN);
        right.setBorderTop(BorderStyle.THIN);
        if(color != -1){
            right.setFillForegroundColor(color);
            right.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        for (int l = 0; l < table.size(); l++) {
            //取出表里的每一行
            Row row = sheet.createRow(lineNum++);
            Line line = table.get(l);
            for (int n = 0; n < columnNumCount; n++) {
                Cell cell = row.createCell(n);
                cell.setCellValue(line.getByIndex(n));
                CellStyle cellStyle = workBook.createCellStyle();
                if (n == 0) {
                    cell.setCellStyle(left);//左边框
                } else if (n == columnNumCount - 1) {
                    cell.setCellStyle(right);//右边框
                } else {
                    cell.setCellStyle(center);
                }
            }
        }
    }

    private static short getColor(ArrayList<Line> table) {
        if(table.size() > 0){
            Line line = table.get(0);
            if(line.getDesTableName() == null ){
                return line.getSrcTableCode().startsWith("TB_") ? IndexedColors.LIGHT_GREEN.getIndex() : -1;
            }
            if(line.getDesTableName().startsWith("H_")){
                return IndexedColors.CORAL.getIndex();
            }else if(line.getDesTableName().startsWith("L_")){
                return IndexedColors.LIGHT_YELLOW.getIndex();
            }else if(line.getDesTableName().startsWith("S_")){
                return IndexedColors.SKY_BLUE.getIndex();
            }
        }
        return -1;
    }

    public static Workbook getWorkbook(File file) throws IOException{
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }


}
