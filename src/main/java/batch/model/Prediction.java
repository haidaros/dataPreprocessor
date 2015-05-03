package batch.model;

/**
 * Created by eg on 03/05/15.
 */
public class Prediction {
    double prediction;
    double predictionProbability;
    double predictionDensity;


    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public double getPredictionProbability() {
        return predictionProbability;
    }

    public void setPredictionProbability(double predictionProbability) {
        this.predictionProbability = predictionProbability;
    }

    public double getPredictionDensity() {
        return predictionDensity;
    }

    public void setPredictionDensity(double predictionDensity) {
        this.predictionDensity = predictionDensity;
    }
}
