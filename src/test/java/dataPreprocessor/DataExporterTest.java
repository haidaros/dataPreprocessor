package dataPreprocessor;

import org.junit.Test;
import util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by eg on 24/02/15.
 * todo
 */
public class DataExporterTest {
    @Test
    public void testDataExporterNoBugs() throws Exception {
        File f = ResourceUtils.getFile("datasets/mylyn/single-version-ck-oo.xls");
        FileInputStream file = new FileInputStream(f);
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        DataExporter dataExporter = new DataExporter(0.25, "bugs", file, exclusion);
        dataExporter.setModeofDataExporter(DataExporter.Mode.NOBUGS);
        dataExporter.process();
        System.out.println("exclusion = " + exclusion);
        dataExporter.getTestCSV("test.csv");
        dataExporter.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.LinearRegression);
        wk.train();
        wk.fillLinearRegression(dataExporter.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBK);
        wk.train();
        wk.fillIBK(dataExporter.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.SMOreg);
        wk.train();
        wk.fillSVM(dataExporter.testList);
        OutputFileCreator outputFileCreator = new OutputFileCreator();
        outputFileCreator.createResultFile(dataExporter.testList);
    }

    @Test
    public void csvTest() throws Exception {
        File f = ResourceUtils.getFile("datasets/mylyn/single-version-ck-oo.csv");
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        DataExporter dataExporter = new DataExporter("bugs", f);
        dataExporter.setModeofDataExporter(DataExporter.Mode.NOBUGS);
        dataExporter.process();
        System.out.println("exclusion = " + exclusion);
        dataExporter.getTestCSV("test.csv");
        dataExporter.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.LinearRegression);
        wk.train();
        wk.fillLinearRegression(dataExporter.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBK);
        wk.train();
        wk.fillIBK(dataExporter.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.SMOreg);
        wk.train();
        wk.fillSVM(dataExporter.testList);
        OutputFileCreator outputFileCreator = new OutputFileCreator();
        outputFileCreator.createResultFile(dataExporter.testList);
    }
}
