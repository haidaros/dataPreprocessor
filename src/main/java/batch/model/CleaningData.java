package batch.model;

import java.io.File;
import java.util.List;

/**
 * Created by eg on 27/04/15.
 */
public class CleaningData {
    File file;
    private List<String[]> itemsInsideFile;

    public CleaningData(File file, List<String[]> itemsInsideFile) {
        this.file = file;
        this.itemsInsideFile = itemsInsideFile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<String[]> getItemsInsideFile() {
        return itemsInsideFile;
    }

    public void setItemsInsideFile(List<String[]> itemsInsideFile) {
        this.itemsInsideFile = itemsInsideFile;
    }
}
