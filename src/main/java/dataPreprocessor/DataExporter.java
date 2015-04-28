package dataPreprocessor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by eg on 24/02/15.
 * //todo
 * merge two file
 * //delete loc and bug = 0  records
 */
public class DataExporter {
    static double defaultTestRate = 0.25;

    double testRate;
    String bugTableHeader = "bugs";
    String bugDensityHeader = "BugDensity";
    String buggyHeader = "Buggy";
    String locHeader = "numberOfLinesOfCode";
    String classnameHeader = "classname";
    int bugColumnIndex;
    List<Integer> zeroIndexs;
    List<Integer> bugyIndexs;
    List<DataEntry> classList;
    List<DataEntry> testList;
    List<DataEntry> trainingList;
    String[] excludedColumns;
    List<Column> columnNames;
    HSSFWorkbook workbook;
    File csvFile;
    //Mode
    Mode modeofDataExporter = Mode.NOBUGS;

    DecimalFormat decimalFormat = new DecimalFormat("#.##");


    public enum Mode {
        NOBUGS,
        BUGDENSITY,
        CLASS,
        BUGPRONENESS
    }

    public DataExporter(String bugTableHeader, File csvFile) throws IOException {
        this.testRate = defaultTestRate;
        this.bugTableHeader = bugTableHeader;
        excludedColumns = null;
        this.csvFile = csvFile;
    }

    public DataExporter(FileInputStream excelFile) throws IOException {
        this.testRate = defaultTestRate;
        this.bugTableHeader = null;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = null;
    }

    public DataExporter(File csvFile) throws IOException {
        this.testRate = defaultTestRate;
        this.bugTableHeader = null;
        excludedColumns = null;
        this.csvFile = csvFile;
    }

    public DataExporter(String bugTableHeader, FileInputStream excelFile) throws IOException {
        this.testRate = defaultTestRate;
        this.bugTableHeader = bugTableHeader;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = null;
    }

    public DataExporter(double testRate, String bugTableHeader, FileInputStream excelFile) throws IOException {
        this.testRate = testRate;
        this.bugTableHeader = bugTableHeader;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = null;
    }

    public DataExporter(String bugTableHeader, FileInputStream excelFile, String excludedHeaders) throws IOException {
        this.testRate = defaultTestRate;
        this.bugTableHeader = bugTableHeader;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = excludedHeaders.split(",");
    }

    public DataExporter(double testRate, FileInputStream excelFile, String excludedHeaders) throws IOException {
        this.testRate = testRate;
        this.bugTableHeader = null;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = excludedHeaders.split(",");
    }

    public DataExporter(double testRate, String bugTableHeader, FileInputStream excelFile, String excludedHeaders) throws IOException {
        this.testRate = testRate;
        this.bugTableHeader = bugTableHeader;
        workbook = new HSSFWorkbook(excelFile);
        excludedColumns = excludedHeaders.split(",");
    }

    public void process() throws Exception {
        if (csvFile == null)
            exportDatafromwb();
        else
            exportFromCSV();
        findIndexes();
        splitTestSet();
        replicateTrainingData();
        createTrainingSet();
    }

    private void exportFromCSV() throws FileNotFoundException {
        CSVReader reader = new CSVReader(new FileReader(csvFile), ';');
        Iterator<String[]> iterator = reader.iterator();
        exportColumnCSV(iterator.next());
        classList = new LinkedList<DataEntry>();
        Column bugDensity = new Column("BugDensity", bugColumnIndex + 1);
        Column buggy = new Column("Buggy", bugColumnIndex + 2);
        columnNames.add(bugDensity);
        columnNames.add(buggy);
        while (iterator.hasNext()) {
            String[] entry = iterator.next();
            DataEntry cl = new DataEntry();
            for (Column c : columnNames) {
                if (c.getKey().equals(buggyHeader)) {
                    cl.setBugy(cl.getBug() > 0 ? "Yes" : "No");
                } else if (c.getKey().equals(bugDensityHeader)) {
                    if (cl.getLoc() == 0) {
                        cl.setDensity(0.0);
                    } else {
                        cl.setDensity(1000 * ((double) cl.getBug() / cl.getLoc()));
                    }
                } else if (c.getKey().equals(locHeader)) {
                    cl.setLoc(Double.valueOf(entry[c.getValue()]).intValue());
                } else if (c.getKey().equals(classnameHeader)) {
                    cl.setClassName(entry[c.getValue()]);
                } else if (c.getKey().equals(bugTableHeader)) {
                    cl.setBug(Integer.valueOf(entry[c.getValue()].trim()));
                } else {
                    cl.addOther(c.getKey(), Double.valueOf(entry[c.getValue()].trim()));
                }
            }
            if (cl.getLoc() != 0)
                classList.add(cl);
        }
    }

