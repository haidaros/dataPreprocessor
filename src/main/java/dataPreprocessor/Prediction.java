package dataPreprocessor;

/**
 * Created by eg on 29/03/15.
 */
public class Prediction {
    String name;
    double prediction;//TODO what prediction??
    double predictionDensinty;
    double probability;
    double distribution;

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

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getDistribution() {
        return distribution;
    }

    public void setDistribution(double distribution) {
        this.distribution = distribution;
    }
}
