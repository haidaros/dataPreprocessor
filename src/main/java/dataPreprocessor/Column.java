package dataPreprocessor;

import java.util.List;

/**
 * Created by eg on 24/02/15.
 */
public class Column {
    private String key;
    private int value;
    boolean isTarget = false;
    boolean isLoc = false;
    boolean isRowToCheck = false;
    boolean isnative = true;
    double checkValue;
    List<String> column;
    List<String> alternativeHeaderNames;


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

    public Column(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
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

    public boolean isIsnative() {
        return isnative;
    }

    public void setIsnative(boolean isnative) {
        this.isnative = isnative;
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

    public boolean isLoc() {
        return isLoc;
    }

    public void setLoc(boolean isLoc) {
        this.isLoc = isLoc;
    }
}
