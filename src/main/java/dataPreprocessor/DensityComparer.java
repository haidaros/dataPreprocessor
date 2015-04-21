package dataPreprocessor;

import java.util.Comparator;

/**
 * Created by eg on 11/03/15.
 */
public class DensityComparer implements Comparator<DataEntry> {
    public int compare(DataEntry o1, DataEntry o2) {
        if (o1.density >= o2.density)
            return -1;
        else
            return 1;
    }
}

class linearRegressionDensityComparer implements Comparator<DataEntry> {
    public int compare(DataEntry o1, DataEntry o2) {
        if (o1.predictionDensinty >= o2.predictionDensinty)
            return -1;
        else
            return 1;
    }
}


class ibkDensityComparer implements Comparator<DataEntry> {
    public int compare(DataEntry o1, DataEntry o2) {
        if (o1.ibkpredictionDensinty >= o2.ibkpredictionDensinty)
            return -1;
        else
            return 1;
    }
}


class svmDensityComparer implements Comparator<DataEntry> {
    public int compare(DataEntry o1, DataEntry o2) {
        if (o1.svmpredictionDensinty >= o2.svmpredictionDensinty)
            return -1;
        else
            return 1;
    }
}

class dynamicPredictionDensityComparer implements Comparator<DataEntry> {
    int predictionIndex;

    dynamicPredictionDensityComparer(int predictionIndex) {
        this.predictionIndex = predictionIndex;
    }

    public int compare(DataEntry o1, DataEntry o2) {
        if (o1.predictions.get(predictionIndex).getPredictionDensinty() > o2.predictions.get(predictionIndex).getPredictionDensinty())
            return -1;
        else if (o1.predictions.get(predictionIndex).getPredictionDensinty() == o2.predictions.get(predictionIndex).getPredictionDensinty())
            return o1.loc >= o2.loc ? 1 : -1;
        else
            return 1;
    }
}

class dynamicPredictionComparer implements Comparator<DataEntry> {
    int predictionIndex;

    dynamicPredictionComparer(int predictionIndex) {
        this.predictionIndex = predictionIndex;
    }

    public int compare(DataEntry o1, DataEntry o2) {
        if (o1.predictions.get(predictionIndex).getPrediction() > o2.predictions.get(predictionIndex).getPrediction())
            return -1;
        else if (o1.predictions.get(predictionIndex).getPrediction() == o2.predictions.get(predictionIndex).getPrediction())
            return o1.loc >= o2.loc ? -1 : 1;
        else
            return 1;
    }
}


class dynamicProbabilityComparer implements Comparator<DataEntry> {
    int predictionIndex;

    dynamicProbabilityComparer(int predictionIndex) {
        this.predictionIndex = predictionIndex;
    }

    public int compare(DataEntry o1, DataEntry o2) {
        Prediction p1 = o1.predictions.get(predictionIndex);
        Prediction p2 = o2.predictions.get(predictionIndex);
        if (p1.getPrediction() > p2.getPrediction())
            return -1;
        else if (p1.prediction == p2.prediction) {
            if (p1.probability > p2.probability) {
                return -1;
            } else if (p1.probability == p2.probability) {
                if (o1.loc <= o2.loc)
                    return -1;
                else
                    return 1;
            } else {
                return 1;
            }
        } else
            return 1;
    }
}
