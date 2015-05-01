package batch.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by eg on 27/04/15.
 */

public class CleaningSpecialColumn {
    List<String> alternativeHeaderNames;
    Map<String, String> externalColumnSet;
    boolean found = false;

    public CleaningSpecialColumn(List<Object> array) {
        alternativeHeaderNames = new ArrayList<String>();
        for (Object o : array) {
            alternativeHeaderNames.add(o.toString());
        }
    }

    public List<String> getAlternativeHeaderNames() {
        return alternativeHeaderNames;
    }

    public void setAlternativeHeaderNames(List<String> alternativeHeaderNames) {
        this.alternativeHeaderNames = alternativeHeaderNames;
    }

    public Map<String, String> getExternalColumnSet() {
        return externalColumnSet;
    }

    public void setExternalColumnSet(Map<String, String> externalColumnSet) {
        this.externalColumnSet = externalColumnSet;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}
