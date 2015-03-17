package dataPreprocessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by eg on 11/03/15.
 */
public class DataEntry {

    static String bugDensityHeader = "BugDensity";
    static String buggyHeader = "Buggy";
    static String locHeader = "numberOfLinesOfCode";
    static String bugTableHeader = "bugs";

    String className;
    int bug;
    double density;
    //LinearRegressionTest
    double prediction;
    double predictionDensinty;
    //IBk
    double ibkprediction;
    double ibkpredictionDensinty;
    //SVM
    double svmprediction;
    double svmpredictionDensinty;
    int loc;
    String bugy;
    LinkedHashMap<String, Double> others;

    public DataEntry() {
        others = new LinkedHashMap<String, Double>();
    }

    public DataEntry(String className, int bug, double density, double prediction, int loc) {
        this.className = className;
        this.bug = bug;
        this.density = density;
        this.prediction = prediction;
        this.loc = loc;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getBug() {
        return bug;
    }

    public void setBug(int bug) {
        this.bug = bug;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public LinkedHashMap<String, Double> getOthers() {
        return others;
    }

    public void setOthers(LinkedHashMap<String, Double> others) {
        this.others = others;
    }

    public String getBugy() {
        return bugy;
    }

    public void setBugy(String bugy) {
        this.bugy = bugy;
    }

    public void addOther(String key, double numericCellValue) {
        others.put(key, numericCellValue);
    }

    public String[] createHeaderFileforCSV(DataExporter.Mode mode) {
        String[] str = new String[others.size() + 2];
        int i = 0;
        for (Map.Entry<String, Double> e : others.entrySet()) {
            str[i] = e.getKey();
            i++;
        }
        str[i++] = locHeader;
        if (mode == DataExporter.Mode.BUGDENSITY) {
            str[i] = bugDensityHeader;
        } else if (mode == DataExporter.Mode.CLASS) {
            str[i] = buggyHeader;
        } else if (mode == DataExporter.Mode.NOBUGS) {
            str[i] = bugTableHeader;
        }
        return str;
    }

    public String[] createDataForCSV(DataExporter.Mode mode) {
        String[] str = new String[others.size() + 2];
        int i = 0;
        for (Map.Entry<String, Double> e : others.entrySet()) {
            str[i] = String.valueOf(e.getValue());
            i++;
        }
        str[i++] = String.valueOf(loc);
        if (mode == DataExporter.Mode.BUGDENSITY) {
            str[i] = String.valueOf(density);
        } else if (mode == DataExporter.Mode.CLASS) {
            str[i] = bugy;
        } else if (mode == DataExporter.Mode.NOBUGS) {
            str[i] = String.valueOf(bug);
        }
        return str;
    }

    public boolean isBiggerDensity(DataEntry e) {
        if (density >= e.density)
            return true;
        return false;
    }

    public double getPredictionDensinty() {
        return predictionDensinty;
    }

    public void setPredictionDensinty(double predictionDensinty) {
        this.predictionDensinty = predictionDensinty;
    }

    public double getSvmpredictionDensinty() {
        return svmpredictionDensinty;
    }

    public void setSvmpredictionDensinty(double svmpredictionDensinty) {
        this.svmpredictionDensinty = svmpredictionDensinty;
    }

    public double getSvmprediction() {
        return svmprediction;
    }

    public void setSvmprediction(double svmprediction) {
        this.svmprediction = svmprediction;
    }

    public double getIbkpredictionDensinty() {
        return ibkpredictionDensinty;
    }

    public void setIbkpredictionDensinty(double ibkpredictionDensinty) {
        this.ibkpredictionDensinty = ibkpredictionDensinty;
    }

    public double getIbkprediction() {
        return ibkprediction;
    }

    public void setIbkprediction(double ibkprediction) {
        this.ibkprediction = ibkprediction;
    }
}
