import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Name;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by eg on 24/02/15.
 * todo
 */
public class DataExporterTest {
    DataExporter de;

    public static void main(String[] args) {
        try {
            new DataExporterTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataExporterTest() throws Exception {
        File inputFile = new File("/Users/eg/Desktop/Projects/dataPreprocessor/datasets/mylyn/single-version-ck-oo.xls");
        FileInputStream file = new FileInputStream(inputFile);
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        de = new DataExporter(0.25, "bugs", file, exclusion);
        de.setModeofDataExporter(DataExporter.Mode.NOBUGS);
        de.process();
        System.out.println("exclusion = " + exclusion);
        de.getTestCSV("test.csv");
        de.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.LinearRegression);
        wk.train();
        wk.fillLinearRegression(de.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBK);
        wk.train();
        wk.fillIBK(de.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.SMOreg);
        wk.train();
        wk.fillSVM(de.testList);
        createResultFile(de.testList);
    }

    public void createResultFile(List<DataEntry> list) throws IOException {
        FileInputStream fis = new FileInputStream("templateResult.xls");
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);
        int deface = 2;
        int rowNum = list.size();
        Collections.sort(list, new DensityComparer());
        createOptimal(wb, list);
        Collections.sort(list, new linearRegressionDensityComparer());
        createLinearRegression(wb, list);
        Collections.sort(list, new ibkDensityComparer());
        createibk(wb, list);
        Collections.sort(list, new svmDensityComparer());
        createSVM(wb, list);
        //Test//
        Name rangeCell = wb.getName("opPLoc");
        String reference = sheet.getSheetName() + "!$D$" + (deface + 1) + ":$D$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        rangeCell = wb.getName("opPBug");
        reference = sheet.getSheetName() + "!$E$" + (deface + 1) + ":$E$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        sheet = wb.getSheetAt(1);
        rangeCell = wb.getName("linPLoc");
        reference = sheet.getSheetName() + "!$C$" + (deface + 1) + ":$C$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        rangeCell = wb.getName("linPBug");
        reference = sheet.getSheetName() + "!$D$" + (deface + 1) + ":$D$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        sheet = wb.getSheetAt(2);
        rangeCell = wb.getName("ibkPLoc");
        reference = sheet.getSheetName() + "!$C$" + (deface + 1) + ":$C$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        rangeCell = wb.getName("ibkPBug");
        reference = sheet.getSheetName() + "!$D$" + (deface + 1) + ":$D$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        sheet = wb.getSheetAt(3);
        rangeCell = wb.getName("svmPLoc");
        reference = sheet.getSheetName() + "!$C$" + (deface + 1) + ":$C$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        rangeCell = wb.getName("svmPBug");
        reference = sheet.getSheetName() + "!$D$" + (deface + 1) + ":$D$" + (rowNum + deface);
        rangeCell.setRefersToFormula(reference);
        //Test//
        FileOutputStream out = new FileOutputStream(new File("result.xls"));
        wb.write(out);
        out.close();
    }