    private void exportColumnCSV(String[] headerrow) {
        columnNames = new ArrayList<Column>();
        for (int i = 0; i < headerrow.length; i++) {
            boolean exclusion = false;
            String cname = headerrow[i].trim();
            if (excludedColumns != null) {
                for (String s : excludedColumns) {
                    if (cname.equals(s.trim())) {
                        exclusion = true;
                    }
                }
            }
            if (!exclusion && !cname.equals("")) {
                columnNames.add(new Column(cname, i));
            }
            if (cname.equals(buggyHeader)) {
                bugColumnIndex = i;
            }
        }
    }

    private void createTrainingSet() {
        //MainDataCreation
        trainingList = new LinkedList<DataEntry>();
        for (Integer i : zeroIndexs) {
            trainingList.add(classList.get(i));
        }
        for (Integer i : bugyIndexs) {
            trainingList.add(classList.get(i));
        }
    }

    private void splitTestSet() {
        //Spliting
        int balancedZeroCountinTest = (int) (zeroIndexs.size() * testRate);
        int balancedBugyCountinTest = (int) (bugyIndexs.size() * testRate);
        //Split Array//
        testList = new LinkedList<DataEntry>();
        Random r = new Random();
        //Test Data Creation
        for (int l = balancedZeroCountinTest; l > 0; l--) {
            int random = r.nextInt(zeroIndexs.size());
            testList.add(classList.get(zeroIndexs.get(random)));
            zeroIndexs.remove(random);
        }
        for (int l = balancedBugyCountinTest; l > 0; l--) {
            int random = r.nextInt(bugyIndexs.size());
            testList.add(classList.get(bugyIndexs.get(random)));
            bugyIndexs.remove(random);
        }
    }

    private void findIndexes() {
        zeroIndexs = new ArrayList<Integer>();
        bugyIndexs = new ArrayList<Integer>();
        for (int k = 0; k < classList.size(); k++) {
            DataEntry dataEntry = classList.get(k);
            if (dataEntry.getBug() == 0) {
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
            if (excludedColumns != null) {
                for (String s : excludedColumns) {
                    if (cname.equals(s.trim())) {
                        exclusion = true;
                    }
                }
            }
            if (!exclusion && !cname.equals("")) {
                columnNames.add(new Column(cname, i));
            }
            if (cname.equals(buggyHeader)) {
                bugColumnIndex = i;
            }
            i++;
        }
        while (cellIterator.hasNext());

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

    private void exportDatafromwb() {
        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        exportNecesseryColumns(rowIterator.next());
        classList = new LinkedList<DataEntry>();
        Column bugDensity = new Column("BugDensity", bugColumnIndex + 1);
        Column buggy = new Column("Buggy", bugColumnIndex + 2);
        columnNames.add(bugDensity);
        columnNames.add(buggy);
        while (rowIterator.hasNext()) {
            Row entry = rowIterator.next();
            DataEntry cl = new DataEntry();
            for (Column c : columnNames) {
                if (c.getKey().equals(buggyHeader)) {
                    cl.setBugy(cl.getBug() > 0 ? "Yes" : "No");
                } else if (c.getKey().equals(bugDensityHeader)) {
                    if (cl.getLoc() == 0) {
                        cl.setDensity(0.0);
                    } else {
                        cl.setDensity(1000 * ((double) cl.getBug() / cl.getLoc()));
                    }
                } else if (c.getKey().equals(locHeader)) {
                    cl.setLoc(Double.valueOf(entry.getCell(c.getValue()).getStringCellValue()).intValue());
                } else if (c.getKey().equals(classnameHeader)) {
                    cl.setClassName(entry.getCell(c.getValue()).getStringCellValue());
                } else if (c.getKey().equals(bugTableHeader)) {
                    cl.setBug((int) entry.getCell(c.getValue()).getNumericCellValue());
                } else {
                    try {
                        cl.addOther(c.getKey(), entry.getCell(c.getValue()).getNumericCellValue());
                    } catch (Exception ex) {
                        cl.addOther(c.getKey(), Double.valueOf(entry.getCell(c.getValue()).getStringCellValue()));
                    }
                }
            }
            classList.add(cl);
        }

    }

    public void getTestCSV(String outputfileName) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(outputfileName));
        List<String[]> data = new ArrayList<String[]>();
        data.add(testList.get(0).createHeaderFileforCSV(modeofDataExporter));
        for (DataEntry e : testList) {
            data.add(e.createDataForCSV(modeofDataExporter));
        }
        writer.writeAll(data, false);
        writer.close();
    }

