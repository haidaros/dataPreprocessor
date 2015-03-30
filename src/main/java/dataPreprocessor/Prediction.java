package dataPreprocessor;

/**
 * Created by eg on 29/03/15.
 */
public class Prediction {
    String name;
    double prediction;
    double predictionDensinty;

    public Prediction() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public double getPredictionDensinty() {
        return predictionDensinty;
    }

    public void setPredictionDensinty(double predictionDensinty) {
        this.predictionDensinty = predictionDensinty;
    }
}
