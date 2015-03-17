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