    public void getTrainingCSV(String outputfileName) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(outputfileName));
        List<String[]> data = new ArrayList<String[]>();
        data.add(trainingList.get(0).createHeaderFileforCSV(modeofDataExporter));
        for (DataEntry e : trainingList) {
            data.add(e.createDataForCSV(modeofDataExporter));
        }
        writer.writeAll(data, false);
        writer.close();
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
        String[] headers = new String[columnNames.size() - 2];
        int i = 0;
        for (Column c : columnNames) {
            if (c.getKey().equals(bugTableHeader)) {
                if (modeofDataExporter == Mode.NOBUGS) {
                    headers[i] = c.getKey();
                    i++;
                }
            } else if (c.getKey().equals(bugDensityHeader)) {
                if (modeofDataExporter == Mode.BUGDENSITY) {
                    headers[i] = c.getKey();
                    i++;
                }
            } else if (c.getKey().equals(buggyHeader)) {
                if (modeofDataExporter == Mode.CLASS) {
                    headers[i] = c.getKey();
                    i++;
                }
            } else {
                headers[i] = c.getKey();
                i++;
            }
        }
        return headers;
    }

    private void fillExcelData(List<LinkedHashMap<String, String>> file, Sheet sh, Workbook wb) {
        int rownum = 1;
        DataFormat format = wb.createDataFormat();
        CellStyle style;
        style = wb.createCellStyle();
        for (Map<String, String> m : file) {
            Row row = sh.createRow(rownum);
            Iterator<String> iterator = m.values().iterator();
            for (int k = 0; iterator.hasNext(); k++) {
                Cell cell = row.createCell(k);
                cell.setCellValue(iterator.next());
                if (k > 0) {
                    style.setDataFormat(format.getFormat("0.0"));
                }
                cell.setCellStyle(style);
            }
            rownum++;
        }
    }

    public void createResultFile(List<Double> predictionList, String headers) throws Exception {
//        System.out.println("resultList = " + resultList.size());
        FileInputStream fis = new FileInputStream("templateResult.xls");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.getSheetAt(0);
        int rownum = 3;
        for (DataEntry e : testList) {
            Row row = sheet.createRow(rownum);
            Cell cell = row.createCell(1);
            cell.setCellValue(e.getLoc());
            cell = row.createCell(2);
            cell.setCellValue(e.getBug());
            cell = row.createCell(3);
            cell.setCellValue(e.getDensity());
            cell = row.createCell(4);
            cell.setCellValue(e.getBug());
        }
        FileOutputStream out =
                new FileOutputStream(new File("result.xls"));
        wb.write(out);
        out.close();
    }

    public Mode getModeofDataExporter() {
        return modeofDataExporter;
    }

    public void setModeofDataExporter(Mode modeofDataExporter) {
        this.modeofDataExporter = modeofDataExporter;
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

    public List<DataEntry> getTestList() {
        return testList;
    }

    public void setTestList(List<DataEntry> testList) {
        this.testList = testList;
    }
}
