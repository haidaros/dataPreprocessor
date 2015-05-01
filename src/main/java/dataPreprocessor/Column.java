package dataPreprocessor;

import java.util.List;

/**
 * Created by eg on 24/02/15.
 */
public class Column {
    private String key;
    private int value;
    boolean isRowToCheck = false;
    double checkValue;
    boolean isTarget = false;
    List<String> column;
    List<String> alternativeHeaderNames;


    public Column(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isRowToCheck() {
        return isRowToCheck;
    }

    public void setRowToCheck(boolean isRowToCheck) {
        this.isRowToCheck = isRowToCheck;
    }

    public double getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(double checkValue) {
        this.checkValue = checkValue;
    }

    public List<String> getColumn() {
        return column;
    }

    public void setColumn(List<String> column) {
        this.column = column;
    }

    public List<String> getAlternativeHeaderNames() {
        return alternativeHeaderNames;
    }

    public void setAlternativeHeaderNames(List<String> alternativeHeaderNames) {
        this.alternativeHeaderNames = alternativeHeaderNames;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }
}
