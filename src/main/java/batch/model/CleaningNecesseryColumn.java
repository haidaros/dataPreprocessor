package batch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eg on 28/04/15.
 */

public class CleaningNecesseryColumn {
    List<String> headerNames;
    boolean isExistbyDefault;
    List<String> column;

    public CleaningNecesseryColumn(List<Object> hnames) {
        this.headerNames = new ArrayList<String>();
        for (Object o : hnames) {
            headerNames.add(o.toString());
        }
        this.isExistbyDefault = false;
    }

    public List<String> getHeaderNames() {
        return headerNames;
    }

    public void setHeaderNames(List<String> headerNames) {
        this.headerNames = headerNames;
    }

    public boolean isExistbyDefault() {
        return isExistbyDefault;
    }

    public void setExistbyDefault(boolean isExistbyDefault) {
        this.isExistbyDefault = isExistbyDefault;
    }

    public List<String> getColumn() {
        return column;
    }

    public void setColumn(List<String> column) {
        this.column = column;
    }
}
