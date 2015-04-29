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
    List<String[]> testListBuggy;
    List<String[]> trainingListBuggy;
    List<String[]> testListDensity;
    List<String[]> trainingListDensity;
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

    public List<String[]> getTrainingListDensity() {
        return trainingListDensity;
    }

    public void setTrainingListDensity(List<String[]> trainingListDensity) {
        this.trainingListDensity = trainingListDensity;
    }

    public List<String[]> getTestListDensity() {
        return testListDensity;
    }

    public void setTestListDensity(List<String[]> testListDensity) {
        this.testListDensity = testListDensity;
    }

    public List<String[]> getTrainingListBuggy() {
        return trainingListBuggy;
    }

    public void setTrainingListBuggy(List<String[]> trainingListBuggy) {
        this.trainingListBuggy = trainingListBuggy;
    }

    public List<String[]> getTestListBuggy() {
        return testListBuggy;
    }

    public void setTestListBuggy(List<String[]> testListBuggy) {
        this.testListBuggy = testListBuggy;
    }
}
