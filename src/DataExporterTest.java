import java.io.File;
import java.io.FileInputStream;

/**
 * Created by eg on 24/02/15.
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
        File inputFile = new File("/Users/eg/Desktop/Projects/dataPreprocessor/datasets/equinox/change-metrics.xls");
        FileInputStream file = new FileInputStream(inputFile);
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        de = new DataExporter(0.25, "bugs", file, exclusion);
        de.process();
        de.getTrainingExcel("trainingExcel.xls");
        de.getTestExcel("testExcel.xls");
        de.getTestCSV("textCSV");
        System.out.println("file = " + file);
    }
}
