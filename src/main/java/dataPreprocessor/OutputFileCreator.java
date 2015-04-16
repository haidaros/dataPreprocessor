package dataPreprocessor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

import static dataPreprocessor.DataExporter.Mode.*;

/**
 * Created by eg on 16/03/15.
 */
public class OutputFileCreator {
    private DataExporter.Mode mode = NOBUGS;
    private String resultfileName = "result.xls";
    private String[] numericHeaders = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs",
            "PredictedNumberofBugs", "PredictedDensity"};
    private String[] optimalHeaders = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Density", "Area"};
    private String[] densityHeader = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Predicted Density", "Area"};
    private String[] classHeader = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Prediction", "Area"};
    private String[] pronenessHeader = {"NumberOfLinesOfCode", "Actualno.ofbugs", "PercentageofLocs", "PercentageofBugs", "Probability", "Area"};

    double[] uacForAlphas;

    Sheet ceSheet;

    public OutputFileCreator(DataExporter.Mode modeofDataExporter) {
        this.mode = modeofDataExporter;
    }

    public void createNumericalSheet(List<DataEntry> list, String resultfileName, double[] ceList) throws Exception {
        this.resultfileName = resultfileName;
        createNumericalSheet(list, ceList);
    }

    public void createNumericalSheet(List<DataEntry> list, double[] ceList) throws Exception {
        uacForAlphas = new double[ceList.length];
        FileInputStream fis = new FileInputStream(ResourceUtils.getPath("templateExcel.xls"));
        XSSFWorkbook wb = new XSSFWorkbook();
        List<Prediction> predictions = list.get(0).getPredictions();
        int predictionscount;
        if (predictions != null)
            predictionscount = predictions.size();
        else
            throw new Exception("There is no predictions");
        //Chart 1
        Drawing drawing = wb.createSheet("Chart").createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 1, 17, 35);
        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP);
        ScatterChartData data = chart.getChartDataFactory().createScatterChartData();
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setNumberFormat("Percentage"); //todo  not working right now
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        //Chart 2
        Sheet ceChartSheet = wb.createSheet("CEChart");
        ceSheet = ceChartSheet;
//        Drawing drawing2 = ceChartSheet.createDrawingPatriarch();
//        ClientAnchor anchor2 = drawing.createAnchor(0, 0, 0, 0, 1, 1, 17, 35);
//        Chart chart2 = drawing.createChart(anchor);
//        ChartLegend legend2 = chart.getOrCreateLegend();
//        legend2.setPosition(LegendPosition.TOP);
//        ChartAxis bottomAxis2 = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
//        ValueAxis leftAxis2 = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
//        ScatterChartData data2 = chart.getChartDataFactory().createScatterChartData();
        createHeaderforCeChart(list.get(0), ceChartSheet, ceList);
        //Data
        XSSFSheet sheet = wb.createSheet("Optimal");
        Collections.sort(list, new DensityComparer());
        calculatePercentages(list);
        calculateAreaBelow(list);
        int rowNum = createOptimal(sheet, list, ceList);
        fillFormulate(sheet, "Optimal", rowNum, data);
        createRandomOrder(sheet, data);
        for (int i = 0; i < predictionscount; i++) {
            if (mode == DataExporter.Mode.CLASS)
                Collections.sort(list, new dynamicPredictionComparer(i));
            else if (mode == DataExporter.Mode.BUGPRONENESS)
                Collections.sort(list, new dynamicProbabilityComparer(i));
            else
                Collections.sort(list, new dynamicPredictionDensityComparer(i));
            String sheetName = list.get(0).getPredictions().get(i).getName();
            //todo
            //Area Calculation
            //percentageCalculation
            calculatePercentages(list);
            calculateAreaBelow(list);
            sheet = wb.createSheet(sheetName);
            rowNum = fillExcel(sheet, list, i, ceList);
            fillFormulate(sheet, sheetName, rowNum, data);
        }
        //PlotChart
