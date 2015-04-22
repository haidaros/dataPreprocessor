package dataPreprocessor;

import org.junit.Test;
import util.ResourceUtils;

import java.io.File;

/**
 * Created by eg on 24/02/15.
 */
public class DataExporterTest {
    //Test Mode: Number of Bugs
    //SMoreg, Linear Regression , IBK
    //Todo
    //check the excel chart
    double[] ceList = {0.2, 1};

    @Test
    public void testModeNoBugs() throws Exception {
        File f = ResourceUtils.getFile("datasets/mylyn/single-version-ck-oo.csv");
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        DataExporter dataExporter = new DataExporter("bugs", f);
        dataExporter.setModeofDataExporter(DataExporter.Mode.NOBUGS);
        dataExporter.process();
        dataExporter.getTestCSV("test.csv");
        dataExporter.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.SMOreg);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.LinearRegression);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBk);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        OutputFileCreator outputFileCreator = new OutputFileCreator(dataExporter.modeofDataExporter);
        outputFileCreator.createNumericalSheet(dataExporter.testList, "resultNoBugs.xls", ceList);
    }

    //Test Mode: Bug Density
    //SMoreg, Linear Regression , IBK
    @Test
    public void testBugDensity() throws Exception {
        File f = ResourceUtils.getFile("datasets/mylyn/single-version-ck-oo.csv");
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        DataExporter dataExporter = new DataExporter("bugs", f);
        dataExporter.setModeofDataExporter(DataExporter.Mode.BUGDENSITY);
        dataExporter.process();
        dataExporter.getTestCSV("test.csv");
        dataExporter.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.SMOreg);
        wk.train();
        wk.fillDensityPrediction(dataExporter.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.LinearRegression);
        wk.train();
        wk.fillDensityPrediction(dataExporter.testList);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBk);
        wk.train();
        wk.fillDensityPrediction(dataExporter.testList);
        OutputFileCreator outputFileCreator = new OutputFileCreator(dataExporter.getModeofDataExporter());
        outputFileCreator.createNumericalSheet(dataExporter.testList, "resultDensity.xls", ceList);
    }

    //Test Mode: Class
    //RandomForestm,SMO,IBK,MultiplayerPerception,
    @Test
    public void testModeClass() throws Exception {
        File f = ResourceUtils.getFile("datasets/mylyn/single-version-ck-oo.csv");
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        DataExporter dataExporter = new DataExporter("bugs", f);
        dataExporter.setModeofDataExporter(DataExporter.Mode.CLASS);
        dataExporter.process();
        dataExporter.getTestCSV("test.csv");
        dataExporter.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.RandomForest);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.SMO);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBk);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.MultilayerPerceptron);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, dataExporter.modeofDataExporter);
        OutputFileCreator outputFileCreator = new OutputFileCreator(dataExporter.modeofDataExporter);
        outputFileCreator.createResult(dataExporter.testList, "resultClass.xls", ceList);
    }

    //Test Mode: Bug Proneness
    //RandomForest , J48 , IBK
    //todo
    //what we should give weka
    @Test
    public void testBugProneness() throws Exception {
        File f = ResourceUtils.getFile("datasets/lucene/single-version-ck-oo.csv");
        String exclusion = "nonTrivialBugs,majorBugs,criticalBugs,highPriorityBugs";
        DataExporter dataExporter = new DataExporter("bugs", f);
        dataExporter.setModeofDataExporter(DataExporter.Mode.CLASS);
        dataExporter.process();
        dataExporter.getTestCSV("test.csv");
        dataExporter.getTrainingCSV("training.csv");
        WekaInterface wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.J48);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, DataExporter.Mode.BUGPRONENESS);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.RandomForest);
        wk.train();
        wk.fillPredictionData(dataExporter.testList, DataExporter.Mode.BUGPRONENESS);
        wk = new WekaInterface("test.csv", "training.csv", WekaInterface.wekaMode.IBk);
        wk.train();
        wk.fillPredictionData(dataExporter.testList,DataExporter.Mode.BUGPRONENESS);
        OutputFileCreator outputFileCreator = new OutputFileCreator(DataExporter.Mode.BUGPRONENESS);
        outputFileCreator.createResult(dataExporter.testList, "resultProne.xls", ceList);
    }
}
