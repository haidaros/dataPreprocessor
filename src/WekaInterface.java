import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by eg on 03/03/15.
 * todo
 * Random Forest from tree for YES / NO
 * Bayesian Logistic Regression
 * NaiveBayes
 * <p/>
 * //
 * SMOReg
 * LinearRegression
 * IBG
 */
public class WekaInterface {
    Classifier classifier;
    Instances trainingData;
    Instances testData;

    enum wekaMode {
        SMOreg,
        LinearRegression,
        IBK,
        Bayesian
    };

    public WekaInterface(String testFileName, String trainingFileName, wekaMode mode) throws Exception {
        ConverterUtils.DataSource training = new ConverterUtils.DataSource(trainingFileName);
        ConverterUtils.DataSource test = new ConverterUtils.DataSource(testFileName);
        //**
        trainingData = training.getDataSet();
        testData = test.getDataSet();
        //**
        trainingData.setClassIndex(trainingData.numAttributes() - 1);
        testData.setClassIndex(testData.numAttributes() - 1);
        //**

        if (mode == wekaMode.SMOreg) {
            classifier = new SMOreg();
        } else if (mode == wekaMode.Bayesian) {
            classifier = new NaiveBayesSimple();
        } else if (mode == wekaMode.LinearRegression) {
            classifier = new LinearRegression();
        } else if (mode == wekaMode.IBK) {
            classifier = new IBk();
        }

    }

    public void train() throws Exception {
        classifier.buildClassifier(trainingData);
    }

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
