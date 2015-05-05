package batch.output;

import batch.model.OutputData;
import batch.model.OutputEntry;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemWriter;
import util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by eg on 04/05/15.
 */
public class OutputWriter implements ItemWriter<OutputData> {
    public void write(List<? extends OutputData> outputDatas) throws Exception {
        String mainfolderName = new File(ResourceUtils.getConfig().getString("input-path")).getParentFile().getAbsolutePath() + "/output";
        new File(mainfolderName).mkdir();
        List<Object> ceList = ResourceUtils.getConfig().getList("outputs.ceList.value");
        double[] ces = new double[ceList.size()];
        for (int k = 0; k < ceList.size(); k++) {
            ces[k] = Double.parseDouble(String.valueOf(ceList.get(k)));
        }
        for (OutputData o : outputDatas) {
            createCSVs(o, mainfolderName, ces);
        }
    }

    private void createCSVs(OutputData o, String mainfolderName, double[] ces) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        //----Chart Start----->
        Drawing drawing = wb.createSheet("Chart").createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 1, 17, 35);
        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP);
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        ScatterChartData chartRow = chart.getChartDataFactory().createScatterChartData();
        //----Chart End----->
        List<String> predictionNames = o.getList().get(0).getPredictionNames();
        String projectName = o.getFile().getParentFile().getName();
        new File(mainfolderName + "/" + projectName).mkdir();
        getHeader(getFileMode(o.getFile()));
        String fileName = o.getFile().getName().substring(0, o.getFile().getName().indexOf(".csv")) + "-result-";
        fileName = mainfolderName + "/" + projectName + "/" + fileName;
        //----Optimal Creation----->
        createOptimal(wb, "Optimal", fileName, o, getHeader(0), chartRow);
        double[] uacForAlphas = calculateCe(o.getList(), ces, true, null);
        o.setCeList(ces);
        o.setCeResult(new double[predictionNames.size()][ces.length]);
        //----Other Classifiers Creation----->
        for (int i = 0; i < predictionNames.size(); i++) {
            createForaclassfier(wb, predictionNames.get(i), fileName, o, getHeader(getFileMode(o.getFile())), i, chartRow);
            o.addCeResult(calculateCe(o.getList(), ces, false, uacForAlphas), i);
        }
        writeCe(o, wb, fileName);
        chart.plot(chartRow, bottomAxis, leftAxis);
        FileOutputStream out = new FileOutputStream(new File(fileName + "all.xls"));
        wb.write(out);
        out.close();
    }

    private String[] getHeader(int fileMode) {
        if (fileMode == 0) {
            return new String[]{
                    "ClassName", "Lines of Code", "Actual number of Bugs", "Percentage of Locs", "Percentage of Bug", "Density", "Area"
            };
        } else if (fileMode == 1) {
            return new String[]{"ClassName", "Lines of Code", "Actual number of Bugs", "Percentage of Locs", "Percentage of Bug", "", "Prediction Density", "Area"};
        } else if (fileMode == 2) {
            return new String[]{"ClassName", "Lines of Code", "Actual number of Bugs", "Percentage of Locs", "Percentage of Bug", "Prediction Density", "Area"};
        } else if (fileMode == 3) {
            return new String[]{"ClassName", "Lines of Code", "Actual number of Bugs", "Percentage of Locs", "Percentage of Bug", "Prediction", "Area"};
        } else {
            return new String[]{"ClassName", "Lines of Code", "Actual number of Bugs", "Percentage of Locs", "Percentage of Bug", "Prediction", "Prediction Probability", "Area"};
        }
    }

    private void writeCe(OutputData o, XSSFWorkbook wb, String filename) throws IOException {
        XSSFSheet sheet = wb.createSheet("ceResult");
        CSVWriter writer = new CSVWriter(new FileWriter(filename + "ceResult.csv"), ';');
        String[] ceHeader = o.getCeHeader();
        writer.writeNext(ceHeader);
        writeArray(sheet, 0, ceHeader);
        for (int i = 0; i < o.getList().get(0).getPredictionNames().size(); i++) {
            String[] ceValueRow = o.getCeValueRow(i);
            writer.writeNext(ceValueRow);
            writeArray(sheet, i + 1, ceValueRow);
        }
        writer.close();
    }

    private void createOptimal(XSSFWorkbook wb, String sheetName, String fileName, OutputData o, String[] header, ScatterChartData chartRow) throws IOException {
        XSSFSheet sheet = wb.createSheet(sheetName);
        String pFileName = fileName + sheetName + ".csv";
        CSVWriter writer = new CSVWriter(new FileWriter(pFileName), ';');
        Collections.sort(o.getList(), new OptimalComparer());
        fillPercentage(o.getList(), o.getTotalBug(), o.getTotalLoc());
        calculateAreaBelow(o.getList());
        //CSV HEADER
        writer.writeNext(header);
        int rowNum = 0;
        writeArray(sheet, rowNum++, header);
        for (OutputEntry entry : o.getList()) {
            String[] values = entry.getArray(0, 0);
            writer.writeNext(values);
            writeArray(sheet, rowNum++, values);
        }
        writer.close();
        fillFormulate(sheet, sheetName, rowNum, chartRow);
    }

    private void createForaclassfier(XSSFWorkbook wb, String sheetName, String fileName, OutputData o, String[] header, int predictionOrder, ScatterChartData chartRow) throws IOException {
        XSSFSheet sheet = wb.createSheet(sheetName);
        String pFileName = fileName + sheetName + ".csv";
        CSVWriter writer = new CSVWriter(new FileWriter(pFileName), ';');
        int fileMode = getFileMode(o.getFile());
        sortFile(o, predictionOrder, fileMode);
        fillPercentage(o.getList(), o.getTotalBug(), o.getTotalLoc());
        calculateAreaBelow(o.getList());
        //CSV HEADER
        writer.writeNext(header);
        int rowNum = 0;
        writeArray(sheet, rowNum++, header);
        for (OutputEntry entry : o.getList()) {
            String[] values = entry.getArray(predictionOrder, fileMode);
            writer.writeNext(values);
            writeArray(sheet, rowNum++, values);
        }
        writer.close();
        fillFormulate(sheet, sheetName, rowNum, chartRow);
    }

    private void sortFile(OutputData o, int predictionOrder, int fileMode) {
        if (fileMode == 1) {
            Collections.sort(o.getList(), new DensityComparer(predictionOrder));
        } else if (fileMode == 2) {
            Collections.sort(o.getList(), new PredictionComparer(predictionOrder));
        } else if (fileMode == 3) {
            Collections.sort(o.getList(), new PredictionComparer(predictionOrder));
        } else {
            Collections.sort(o.getList(), new ProneComparer(predictionOrder));
        }
    }

    private double[] calculateCe(List<OutputEntry> list, double[] ceList, boolean isOptimal, double[] uacForAlphas) {
        double[] result = new double[ceList.length];
        for (int k = 0; k < ceList.length; k++) {
            double alpha = ceList[k];
            int prediction = 0;
            if (alpha == 1)
                prediction = list.size() - 1;
            else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPercentageLoc() >= alpha) {
                        prediction = i;
                        break;
                    }
                }
            }
            OutputEntry e1 = list.get(prediction);
            OutputEntry e2 = list.get(prediction - 1);
            double x1 = e1.getPercentageLoc();
            double x2 = e2.getPercentageLoc();
            double y1 = e1.getPercentageBug();
            double y2 = e2.getPercentageBug();
            double estimatedPBug = y1 + ((alpha - x1) * (y2 - y1) / (x2 - x1));
            double aucBx1andx2 = ((x1 - x2) * (y1 + y2)) / 2;
            double aucx1andAlpha = (alpha - x1) * (y1 + estimatedPBug) / 2;
            double aucb0andfirst = list.get(0).getPercentageLoc() * list.get(0).getPercentageBug() / 2;
            double totalarea = 0;
            for (int l = 0; l <= prediction; l++) {
                totalarea += list.get(l).getArea();
            }
            double finalUacforAlpha;
            if (alpha == 1)
                finalUacforAlpha = totalarea + aucb0andfirst;
            else
                finalUacforAlpha = totalarea - aucBx1andx2 + aucx1andAlpha + aucb0andfirst;
            if (isOptimal) {
                result[k] = finalUacforAlpha;
            } else {
                double alphaarea = alpha * alpha / 2;
                double CEforalpha = (finalUacforAlpha - alphaarea) / (uacForAlphas[k] - alphaarea);
                result[k] = CEforalpha;
            }
        }
        return result;

    }

    private void writeArray(XSSFSheet sheet, int rowNum, String[] values) {
        XSSFRow row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            try {
                row.createCell(i).setCellValue(Double.valueOf(values[i]));
            } catch (Exception ex) {
                row.createCell(i).setCellValue(values[i]);
            }
        }
    }

    private void fillPercentage(List<OutputEntry> list, int totalBug, int totalLoc) {
        int countLoc = 0;
        int countBug = 0;
        for (OutputEntry o : list) {
            countBug += o.getBug();
            countLoc += o.getLoc();
            o.setPercentageBug((double) countBug / (double) totalBug);
            o.setPercentageLoc((double) countLoc / (double) totalLoc);
        }
    }

    private void calculateAreaBelow(List<OutputEntry> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            OutputEntry entry1 = list.get(i);
            OutputEntry entry2 = list.get(i + 1);
            double area = ((entry2.getPercentageLoc() - entry1.getPercentageLoc()) * (entry1.getPercentageBug() + entry2.getPercentageBug())) / 2;
            entry1.setArea(area);
        }
    }

    private void fillFormulate(XSSFSheet sheet, String sheetName, int rowNum, ScatterChartData data) {
        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, rowNum - 1, 3, 3));
        ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, rowNum - 1, 4, 4));
        ScatterChartSeries scatterChartSeries = data.addSerie(xs, ys);
        scatterChartSeries.setTitle(sheetName);
    }

    /*
    * return the mode of file
    * 1 = numberofbugs
    * 2 = Density
    * 3 = Class
    * 4 = Prone
    * */
    private int getFileMode(File file) {
        if (file.getName().contains("bug-count.csv")) {
            return 1;
        } else if (file.getName().contains("bug-density.csv")) {
            return 2;
        } else if (file.getName().contains("buggy-class.csv")) {
            return 3;
        } else {
            return 4;
        }
    }
}
