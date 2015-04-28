package batch.model;

import java.io.File;
import java.util.List;

/**
 * Created by eg on 27/04/15.
 */
public class SplittingData {
    File file;
    List<String[]> testList;
    List<String[]> trainingList;
    String[] header;

    public SplittingData(File file, List<String[]> testList, List<String[]> trainingList, String[] header) {
        this.file = file;
        this.testList = testList;
        this.trainingList = trainingList;
        this.header = header;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<String[]> getTestList() {
        return testList;
    }

    public void setTestList(List<String[]> testList) {
        this.testList = testList;
    }

    public List<String[]> getTrainingList() {
        return trainingList;
    }

    public void setTrainingList(List<String[]> trainingList) {
        this.trainingList = trainingList;
    }

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }
}
