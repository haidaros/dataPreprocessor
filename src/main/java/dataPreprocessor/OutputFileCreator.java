package dataPreprocessor;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.ResourceUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static dataPreprocessor.DataExporter.Mode.BUGDENSITY;
import static dataPreprocessor.DataExporter.Mode.CLASS;
import static dataPreprocessor.DataExporter.Mode.NOBUGS;

/**
 * Created by eg on 16/03/15.
 */
public class OutputFileCreator {
    private DataExporter.Mode mode = NOBUGS;

    private String resultfileName = "result.xls";

    private String[] numericHeaders = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs",
            "PredictedNumberofBugs", "PredictedDensity"};

    private String[] optimalHeaders = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Density"};
    private String[] densityHeader = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Predicted Density"};
    private String[] classHeader = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Prediction"};
    private String[] pronenessHeader = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Prediction"};

    public OutputFileCreator(DataExporter.Mode modeofDataExporter) {
        this.mode = modeofDataExporter;
    }

    public void createResultFile(List<DataEntry> list, String resultfileName) throws IOException {
        this.resultfileName = resultfileName;
        createResultFile(list);
    }

    public void createResultFile(List<DataEntry> list) throws IOException {
        FileInputStream fis = new FileInputStream(ResourceUtils.getPath("templateResult.xls"));
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
        FileOutputStream out = new FileOutputStream(new File(resultfileName));
        wb.write(out);
        out.close();
    }

    public void createNumericalSheet(List<DataEntry> list, String resultfileName) throws Exception {
        this.resultfileName = resultfileName;
        createNumericalSheet(list);
    }

    public void createNumericalSheet(List<DataEntry> list) throws Exception {
        FileInputStream fis = new FileInputStream(ResourceUtils.getPath("templateExcel.xls"));
        XSSFWorkbook wb = new XSSFWorkbook();
        List<Prediction> predictions = list.get(0).getPredictions();
        int predictionscount;
        if (predictions != null)
            predictionscount = predictions.size();
        else
            throw new Exception("There is no predictions");
        //Chart
        Drawing drawing = wb.createSheet("Chart").createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 1, 22, 35);
        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        LineChartData data = chart.getChartDataFactory().createLineChartData();
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setNumberFormat("%");
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        //Data
        XSSFSheet sheet = wb.createSheet("Optimal");
        Collections.sort(list, new DensityComparer());
        int rowNum = createOptimal(sheet, list);
        fillFormulate(sheet, "Optimal", rowNum, data);
        createRandomOrder(sheet, data);
        for (int i = 0; i < predictionscount; i++) {
            if (mode == DataExporter.Mode.CLASS || mode == DataExporter.Mode.BUGPRONENESS)
                Collections.sort(list, new dynamicPredictionComparer(i));
            else
                Collections.sort(list, new dynamicPredictionDensityComparer(i));
            String sheetName = list.get(0).getPredictions().get(i).getName();
            sheet = wb.createSheet(sheetName);
            rowNum = fillExcel(sheet, list, i);
            fillFormulate(sheet, sheetName, rowNum, data);
        }
        //PlotChart
        chart.plot(data, bottomAxis, leftAxis);
        FileOutputStream out = new FileOutputStream(new File(resultfileName));
        wb.write(out);
        out.close();
    }

    private void createRandomOrder(XSSFSheet sheet, LineChartData data) {
        Row rw = sheet.getRow(1);
        Cell cell = rw.createCell(8);
        cell.setCellValue("Random Order");
        rw = sheet.getRow(2);
        rw.createCell(7).setCellValue(0);
        rw.createCell(8).setCellValue(0);
        rw = sheet.getRow(3);
        rw.createCell(7).setCellValue(1);
        rw.createCell(8).setCellValue(1);
        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 3, 7, 7));
        ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 3, 8, 8));
        LineChartSeries lineChartSeries = data.addSeries(xs, ys);
        lineChartSeries.setTitle("Random Order");
    }

    private int createOptimal(XSSFSheet sheet, List<DataEntry> list) {
        fillHeadersforExcel(sheet, "Optimal", optimalHeaders);
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
            double res = ((double) (locfornow)) / totalLoc;
            cell.setCellValue(res);

            //percentage of bug
            cell = row.createCell(3);
            double res1 = (double) (bugfornow) / totalBug;
            cell.setCellValue(res1);

            //density
            cell = row.createCell(4);
            cell.setCellValue(e.getDensity());
            rownum++;
        }
        return rownum;
    }

    private void fillFormulate(XSSFSheet sheet, String sheetName, int rowNum, LineChartData data) {
        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, rowNum - 2, 2, 2));
        ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, rowNum - 2, 3, 3));
        LineChartSeries lineChartSeries = data.addSeries(xs, ys);
        lineChartSeries.setTitle(sheetName);
    }

    private void fillHeadersforExcel(XSSFSheet sheet, String name) {
        fillHeadersforExcel(sheet, name, numericHeaders);
    }

    private void fillHeadersforExcel(XSSFSheet sheet, String name, String[] headers) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(3);
        cell.setCellValue(name);
        //
        row = sheet.createRow(1);
        int i = 0;
        for (String s : headers) {
            row.createCell(i++).setCellValue(s);
        }
    }

    private int fillExcel(XSSFSheet sheet, List<DataEntry> list, int predictionIndex) {
        if (mode == NOBUGS)
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name);
        else if (mode == CLASS)
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name, classHeader);
        else if (mode == BUGDENSITY)
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name, densityHeader);
        else
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name, pronenessHeader);
        int locfornow = 0;
        int bugfornow = 0;
        int numbers[] = calculateLocandBug(list);
        int totalBug = numbers[0];
        int totalLoc = numbers[1];
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            int k = 0;
            Cell cell = row.createCell(k++);
            cell.setCellValue(e.getLoc());
            locfornow += e.getLoc();
            //bugs
            cell = row.createCell(k++);
            cell.setCellValue(e.getBug());
            bugfornow += e.getBug();

            //percentage of loc
            cell = row.createCell(k++);
            double res = (double) (locfornow) / totalLoc;
            cell.setCellValue(res > 1 ? 1 : res);
            //percentega of bug
            cell = row.createCell(k++);
            double res1 = (double) (bugfornow) / totalBug;
            cell.setCellValue(res1 > 1 ? 1 : res1);

            if (mode == NOBUGS) {
                cell = row.createCell(k++);
                cell.setCellValue(e.predictions.get(predictionIndex).prediction);
            }

            cell = row.createCell(k++);
            cell.setCellValue(e.predictions.get(predictionIndex).predictionDensinty);
            rownum++;
        }
        return rownum;
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
            locfornow += e.getLoc();
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

    public void createResult(List<DataEntry> testList, String s) throws Exception {
        switch (mode) {
            case NOBUGS:
                createNumericalSheet(testList, s);
                break;
            case BUGDENSITY:
                createNumericalSheet(testList, s);
                break;
            case CLASS:
                createNumericalSheet(testList, s);
                break;
            case BUGPRONENESS:
                createNumericalSheet(testList, s);
                break;
            default:
                throw new Exception("you have to choose a mode");
        }
    }

    private void createClassSheet(List<DataEntry> list, String s) throws Exception {
        FileInputStream fis = new FileInputStream(ResourceUtils.getPath("templateExcel.xls"));
        XSSFWorkbook wb = new XSSFWorkbook();
        List<Prediction> predictions = list.get(0).getPredictions();
        int predictionscount;
        if (predictions != null)
            predictionscount = predictions.size();
        else
            throw new Exception("There is no predictions");
        //Chart
        Drawing drawing = wb.createSheet("Chart").createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 1, 22, 35);
        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        LineChartData data = chart.getChartDataFactory().createLineChartData();
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setNumberFormat("%");
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        //Data
        XSSFSheet sheet = wb.createSheet("Optimal");
        Collections.sort(list, new DensityComparer());
        int rowNum = createOptimal(sheet, list);
        fillFormulate(sheet, "Optimal", rowNum, data);
        createRandomOrder(sheet, data);
        for (int i = 0; i < predictionscount; i++) {
            Collections.sort(list, new dynamicPredictionDensityComparer(i));
            String sheetName = list.get(0).getPredictions().get(i).getName();
            sheet = wb.createSheet(sheetName);
            rowNum = fillExcel(sheet, list, i);
            fillFormulate(sheet, sheetName, rowNum, data);
        }
        //PlotChart
        chart.plot(data, bottomAxis, leftAxis);
        FileOutputStream out = new FileOutputStream(new File(resultfileName));
        wb.write(out);
        out.close();
    }
}
