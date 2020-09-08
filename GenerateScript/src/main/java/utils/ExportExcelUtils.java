package utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;

/**
 * CheckModule
 * Created by ccwei on 2018/9/20.
 */
public class ExportExcelUtils {

    //显示的导出表的标题
    private String title;
    //导出表的列名
    private String[] rowName ;

    private List<Object[]> dataList = new ArrayList<Object[]>();


    //构造方法，传入要导出的数据
    public ExportExcelUtils(String title,String[] rowName,List<Object[]>  dataList){
        this.dataList = dataList;
        this.rowName = rowName;
        this.title = title;
    }

    /*
     * 列表首页的大title样式
     */
    public static HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {

        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short)16);
        //字体加粗
        font.setBold(true);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
//        style.setBottomBorderColor();
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
//        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
//        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
//        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置背景颜色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        return style;
    }

    /*
     * 标题 列的样式
     */
    public static CellStyle getColTopStyle(Workbook workbook) {
        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short)10);
        //字体加粗
        font.setBold(true);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
//        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
//        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
//        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
//        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置背景颜色
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        return style;
    }



    /**设计excel定义列数、列宽和标头信息*/
    public static HSSFSheet setSheetBaseInfoExcel(String excelName,int columWith,int rowHight,boolean fixedColum,String columWithMsg ,HSSFWorkbook wb){
        HSSFSheet sheet = wb.createSheet(excelName);
        sheet.setDefaultColumnWidth(columWith);
        sheet.setDefaultRowHeightInPoints(rowHight);
        if(fixedColum){
            sheet.createFreezePane(2,3,2,3);
        }

        String columWiths[] =  columWithMsg.split(",");
        for (int i=0;i<columWiths.length;i++){
            sheet.setColumnWidth(i,Integer.valueOf(columWiths[i])*512);
        }
        return sheet;
    }

    /**设计excel标头信息*/
    public static HSSFRow setSheetTitleExcel(String columNameMsg , HSSFSheet sheet,HSSFCellStyle style){
        String columNames[] =  columNameMsg.split(",");
        HSSFRow row = sheet.createRow(2);
        for (int i=0;i<columNames.length;i++){
            HSSFCell headCell = row.createCell(i);
            headCell.setCellValue(columNames[i]);
            headCell.setCellStyle(style);
        }
        return row;
    }

    /**创建统计行数据，创建指定列并且赋值*/
    public static void setSheetSumTotleExcel(String columNos ,String columNams,String columValues,HSSFCellStyle style,HSSFSheet sheet){
        int lastRowNum = sheet.getPhysicalNumberOfRows();
        HSSFRow row = sheet.createRow(lastRowNum + 1);
        HSSFCell cell;
        String columNoss[] =  columNos.split(",");
        String columNames[] =  columNams.split(",");
        String columValuess[] =  columValues.split(",");
        for (int i=0;i<columNoss.length;i++){

            cell = row.createCell(Integer.valueOf(columNoss[i])-1);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(columNames[i]+": ");
            cell.setCellStyle(style);

            cell = row.createCell(Integer.valueOf(columNoss[i]));
            cell.setCellType(CellType.STRING);
            cell.setCellValue(columValuess[i]);
            cell.setCellStyle(style);
        }
    }

    /**创建Cell 指定单元格数据类型 valueType=1代表正常单行数据   valueType=2代表换行的数据格式*/
    public static void createCellSetValueExcel(int columNo,String value,int valueType,HSSFRow row){
        HSSFCell cell;
        cell = row.createCell(columNo);
        cell.setCellType(CellType.STRING);
        if(valueType==1){
            cell.setCellValue(value);
        }else {
            cell.setCellValue(value);
        }
    }
}
