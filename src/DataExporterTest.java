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
        OutputFileCreator outputFileCreator = new OutputFileCreator();
        outputFileCreator.createResultFile(de.testList);
    }
}
