package batch.model;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eg on 04/05/15.
 */
public class OutputEntry {
    String className;
    int loc;
    int bug;
    double percentageLoc;
    double percentageBug;
    double density;
    double area;
    List<Prediction> predictions = new LinkedList<Prediction>();
    List<String> predictionNames = new LinkedList<String>();

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

    public List<String> getPredictionNames() {
        return predictionNames;
    }

    public void setPredictionNames(List<String> predictionNames) {
        this.predictionNames = predictionNames;
    }

    public void addPrediction(Prediction prediction) {
        predictions.add(prediction);
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String[] getArray(int i, int fileMode) {
        if (fileMode == 0)
            return getOptimalArray();
        else if (fileMode == 1) {
            return getArray(i);
        } else {
            return getDensityandClassArray(i);
        }
    }

    public String[] getArray(int i) {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String[] array = new String[8];
        array[0] = className;
        array[1] = String.valueOf(loc);
        array[2] = String.valueOf(bug);
        array[3] = String.valueOf(decimalFormat.format(percentageLoc));
        array[4] = String.valueOf(decimalFormat.format(percentageBug));
        array[5] = String.valueOf(predictions.get(i).getPrediction());
        array[6] = String.valueOf(decimalFormat.format(predictions.get(i).getPredictionDensity()));
        array[7] = String.valueOf(area);
        return array;
    }

    public String[] getDensityandClassArray(int i) {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String[] array = new String[7];
        array[0] = className;
        array[1] = String.valueOf(loc);
        array[2] = String.valueOf(bug);
        array[3] = String.valueOf(decimalFormat.format(percentageLoc));
        array[4] = String.valueOf(decimalFormat.format(percentageBug));
        array[5] = String.valueOf(predictions.get(i).getPrediction());
        array[6] = String.valueOf(area);
        return array;
    }

    public String[] getOptimalArray() {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String[] array = new String[7];
        array[0] = className;
        array[1] = String.valueOf(loc);
        array[2] = String.valueOf(bug);
        array[3] = String.valueOf(decimalFormat.format(percentageLoc));
        array[4] = String.valueOf(decimalFormat.format(percentageBug));
        array[5] = String.valueOf(decimalFormat.format(density));
        array[6] = String.valueOf(area);
        return array;
    }
}
