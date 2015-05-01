package batch.prediction;

import batch.model.PredictionData;
import batch.model.PredictionEntry;
import batch.model.PredictionModel;
import com.opencsv.CSVReader;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.springframework.batch.item.ItemProcessor;
import util.ResourceUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by eg on 29/04/15.
 */
public class PredictionProcessor implements ItemProcessor<Map<File, File>, List<PredictionData>> {
    public List<PredictionData> process(Map<File, File> files) throws Exception {
        List<HierarchicalConfiguration> configList = ResourceUtils.getConfig().configurationsAt("predictions.mode");
        List<PredictionModel> modelList = new LinkedList<PredictionModel>();
        for (HierarchicalConfiguration c : configList) {
            PredictionModel predictionModel = new PredictionModel(c.getString("name"), c.getList("model"));
            modelList.add(predictionModel);
        }
        List<PredictionData> predictionDatas = new LinkedList<PredictionData>();
        for (Map.Entry<File, File> f : files.entrySet()) {
            predictionDatas.add(predictFile(f, modelList));
        }
        return predictionDatas;
    }

    private PredictionData predictFile(Map.Entry<File, File> f, List<PredictionModel> modelList) throws Exception {
        Map<File, List<PredictionEntry>> resultMap = new HashMap<File, List<PredictionEntry>>();
        PredictionData pd = new PredictionData();
        pd.setFile(f.getValue());
        if (f.getKey().getName().contains("bug-count")) {
            pd.setPredictionEntries(predict(f, findModel(modelList, "bug-count"), false));
        } else if (f.getKey().getName().contains("buggy-class")) {
            pd.setProne(true);
            pd.setPredictionEntries(predict(f, findModel(modelList, "buggy-class"), false));
            pd.setPronepredictionEntries(predict(f, findModel(modelList, "bug-proneness"), true));
        } else if (f.getKey().getName().contains("bug-density")) {
            pd.setPredictionEntries(predict(f, findModel(modelList, "bug-density"), false));
        }
        return pd;
    }

    private List<PredictionEntry> predict(Map.Entry<File, File> f, PredictionModel model, boolean isProne) throws Exception {
        for (String classifierName : model.getModes()) {
            System.out.println("f.getKey() = " + f.getKey());
            System.out.println("f.getValue() = " + f.getValue());
            ConverterUtils.DataSource training = new ConverterUtils.DataSource(f.getKey().getAbsolutePath());
            ConverterUtils.DataSource test = new ConverterUtils.DataSource(f.getValue().getAbsolutePath());
            //**
            Instances trainingDataSet = training.getDataSet();
            Instances testDataSet = test.getDataSet();
            //**
            trainingDataSet.setClassIndex(trainingDataSet.numAttributes() - 1);
            testDataSet.setClassIndex(testDataSet.numAttributes() - 1);
            //**
            Classifier classifier;
            Class clazz;
            try {
                clazz = Class.forName("weka.classifiers.functions." + classifierName);
                classifier = (Classifier) clazz.newInstance();
            } catch (Exception ex) {
                try {
                    clazz = Class.forName("weka.classifiers.trees." + classifierName);
                    classifier = (Classifier) clazz.newInstance();
                } catch (Exception e) {
                    clazz = Class.forName("weka.classifiers.lazy." + classifierName);
                    classifier = (Classifier) clazz.newInstance();
                }
            }
            if (classifier != null) {
                classifier.buildClassifier(trainingDataSet);
                List<PredictionEntry> predictionResult = new LinkedList<PredictionEntry>();
                int totals[] = countTotalBug(f.getValue(), predictionResult);
                for (int i = 0; i < testDataSet.numInstances(); i++) {
                    Double pred = classifier.classifyInstance(testDataSet.instance(i));
                    double[] probabilityandDistribution = classifier.distributionForInstance(testDataSet.instance(i));
                    PredictionEntry pe = predictionResult.get(i);
                    pe.setPrediction(pred);
                    pe.setPredictionDensity(pred / pe.getLoc() * 1000);
                    if (isProne) {
                        pe.setPredictionProbability(probabilityandDistribution[pred.intValue()]);
                    }
                }
                return predictionResult;
            } else {
                throw new Exception("One of the classifier name is not correct check it out:" + classifierName);
            }
        }
        return null;
    }

    private int[] countTotalBug(File file, List<PredictionEntry> predictionResult) throws FileNotFoundException {
        CSVReader reader = new CSVReader(new FileReader(file), ',');
        Iterator<String[]> iterator = reader.iterator();
        iterator.next(); //skip header column
        int totalLoc = 0;
        int totalBug = 0;
        while (iterator.hasNext()) {
            String[] next = iterator.next();
            PredictionEntry entry = new PredictionEntry();
            int bug = (int) Double.parseDouble(next[next.length - 1].trim());
            int loc = (int) Double.parseDouble(next[next.length - 2].trim());
            totalBug += bug;
            totalLoc += loc;
            entry.setBug(bug);
            entry.setLoc(loc);
            predictionResult.add(entry);
        }
        for (PredictionEntry pe : predictionResult) {
            pe.setPercentageLoc((double) pe.getLoc() / (double) totalLoc);
            pe.setPercentageBug((double) pe.getBug() / (double) totalBug);
            pe.setDensity(1000 * (double) pe.getBug() / (double) pe.getLoc());
        }
        int[] ints = new int[2];
        ints[0] = totalBug;
        ints[1] = totalLoc;
        return ints;
    }

    private PredictionModel findModel(List<PredictionModel> modelList, String s) {
        for (PredictionModel m : modelList) {
            if (s.equals(m.getPredictionName()))
                return m;
        }
        return null;
    }

}
