package dataPreprocessor;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by eg on 03/03/15.
 */
public class WekaInterface {
    Classifier classifier;
    Instances trainingData;
    Instances testData;

    wekaMode mode;


    enum wekaMode {
        SMOreg,
        LinearRegression,
        IBK,
        SMO,
        RandomForest,
        MultiplayerPerception,
        J48
    }

    ;

    public WekaInterface(String testFileName, String trainingFileName, wekaMode mode) throws Exception {
        ConverterUtils.DataSource training = new ConverterUtils.DataSource(trainingFileName);
        ConverterUtils.DataSource test = new ConverterUtils.DataSource(testFileName);
        //**
        trainingData = training.getDataSet();
        testData = test.getDataSet();
        //**
        trainingData.setClassIndex(trainingData.numAttributes() - 1);
        testData.setClassIndex(testData.numAttributes() - 1);

        this.mode = mode;
        //**
        switch (mode) {
            case SMOreg:
                classifier = new SMOreg();
                break;
            case LinearRegression:
                classifier = new LinearRegression();
                break;
            case IBK:
                classifier = new IBk();
                break;
            case SMO:
                classifier = new SMO();
                break;
            case RandomForest:
                classifier = new RandomForest();
                break;
            case MultiplayerPerception:
                classifier = new MultilayerPerceptron();
                break;
            case J48:
                classifier = new J48();
                break;
            default:
                throw new Exception("You have to choose a weka mode");
        }

    }

    public void train() throws Exception {
        classifier.buildClassifier(trainingData);
    }

    public void fillPredictionData(List<DataEntry> testList, DataExporter.Mode mode) throws Exception {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = 0; i < testData.numInstances(); i++) {
            Double pred = classifier.classifyInstance(testData.instance(i));
            Prediction prediction = new Prediction();
            prediction.setName(this.mode.toString());
            prediction.setPrediction(pred);
            if (mode == DataExporter.Mode.NOBUGS) {
                int loc = testList.get(i).getLoc();
                prediction.setPredictionDensinty(loc == 0 ? 0 : (1000 * (pred / loc)));
            } else
                prediction.setPredictionDensinty(pred);
            testList.get(i).addPrediction(prediction);
        }
    }

    public void fillDensityPrediction(List<DataEntry> testList) throws Exception {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = 0; i < testData.numInstances(); i++) {
            Double pred = classifier.classifyInstance(testData.instance(i));
            Prediction prediction = new Prediction();
            prediction.setName(mode.toString());
            prediction.setPrediction(pred);
            prediction.setPredictionDensinty(pred);
            testList.get(i).addPrediction(prediction);
        }
    }

    //Old methods
    public void fillLinearRegression(List<DataEntry> testList) throws Exception {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = 0; i < testData.numInstances(); i++) {
            Double pred = classifier.classifyInstance(testData.instance(i));
            testList.get(i).setPrediction(pred);
            int loc = testList.get(i).getLoc();
            testList.get(i).setPredictionDensinty(loc == 0 ? 0 : (1000 * (pred / loc)));
        }
    }

    public void fillIBK(List<DataEntry> testList) throws Exception {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = 0; i < testData.numInstances(); i++) {
            Double pred = classifier.classifyInstance(testData.instance(i));
            testList.get(i).setIbkprediction(pred);
            int loc = testList.get(i).getLoc();
            testList.get(i).setIbkpredictionDensinty(loc == 0 ? 0 : (1000 * (pred / loc)));
        }
    }

    public void fillSVM(List<DataEntry> testList) throws Exception {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = 0; i < testData.numInstances(); i++) {
            Double pred = classifier.classifyInstance(testData.instance(i));
            testList.get(i).setSvmprediction(pred);
            int loc = testList.get(i).getLoc();
            testList.get(i).setSvmpredictionDensinty(loc == 0 ? 0 : (1000 * (pred / loc)));
        }
    }
}