    private void createOptimal(HSSFWorkbook wb, List<DataEntry> list) {
        HSSFSheet sheet = wb.getSheetAt(0);
        int locfornow = 0;
        int bugfornow = 0;
        int numbers[] = calculateLocandBug(list);
        int totalBug = numbers[0];
        int totalLoc = numbers[1];
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            Cell cell = row.createCell(0);
            cell.setCellValue(e.getLoc());
            System.out.println("locfornow = " + locfornow + "e.getLoc()" + e.getLoc());
            locfornow += e.getLoc();
            System.out.println("locfornow = " + locfornow);
            System.out.println("totalLoc = " + totalLoc);
            //bugs
            cell = row.createCell(1);
            cell.setCellValue(e.getBug());
            bugfornow += e.getBug();
            //density
            cell = row.createCell(2);
            cell.setCellValue(e.getDensity());
            //todo
            //percentage of loc
            cell = row.createCell(3);
            double res = ((double) (locfornow)) / totalLoc;
            System.out.println("res = " + res);
            cell.setCellValue(res);

            //percentega of bug
            cell = row.createCell(4);
            double res1 = (double) (bugfornow) / totalBug;
            cell.setCellValue(res1);
            rownum++;
        }
    }

    private void createLinearRegression(HSSFWorkbook wb, List<DataEntry> list) {
        HSSFSheet sheet = wb.getSheetAt(1);
        int locfornow = 0;
        int bugfornow = 0;
        int numbers[] = calculateLocandBug(list);
        int totalBug = numbers[0];
        int totalLoc = numbers[1];
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            Cell cell = row.createCell(0);
            cell.setCellValue(e.getLoc());
            locfornow += e.getLoc();
            //bugs
            cell = row.createCell(1);
            cell.setCellValue(e.getBug());
            bugfornow += e.getBug();

            //percentage of loc
            cell = row.createCell(2);
            double res = (double) (locfornow) / totalLoc;
            cell.setCellValue(res > 1 ? 1 : res);
            //percentega of bug
            cell = row.createCell(3);
            double res1 = (double) (bugfornow) / totalBug;
            cell.setCellValue(res1 > 1 ? 1 : res1);

            cell = row.createCell(4);
            cell.setCellValue(e.getPrediction());

            cell = row.createCell(5);
            cell.setCellValue(e.getPredictionDensinty());

            rownum++;
        }
    }

    private void createibk(HSSFWorkbook wb, List<DataEntry> list) {
        HSSFSheet sheet = wb.getSheetAt(2);
        int locfornow = 0;
        int bugfornow = 0;
        int numbers[] = calculateLocandBug(list);
        int totalBug = numbers[0];
        int totalLoc = numbers[1];
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            Cell cell = row.createCell(0);
            cell.setCellValue(e.getLoc());
            locfornow += e.getLoc();
            //bugs
            cell = row.createCell(1);
            cell.setCellValue(e.getBug());
            bugfornow += e.getBug();

            //percentage of loc
            cell = row.createCell(2);
            double res = (double) (locfornow) / totalLoc;
            cell.setCellValue(res > 1 ? 1 : res);
            //percentega of bug
            cell = row.createCell(3);
            double res1 = (double) (bugfornow) / totalBug;
            cell.setCellValue(res1 > 1 ? 1 : res1);

            cell = row.createCell(4);
            cell.setCellValue(e.getIbkprediction());

            cell = row.createCell(5);
            cell.setCellValue(e.getIbkpredictionDensinty());

            rownum++;
        }
    }

    private void createSVM(HSSFWorkbook wb, List<DataEntry> list) {
        HSSFSheet sheet = wb.getSheetAt(3);
        int locfornow = 0;
        int bugfornow = 0;
        int numbers[] = calculateLocandBug(list);
        int totalBug = numbers[0];
        int totalLoc = numbers[1];
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            Cell cell = row.createCell(0);
            cell.setCellValue(e.getLoc());
            locfornow += e.getLoc();
            //bugs
            cell = row.createCell(1);
            cell.setCellValue(e.getBug());
            bugfornow += e.getBug();

            //percentage of loc
            cell = row.createCell(2);
            double res = (double) (locfornow) / totalLoc;
            cell.setCellValue(res > 1 ? 1 : res);
            //percentega of bug
            cell = row.createCell(3);
            double res1 = (double) (bugfornow) / totalBug;
            cell.setCellValue(res1 > 1 ? 1 : res1);

            cell = row.createCell(4);
            cell.setCellValue(e.getSvmprediction());

            cell = row.createCell(5);
            cell.setCellValue(e.getSvmpredictionDensinty());

            rownum++;
        }
    }

    private int[] calculateLocandBug(List<DataEntry> testList) {
        int totalloc = 0;
        int totalbug = 0;
        int rownum = 0;
        for (DataEntry e : testList) {
            totalbug += e.getBug();
            totalloc += e.getLoc();
            rownum++;
        }
        return new int[]{totalbug, totalloc, rownum};
    }
}
