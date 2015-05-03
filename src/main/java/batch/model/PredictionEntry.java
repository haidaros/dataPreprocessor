package batch.model;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eg on 29/04/15.
 */
public class PredictionEntry {
    String className;
    int loc;
    int bug;
    double percentageLoc;
    double percentageBug;
    double density;
    List<Prediction> predictions = new LinkedList<Prediction>();
    List<String> predictionNames = new LinkedList<String>();


    public PredictionEntry() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getBug() {
        return bug;
    }

    public void setBug(int bug) {
        this.bug = bug;
    }

    public double getPercentageLoc() {
        return percentageLoc;
    }

    public void setPercentageLoc(double percentageLoc) {
        this.percentageLoc = percentageLoc;
    }

    public double getPercentageBug() {
        return percentageBug;
    }

    public void setPercentageBug(double percentageBug) {
        this.percentageBug = percentageBug;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public String[] getProneArray() {
        String[] array = new String[3 + predictionNames.size() * 2];
        array[0] = className;
        array[1] = String.valueOf(loc);
        array[2] = String.valueOf(bug);
        for (int i = 0; i < predictionNames.size() ; i++) {
            array[3 + i * 2] = String.valueOf(predictions.get(i).getPrediction());
            array[4 + i * 2] = String.valueOf(predictions.get(i).getPredictionProbability());
        }
        return array;
    }

    public String[] getArray() {
        String[] array = new String[3 + predictionNames.size()];
        array[0] = className;
        array[1] = String.valueOf(loc);
        array[2] = String.valueOf(bug);
        for (int i = 0; i < predictionNames.size(); i++) {
            array[3 + i] = String.valueOf(new DecimalFormat("##.##").format(predictions.get(i).getPrediction()));
        }
        return array;
    }

    public String[] getHeaderArray() {
        String[] array = new String[3 + predictionNames.size()];
        array[0] = "ClassName";
        array[1] = "Loc";
        array[2] = "Bug";
        for (int i = 0; i < predictionNames.size(); i++) {
            array[3 + i] = predictionNames.get(i);
        }
        return array;
    }

    public String[] getHeaderProneArray() {
        String[] array = new String[3 + predictionNames.size() * 2];
        array[0] = "ClassName";
        array[1] = "Loc";
        array[2] = "Bug";
        for (int i = 0; i < predictionNames.size();i++) {
            array[3 + i * 2] = predictionNames.get(i);
            array[4 + i * 2] = "Probabilty";
        }
        return array;
    }

    public void addPrediction(Prediction prediction) {
        predictions.add(prediction);
    }

    public void addPredictionHeader(String classifierName) {
        predictionNames.add(classifierName);
    }
}
