package batch.model;

import dataPreprocessor.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eg on 27/04/15.
 */
public class CleaningTargetHeader {
    List<String> alternativeHeaderName;
    boolean found = false;
    Column targetColum;

    public CleaningTargetHeader(List<Object> array) {
        alternativeHeaderName = new ArrayList<String>();
        for (Object o : array) {
            alternativeHeaderName.add(o.toString());
        }
    }

    public List<String> getAlternativeHeaderName() {
        return alternativeHeaderName;
    }

    public void setAlternativeHeaderName(List<String> alternativeHeaderName) {
        this.alternativeHeaderName = alternativeHeaderName;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public Column getTargetColum() {
        return targetColum;
    }

    public void setTargetColum(Column targetColum) {
        this.targetColum = targetColum;
    }
}
