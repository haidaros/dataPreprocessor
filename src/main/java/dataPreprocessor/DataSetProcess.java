package dataPreprocessor;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;

/**
 * Created by eg on 18/02/15.
 */
public class DataSetProcess {
    static double testRate = 0.25;
    static String bugTableHeader = "bugs";
    static String outputfileName = "equinox_change_matrix.xls";

    public static void main(String[] args) throws Exception {
        //Reading File
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("format sheet");
        CellStyle style;
        DataFormat format = wb.createDataFormat();
        Row row;
        Cell cell;
        short rowNum = 0;
        short colNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(colNum);
        cell.setCellValue(11111.25);
        style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("0.0"));
        cell.setCellStyle(style);

        row = sheet.createRow(rowNum++);
        cell = row.createCell(colNum);
        cell.setCellValue(11111.25);
        style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("#,##0.0000"));
        cell.setCellStyle(style);

        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }
}
