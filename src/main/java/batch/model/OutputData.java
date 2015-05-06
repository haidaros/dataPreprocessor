package batch.model;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by eg on 04/05/15.
 */
public class OutputData {
    List<OutputEntry> list;
    File file;
    int totalBug;
    int totalLoc;
    double[] ceList;
    double[][] ceResult;

    public OutputData(List<OutputEntry> list, File file, int totalBug, int totalLoc) {
        this.list = list;
        this.file = file;
        this.totalBug = totalBug;
        this.totalLoc = totalLoc;
    }

    public List<OutputEntry> getList() {
        return list;
    }

    public void setList(List<OutputEntry> list) {
        this.list = list;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getTotalBug() {
        return totalBug;
    }

    public void setTotalBug(int totalBug) {
        this.totalBug = totalBug;
    }

    public int getTotalLoc() {
        return totalLoc;
    }

    public void setTotalLoc(int totalLoc) {
        this.totalLoc = totalLoc;
    }

    public double[][] getCeResult() {
        return ceResult;
    }

    public void setCeResult(double[][] ceResult) {
        this.ceResult = ceResult;
    }

    public double[] getCeList() {
        return ceList;
    }

    public void setCeList(double[] ceList) {
        this.ceList = ceList;
    }

    public void addCeResult(double[] result, int classifierRowNum) {
        ceResult[classifierRowNum] = result;
    }

    public String[] getCeHeader() {
        String[] ceheader = new String[ceList.length + 1];
        ceheader[0] = " ";
        for (int i = 0; i < ceList.length; i++) {
            ceheader[i + 1] = "alpha = " + String.valueOf(ceList[i]);
        }
        return ceheader;
    }

    public String[] getCeValueRow(int k) {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String[] values = new String[ceList.length + 1];
        values[0] = list.get(0).getPredictionNames().get(k);
        for (int i = 0; i < ceList.length; i++) {
            values[i + 1] = String.valueOf(decimalFormat.format(ceResult[k][i]));
        }
        return values;
    }
}
