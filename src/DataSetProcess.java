import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.*;

/**
 * Created by eg on 18/02/15.
 */
public class DataSetProcess {
    static double testRate = 0.25;
    static String bugTableHeader = "bugs";

    public static void main(String[] args) throws Exception {
        //Reading File
        File inputFile = new File("/Users/eg/Desktop/Projects/dataPreprocessor/datasets/equinox/change-metrics.xls");
        FileInputStream file = new FileInputStream(inputFile);
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        Row columnnames = rowIterator.next();
        int numberofrows = columnnames.getLastCellNum();
        List<LinkedHashMap<String, String>> classes = new ArrayList<LinkedHashMap<String, String>>();
        //Creating Object List
        while (rowIterator.hasNext()) {
            Row entry = rowIterator.next();
            LinkedHashMap<String, String> obj = new LinkedHashMap<String, String>();
            for (int i = 0; i < numberofrows - 1; i++) {
                obj.put(columnnames.getCell(i).toString().trim(), entry.getCell(i).toString());
            }
            classes.add(obj);
        }
        System.out.println("classes.size() = " + classes.size());
        //Counting Zero Bugs classes
        List<Integer> zeroIndexs = new ArrayList<Integer>();
        List<Integer> bugyIndexs = new ArrayList<Integer>();
        for (int k = 0; k < classes.size(); k++) {
            LinkedHashMap<String, String> m = classes.get(k);
            if (Double.valueOf(m.get(bugTableHeader)) == 0) {
                zeroIndexs.add(k);
            } else {
                bugyIndexs.add(k);
            }
        }
        System.out.println("numberofZeroBugs = " + zeroIndexs.size());
        //Spliting
        int balancedZeroCountinTest = (int) (zeroIndexs.size() * testRate);
        int balancedBugyCountinTest = (int) (bugyIndexs.size() * testRate);

        System.out.println("balancedZeroCountinTest = " + balancedZeroCountinTest);
        //Split Array//
        List<LinkedHashMap<String, String>> testFile = new ArrayList<LinkedHashMap<String, String>>();
        Random r = new Random();
        //Test Data Creation

        for (int l = balancedZeroCountinTest; l > 0; l--) {
            int random = r.nextInt(zeroIndexs.size());
            testFile.add(classes.get(zeroIndexs.get(random)));
            classes.remove(zeroIndexs.get(random));
            zeroIndexs.remove(random);
        }

        for (int l = balancedBugyCountinTest; l > 0; l--) {
            int random = r.nextInt(bugyIndexs.size());
            testFile.add(classes.get(bugyIndexs.get(random)));
            classes.remove(bugyIndexs.get(random));
            bugyIndexs.remove(random);
        }
        System.out.println("testFile.size() = " + testFile.size());

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
        System.out.println("replicationofbugy = " + replicationofbugy + " replicationfozerobugs = " + replicationfozerobugs);
        //MainDataCreation
        List<LinkedHashMap<String, String>> mainFile = new ArrayList<LinkedHashMap<String, String>>();
        for (Integer i : zeroIndexs) {
            mainFile.add(classes.get(i));
        }
        for (Integer i : bugyIndexs) {
            mainFile.add(classes.get(i));
        }
        System.out.println("mainFile.size() = " + mainFile.size());
        //Writing to new Excel file
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet shtest = wb.createSheet("TestData");
        HSSFSheet shmain = wb.createSheet("MainData");
        //Creation Headers of Excel Data
        Row headerrowTest = shtest.createRow(0);
        Row headerrowMain = shmain.createRow(0);
        Iterator<org.apache.poi.ss.usermodel.Cell> headeriterator = columnnames.cellIterator();
        for (int i = 0; headeriterator.hasNext(); i++) {
            Cell next = headeriterator.next();
            Cell testcell = headerrowTest.createCell(i);
            Cell maincell = headerrowMain.createCell(i);
            testcell.setCellValue(next.getStringCellValue());
            maincell.setCellValue(next.getStringCellValue());
        }
        //Creating main Data
        int rownum = 1;
        for (Map<String, String> m : mainFile) {
            Row row = shmain.createRow(rownum);
            Iterator<String> iterator = m.values().iterator();
            for (int k = 0; iterator.hasNext(); k++) {
                Cell cell = row.createCell(k);
                cell.setCellValue(iterator.next());
            }
            rownum++;
        }
        //Creating Test Data
        rownum = 1;
        for (Map<String, String> m : testFile) {
            Row row = shtest.createRow(rownum);
            Iterator<String> iterator = m.values().iterator();
            for (int k = 0; iterator.hasNext(); k++) {
                Cell cell = row.createCell(k);
                cell.setCellValue(iterator.next());
            }
            rownum++;
        }
        try {
            FileOutputStream out =
                    new FileOutputStream(new File("new.xls"));
            wb.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
