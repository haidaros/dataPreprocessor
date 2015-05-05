package util;

import batch.model.OutputEntry;

import java.util.Comparator;

/**
 * Created by eg on 05/05/15.
 */
public class ProneComparer implements Comparator<OutputEntry> {
    int predictionIndex;

    public ProneComparer(int predictionIndex) {
        this.predictionIndex = predictionIndex;
    }

    public int compare(OutputEntry o1, OutputEntry o2) {
        if (o1.getPredictions().get(predictionIndex).getPrediction() > o2.getPredictions().get(predictionIndex).getPrediction())
            return -1;
        else if (o1.getPredictions().get(predictionIndex).getPrediction() == o2.getPredictions().get(predictionIndex).getPrediction()) {
            if (o1.getPredictions().get(predictionIndex).getPredictionProbability() > o2.getPredictions().get(predictionIndex).getPredictionProbability())
                return -1;
            else if (o1.getPredictions().get(predictionIndex).getPredictionProbability() == o2.getPredictions().get(predictionIndex).getPredictionProbability())
                return o1.getLoc() >= o2.getLoc() ? 1 : -1;
            else
                return 1;
        } else
            return 1;
    }
}