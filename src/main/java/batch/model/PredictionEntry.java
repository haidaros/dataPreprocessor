package batch.model;

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
    double prediction;
    double predictionDensity;
    double predictionProbability;


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

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public double getPredictionDensity() {
        return predictionDensity;
    }

    public void setPredictionDensity(double predictionDensity) {
        this.predictionDensity = predictionDensity;
    }

    public double getPredictionProbability() {
        return predictionProbability;
    }

    public void setPredictionProbability(double predictionProbability) {
        this.predictionProbability = predictionProbability;
    }

    public String[] getProneArray() {
        return new String[]{className, String.valueOf(loc), String.valueOf(bug), String.valueOf(prediction), String.valueOf(predictionProbability)};
    }

    public String[] getArray() {
        return new String[]{className, String.valueOf(loc), String.valueOf(bug), String.valueOf(prediction)};
    }
}
