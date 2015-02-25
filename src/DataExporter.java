import com.opencsv.CSVWriter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;

/**
 * Created by eg on 24/02/15.
 * //todo
 * merge two file
 */
public class DataExporter {
    double testRate;
    String bugTableHeader;
    List<Integer> zeroIndexs;
    List<Integer> bugyIndexs;
    List<LinkedHashMap<String, String>> classes;
    String[] excludedColumns;
    List<Column> columnNames;
    HSSFWorkbook workbook;
    //Result Sets;
    List<LinkedHashMap<String, String>> trainingSet;
    List<LinkedHashMap<String, String>> testSet;

    public DataExporter(double testRate, String bugTableHeader, FileInputStream excelFile, String excludedHeaders) throws IOException {
        this.testRate = testRate;
        this.bugTableHeader = bugTableHeader;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = excludedHeaders.split(",");
    }

    public void process() {
        exportDatafromwb();
        fingIndexes();
        splitTestSet();
        replicateTrainingData();
        createTrainingSet();
    }
    

    private void createTrainingSet() {
        //MainDataCreation
        trainingSet = new ArrayList<LinkedHashMap<String, String>>();
        for (Integer i : zeroIndexs) {
            trainingSet.add(classes.get(i));
        }
        for (Integer i : bugyIndexs) {
            trainingSet.add(classes.get(i));
        }
    }

    private void replicateTrainingData() {
        Random r = new Random();
        //Replication of data
        int replicationfozerobugs = 0;
        int replicationofbugy = 0;
        while (true) {
            if (zeroIndexs.size() > bugyIndexs.size()) {
                int random = r.nextInt(bugyIndexs.size());
                bugyIndexs.add(bugyIndexs.get(random));
                replicationofbugy++;
            } else if (zeroIndexs.size() < bugyIndexs.size()) {
                int random = r.nextInt(zeroIndexs.size());
                zeroIndexs.add(zeroIndexs.get(random));
                replicationfozerobugs++;
            } else {
                break;
            }
        }
    }

    private void splitTestSet() {
        //Spliting
        int balancedZeroCountinTest = (int) (zeroIndexs.size() * testRate);
        int balancedBugyCountinTest = (int) (bugyIndexs.size() * testRate);
        //Split Array//
        testSet = new ArrayList<LinkedHashMap<String, String>>();
        Random r = new Random();
        //Test Data Creation
        for (int l = balancedZeroCountinTest; l > 0; l--) {
            int random = r.nextInt(zeroIndexs.size());
            testSet.add(classes.get(zeroIndexs.get(random)));
            classes.remove(zeroIndexs.get(random));
            zeroIndexs.remove(random);
        }
        for (int l = balancedBugyCountinTest; l > 0; l--) {
            int random = r.nextInt(bugyIndexs.size());
            testSet.add(classes.get(bugyIndexs.get(random)));
            classes.remove(bugyIndexs.get(random));
            bugyIndexs.remove(random);
        }
    }

    private void fingIndexes() {
        zeroIndexs = new ArrayList<Integer>();
        bugyIndexs = new ArrayList<Integer>();
        for (int k = 0; k < classes.size(); k++) {
            LinkedHashMap<String, String> m = classes.get(k);
            if (Double.valueOf(m.get(bugTableHeader)) == 0) {
                zeroIndexs.add(k);
            } else {
                bugyIndexs.add(k);
            }
        }
    }

    private void exportNecesseryColumns(Row columnnames) {
        columnNames = new ArrayList<Column>();
        Iterator<Cell> cellIterator = columnnames.cellIterator();
        Integer i = 0;
        do {
            Cell cell = cellIterator.next();
            boolean exclusion = false;
            String cname = cell.getStringCellValue().trim();
            for (String s : excludedColumns) {
                if (cname.equals(s.trim())) {
                    exclusion = true;
                }
            }
            if (!exclusion && !cname.equals("")) {
                columnNames.add(new Column(cname, i));
            }
            i++;
        }
        while (cellIterator.hasNext());
    }

    private void exportDatafromwb() {
        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        exportNecesseryColumns(rowIterator.next());
        classes = new ArrayList<LinkedHashMap<String, String>>();
        while (rowIterator.hasNext()) {
            Row entry = rowIterator.next();
            LinkedHashMap<String, String> obj = new LinkedHashMap<String, String>();
            for (Column c : columnNames) {
                obj.put(c.getKey(), entry.getCell(c.getValue()).toString());
            }
            classes.add(obj);
        }
    }

    public double getTestRate() {
        return testRate;
    }

    public void setTestRate(double testRate) {
        this.testRate = testRate;
    }

    public String getBugTableHeader() {
        return bugTableHeader;
    }

    public void setBugTableHeader(String bugTableHeader) {
        this.bugTableHeader = bugTableHeader;
    }

    public void getTestCSV(String outputfileName) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(outputfileName));
        List<String[]> data = new ArrayList<String[]>();
        data.add(createHeadersforCSV());
        createContextforCSV(testSet, data);
        writer.writeAll(data);
        writer.close();
    }


    public void getTrainingCSV(String outputfileName) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(outputfileName));
        List<String[]> data = new ArrayList<String[]>();
        data.add(createHeadersforCSV());
        createContextforCSV(trainingSet, data);
        writer.writeAll(data);
        writer.close();
    }

    private void createContextforCSV(List<LinkedHashMap<String, String>> set, List<String[]> data) {
        for (Map<String, String> m : set) {
            String[] str = new String[m.size()];
            Iterator<String> iterator = m.values().iterator();
            for (int k = 0; iterator.hasNext(); k++) {
                str[k] = iterator.next();
            }
            data.add(str);
        }
    }


    public void getTestExcel(String outputfileName) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet shtest = wb.createSheet("testSet");
        createHeaders(shtest);
        fillExcelData(testSet, shtest, wb);
        FileOutputStream out =
                new FileOutputStream(new File(outputfileName));
        wb.write(out);
        out.close();
    }

    public void getTrainingExcel(String outputfileName) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet shtest = wb.createSheet("testSet");
        createHeaders(shtest);
        fillExcelData(testSet, shtest, wb);
        FileOutputStream out =
                new FileOutputStream(new File(outputfileName));
        wb.write(out);
        out.close();
    }

    private void createHeaders(HSSFSheet shtest) {
        Row headerRow = shtest.createRow(0);
        int i = 0;
        for (Column s : columnNames) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(s.getKey());
            i++;
        }
    }

    private String[] createHeadersforCSV() {
        String[] headers = new String[columnNames.size()];
        int i = 0;
        for (Column c : columnNames) {
            headers[i] = c.getKey();
            i++;
        }
        return headers;
    }

    private void fillExcelData(List<LinkedHashMap<String, String>> file, Sheet sh, Workbook wb) {
        int rownum = 1;
        DataFormat format = wb.createDataFormat();
        CellStyle style;
        for (Map<String, String> m : file) {
            Row row = sh.createRow(rownum);
            Iterator<String> iterator = m.values().iterator();
            for (int k = 0; iterator.hasNext(); k++) {
                Cell cell = row.createCell(k);
                cell.setCellValue(iterator.next());
                style = wb.createCellStyle();
                if (k > 0) {
                    style.setDataFormat(format.getFormat("0.0"));
                }
                cell.setCellStyle(style);
            }
            rownum++;
        }
    }
}
