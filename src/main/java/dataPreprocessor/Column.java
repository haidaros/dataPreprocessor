package dataPreprocessor;

/**
 * Created by eg on 24/02/15.
 */
public class Column {
    private String key;
    private int value;

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
}
