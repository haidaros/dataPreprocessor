package batch.model;

import java.io.File;
import java.util.List;

/**
 * Created by eg on 30/04/15.
 */
public class PredictionData {
    File file;
    List<PredictionEntry> predictionEntries;
    List<PredictionEntry> pronepredictionEntries;
    boolean isProne;

    public PredictionData() {
    }

    public List<PredictionEntry> getPredictionEntries() {
        return predictionEntries;
    }

    public void setPredictionEntries(List<PredictionEntry> predictionEntries) {
        this.predictionEntries = predictionEntries;
    }


    public List<PredictionEntry> getPronepredictionEntries() {
        return pronepredictionEntries;
    }

    public void setPronepredictionEntries(List<PredictionEntry> pronepredictionEntries) {
        this.pronepredictionEntries = pronepredictionEntries;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isProne() {
        return isProne;
    }

    public void setProne(boolean isProne) {
        this.isProne = isProne;
    }
}