//        createCeChart(data2, list.get(0));
//        chart2.plot(data2, bottomAxis2, leftAxis2);
        chart.plot(data, bottomAxis, leftAxis);
        FileOutputStream out = new FileOutputStream(new File(resultfileName));
        wb.write(out);
        out.close();
    }

//    private void createCeChart(ScatterChartData data2, DataEntry dataEntry) {
//        int size = dataEntry.getPredictions().size();
//        for (Prediction p : dataEntry.getPredictions()) {
//            ChartDataSource<Number> ys = DataSources.fromNumericCellRange(ceSheet, new CellRangeAddress(1, size, 1, 2));//todo celist size
//            ScatterChartSeries scatterChartSeries = data2.addSerie(null, ys);
//            scatterChartSeries.setTitle(p.getName());
//        }
//    }

    private void createHeaderforCeChart(DataEntry dataEntry, Sheet ceChartSheet, double[] ceList) {
        Row row = ceChartSheet.createRow(0);
        int cno = 1;
        int rno = 1;
        for (double ce : ceList) {
            row.createCell(cno++).setCellValue("Alpha = " + ce);
        }
        for (Prediction p : dataEntry.getPredictions()) {
            ceChartSheet.createRow(rno++).createCell(0).setCellValue(p.getName());
        }
    }

    private void calculatePercentages(List<DataEntry> list) {
        int numbers[] = calculateLocandBug(list);
        int totalBug = numbers[0];
        int totalLoc = numbers[1];
        int locfornow = 0;
        int bugfornow = 0;
        for (int i = 0; i < list.size(); i++) {
            DataEntry e = list.get(i);
            locfornow += e.getLoc();
            bugfornow += e.getBug();
            //percentage of loc
            double res = (double) (locfornow) / totalLoc;
            e.setPercentageofLoc(res > 1 ? 1 : res);
            //percentega of bug
            double res1 = (double) (bugfornow) / totalBug;
            e.setPercentageofBug(res1 > 1 ? 1 : res1);
        }
    }

    private void calculateAreaBelow(List<DataEntry> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            DataEntry entry1 = list.get(i);
            DataEntry entry2 = list.get(i + 1);
            double area = ((entry2.getPercentageofLoc() - entry1.getPercentageofLoc()) * (entry1.getPercentageofBug() + entry2.getPercentageofBug())) / 2;
            entry1.setArea(area);
        }
    }

    private void createRandomOrder(XSSFSheet sheet, ScatterChartData data) {
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
        ScatterChartSeries lineChartSeries = data.addSerie(xs, ys);
        lineChartSeries.setTitle("Random Order");
    }

    private int createOptimal(XSSFSheet sheet, List<DataEntry> list, double[] ceList) {
        fillHeadersforExcel(sheet, "Optimal", optimalHeaders);
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            Cell cell = row.createCell(0);
            cell.setCellValue(e.getLoc());
            //bugs
            cell = row.createCell(1);
            cell.setCellValue(e.getBug());

            //percentage of loc
            cell = row.createCell(2);
            cell.setCellValue(e.getPercentageofLoc());

            //percentage of bug
            cell = row.createCell(3);
            cell.setCellValue(e.getPercentageofBug());

            //density
            cell = row.createCell(4);
            cell.setCellValue(e.getDensity());

            cell = row.createCell(5);
            cell.setCellValue(e.getArea());
            rownum++;
        }
        //columnnumber
        int cn = 8;
        int lastRow = 21;
        for (int l = 0; l < ceList.length; l++) {
            double alpha = ceList[l];
            int prediction = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPercentageofLoc() >= alpha) {
                    prediction = i;
                    break;
                }
            }
            DataEntry e1 = list.get(prediction);
            DataEntry e2 = list.get(prediction - 1);
            double x1 = e1.getPercentageofLoc();
            double x2 = e2.getPercentageofLoc();
            double y1 = e1.getPercentageofBug();
            double y2 = e2.getPercentageofBug();
            double estimatedPBug = y1 + ((alpha - x1) * (y2 - y1) / (x2 - x1));
            double aucBx1andx2 = ((x1 - x2) * (y1 + y2)) / 2;
            double aucx1andAlpha = (alpha - x1) * (y1 + estimatedPBug) / 2;
            double aucb0andfirst = list.get(0).getPercentageofLoc() * list.get(0).getPercentageofBug() / 2;
            sheet.getRow(5).createCell(cn).setCellValue("X1");
            sheet.getRow(6).createCell(cn).setCellValue(x1);
            sheet.getRow(7).createCell(cn).setCellValue("X2");
            sheet.getRow(8).createCell(cn).setCellValue(x2);
            sheet.getRow(9).createCell(cn).setCellValue("Y1");
            sheet.getRow(10).createCell(cn).setCellValue(y1);
            sheet.getRow(11).createCell(cn).setCellValue("Y2");
            sheet.getRow(12).createCell(cn).setCellValue(y2);
            sheet.getRow(13).createCell(cn).setCellValue("Estimated Percentage of bugs (y)");
            sheet.getRow(14).createCell(cn).setCellValue(estimatedPBug);
            sheet.getRow(15).createCell(cn).setCellValue("AUC between x1 and x2");
            sheet.getRow(16).createCell(cn).setCellValue(aucBx1andx2);
            sheet.getRow(17).createCell(cn).setCellValue("AUC between x1 and alpha");
            sheet.getRow(18).createCell(cn).setCellValue(aucx1andAlpha);
            sheet.getRow(19).createCell(cn).setCellValue("AUC between x1 and firstPoint");
            sheet.getRow(20).createCell(cn).setCellValue(aucb0andfirst);
            double totalarea = 0;
            for (int k = 0; k <= prediction; k++) {
                totalarea += list.get(k).getArea();
            }
            double finalUacforAlpha;
            if (alpha == 1)
                finalUacforAlpha = totalarea + aucb0andfirst;
            else
                finalUacforAlpha = totalarea - aucBx1andx2 + aucx1andAlpha + aucb0andfirst;
            sheet.getRow(lastRow).createCell(cn).setCellValue("Final AUC at alpha=" + alpha);
            sheet.getRow(lastRow + 1).createCell(cn).setCellValue(finalUacforAlpha);
            uacForAlphas[l] = finalUacforAlpha;
            cn++;
        }
        return rownum;
    }

    private void fillFormulate(XSSFSheet sheet, String sheetName, int rowNum, ScatterChartData data) {
        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, rowNum - 1, 2, 2));
        ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, rowNum - 1, 3, 3));
        ScatterChartSeries scatterChartSeries = data.addSerie(xs, ys);
        scatterChartSeries.setTitle(sheetName);
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

    private int fillExcel(XSSFSheet sheet, List<DataEntry> list, int predictionIndex, double[] ceList) {
        if (mode == NOBUGS)
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name);
        else if (mode == CLASS)
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name, classHeader);
        else if (mode == BUGDENSITY)
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name, densityHeader);
        else
            fillHeadersforExcel(sheet, list.get(0).getPredictions().get(predictionIndex).name, pronenessHeader);
        int rownum = 2;
        for (DataEntry e : list) {
            Row row = sheet.createRow(rownum);
            //loc
            int k = 0;
            Cell cell = row.createCell(k++);
            cell.setCellValue(e.getLoc());
            //bugs
            cell = row.createCell(k++);
            cell.setCellValue(e.getBug());

            //percentage of loc
            cell = row.createCell(k++);
            cell.setCellValue(e.getPercentageofLoc());
            //percentega of bug
            cell = row.createCell(k++);
            cell.setCellValue(e.getPercentageofBug());

            if (mode == NOBUGS) {
                cell = row.createCell(k++);
                cell.setCellValue(e.predictions.get(predictionIndex).prediction);
            }

            if (mode != BUGPRONENESS) {
                cell = row.createCell(k++);
                cell.setCellValue(e.predictions.get(predictionIndex).predictionDensinty);
            } else {
                cell = row.createCell(k++);
                cell.setCellValue(e.predictions.get(predictionIndex).probability);

                cell = row.createCell(k++);
                cell.setCellValue(e.predictions.get(predictionIndex).prediction);
            }

            cell = row.createCell(k++);
            cell.setCellValue(e.getArea());
            rownum++;
        }
        int cn = 8;
        int lastRow = 21;
        for (int l = 0; l < ceList.length; l++) {
            double alpha = ceList[l];
            int prediction = 0;
            if (alpha == 1)
                prediction = list.size() - 1;
            else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPercentageofLoc() >= alpha) {
                        prediction = i;
                        break;
                    }
                }
            }
            DataEntry e1 = list.get(prediction);
            DataEntry e2 = list.get(prediction - 1);
            double x1 = e1.getPercentageofLoc();
            double x2 = e2.getPercentageofLoc();
            double y1 = e1.getPercentageofBug();
            double y2 = e2.getPercentageofBug();
            double estimatedPBug = y1 + ((alpha - x1) * (y2 - y1) / (x2 - x1));
            double aucBx1andx2 = ((x1 - x2) * (y1 + y2)) / 2;
            double aucx1andAlpha = (alpha - x1) * (y1 + estimatedPBug) / 2;
            double aucb0andfirst = list.get(0).getPercentageofLoc() * list.get(0).getPercentageofBug() / 2;
            sheet.getRow(5).createCell(cn).setCellValue("X1");
            sheet.getRow(6).createCell(cn).setCellValue(x1);
            sheet.getRow(7).createCell(cn).setCellValue("X2");
            sheet.getRow(8).createCell(cn).setCellValue(x2);
            sheet.getRow(9).createCell(cn).setCellValue("Y1");
            sheet.getRow(10).createCell(cn).setCellValue(y1);
            sheet.getRow(11).createCell(cn).setCellValue("Y2");
            sheet.getRow(12).createCell(cn).setCellValue(y2);
            sheet.getRow(13).createCell(cn).setCellValue("Estimated Percentage of bugs (y)");
            sheet.getRow(14).createCell(cn).setCellValue(estimatedPBug);
            sheet.getRow(15).createCell(cn).setCellValue("AUC between x1 and x2");
            sheet.getRow(16).createCell(cn).setCellValue(aucBx1andx2);
            sheet.getRow(17).createCell(cn).setCellValue("AUC between x1 and alpha");
            sheet.getRow(18).createCell(cn).setCellValue(aucx1andAlpha);
            sheet.getRow(19).createCell(cn).setCellValue("AUC between 0 and firstPoint");
            sheet.getRow(20).createCell(cn).setCellValue(aucb0andfirst);
            double totalarea = 0;
            for (int k = 0; k <= prediction; k++) {
                totalarea += list.get(k).getArea();
            }
            double finalUacforAlpha;
            if (alpha == 1)
                finalUacforAlpha = totalarea + aucb0andfirst;
            else
                finalUacforAlpha = totalarea - aucBx1andx2 + aucx1andAlpha + aucb0andfirst;

            sheet.getRow(lastRow).createCell(cn).setCellValue("Final AUC at alpha=" + alpha);
            sheet.getRow(lastRow + 1).createCell(cn).setCellValue(finalUacforAlpha);
            double alphaarea = alpha * alpha / 2;
            double CEforalpha = (finalUacforAlpha - alphaarea) / (uacForAlphas[l] - alphaarea);
            ceSheet.getRow(predictionIndex + 1).createCell(l + 1).setCellValue(CEforalpha);
            sheet.getRow(lastRow + 2).createCell(cn).setCellValue("Ce for alpha=" + alpha);
            sheet.getRow(lastRow + 3).createCell(cn).setCellValue(CEforalpha);
            cn++;
        }

        return rownum;
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

    public void createResult(List<DataEntry> testList, String s, double[] ceList) throws Exception {
        createNumericalSheet(testList, s, ceList);
    }
}
