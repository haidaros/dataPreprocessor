package util;

import batch.model.OutputEntry;

import java.util.Comparator;

/**
 * Created by eg on 05/05/15.
 */
public class PredictionComparer implements Comparator<OutputEntry> {
    int predictionIndex;

    public PredictionComparer(int predictionIndex) {
        this.predictionIndex = predictionIndex;
    }

    public int compare(OutputEntry o1, OutputEntry o2) {
        if (o1.getPredictions().get(predictionIndex).getPrediction() > o2.getPredictions().get(predictionIndex).getPrediction())
            return -1;
        else if (o1.getPredictions().get(predictionIndex).getPrediction() == o2.getPredictions().get(predictionIndex).getPrediction())
            return o1.getLoc() >= o2.getLoc() ? 1 : -1;
        else
            return 1;
    }
}
