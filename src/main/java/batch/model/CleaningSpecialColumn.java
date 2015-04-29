package batch.model;

import dataPreprocessor.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eg on 27/04/15.
 */

public class CleaningSpecialColumn {
    List<String> alternativeHeaderNames;
    boolean ExistbyDefault = false;
    boolean isTarget = false;
    List<String> externalColumnSet;
    Column existingColumn;

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

    public boolean isExistbyDefault() {
        return ExistbyDefault;
    }

    public void setExistbyDefault(boolean existbyDefault) {
        ExistbyDefault = existbyDefault;
    }

    public List<String> getExternalColumnSet() {
        return externalColumnSet;
    }

    public void setExternalColumnSet(List<String> externalColumnSet) {
        this.externalColumnSet = externalColumnSet;
    }

    public Column getExistingColumn() {
        return existingColumn;
    }

    public void setExistingColumn(Column existingColumn) {
        this.existingColumn = existingColumn;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }
}
