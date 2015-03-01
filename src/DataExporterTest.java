import java.io.File;
import java.io.FileInputStream;

/**
 * Created by eg on 24/02/15.
 * todo
 * data comparer
 * result excel creator
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
        File inputFile = new File("/Users/eg/Desktop/Projects/dataPreprocessor/datasets/mylyn/change-metrics.xls");
        FileInputStream file = new FileInputStream(inputFile);
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs,classname";
        de = new DataExporter(0.25, "bugs", file, exclusion);
        de.process();
//        de.getTrainingExcel("trainingExcel.xls");
//        de.getTestExcel("testExcel.xls");
        de.getTestCSV("test.csv");
        de.getTrainingCSV("training.csv");
        System.out.println("file = " + file);
    }
}
