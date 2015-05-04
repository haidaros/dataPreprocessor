package util;

import batch.model.OutputEntry;

import java.util.Comparator;

/**
 * Created by eg on 04/05/15.
 */
public class OptimalComparer implements Comparator<OutputEntry> {

    public int compare(OutputEntry o1, OutputEntry o2) {
        if (o1.getDensity() > o2.getDensity())
            return -1;
        else if (o1.getDensity() == o2.getDensity())
            return o1.getLoc() >= o2.getLoc() ? 1 : -1;
        else
            return 1;
    }
}
