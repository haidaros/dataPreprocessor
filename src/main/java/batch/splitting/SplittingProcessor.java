package batch.splitting;

import batch.model.SplittingData;
import com.opencsv.CSVReader;
import org.springframework.batch.item.ItemProcessor;
import util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by eg on 27/04/15.
 */
public class SplittingProcessor implements ItemProcessor<List<File>, List<SplittingData>> {
    public List<SplittingData> process(List<File> files) throws Exception {
        double splitRatio = ResourceUtils.getConfig().getDouble("splitting.test-ratio");
        List<SplittingData> splitDataList = new LinkedList<SplittingData>();
        for (File f : files) {
            splitDataList.add(splitFile(f, splitRatio));
        }
        return splitDataList;
    }

    private SplittingData splitFile(File file, double splitRatio) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(file), ';');
        Iterator<String[]> iterator = reader.iterator();
        String[] headers = iterator.next();
        List<String[]> mainList = new LinkedList<String[]>();
        while (iterator.hasNext()) {
            String[] next = iterator.next();
            mainList.add(next);
        }
        List<Integer> zeroIndexs = new LinkedList<Integer>();
        List<Integer> bugyIndexs = new LinkedList<Integer>();
        for (int k = 0; k < mainList.size(); k++) {
            System.out.println("k = " + k);
            String[] dataItem = mainList.get(k);
            if (Double.valueOf(dataItem[dataItem.length - 1].trim()) == 0) {
                zeroIndexs.add(k);
            } else {
                bugyIndexs.add(k);
            }
        }
        List<String[]> testSet = splitTestSet(splitRatio, mainList, zeroIndexs, bugyIndexs, headers);
        replicateTrainingData(zeroIndexs, bugyIndexs);
        List<String[]> trainingSet = splitTrainingSet(mainList, zeroIndexs, bugyIndexs, headers);
        SplittingData splittingData = new SplittingData(file, testSet, trainingSet, headers);
        calculateOtherModes(splittingData);
        return splittingData;
    }

    private void calculateOtherModes(SplittingData splittingData) throws Exception {
        splittingData.setTestListBuggy(makeitBuggy(splittingData.getTestList()));
        splittingData.setTestListDensity(makeitDensity(splittingData.getTestList()));
        splittingData.setTrainingListBuggy(makeitBuggy(splittingData.getTrainingList()));
        splittingData.setTrainingListDensity(makeitDensity(splittingData.getTrainingList()));
    }

    private List<String[]> makeitBuggy(List<String[]> list) {
        int bugindex = list.get(0).length - 1;
        List<String[]> newlist = new LinkedList<String[]>();
        String[] newrow = new String[list.get(0).length];
        System.arraycopy(list.get(0), 0, newrow, 0, list.get(0).length);
        newrow[bugindex] = "Buggy";
        newlist.add(newrow);
        for (int i = 1; i < list.size(); i++) {
            String[] row = list.get(i);
            newrow = new String[row.length];
            System.arraycopy(row, 0, newrow, 0, row.length);
            newrow[bugindex] = Integer.parseInt(row[bugindex]) > 0 ? "Yes" : "No";
            newlist.add(newrow);
        }
        return newlist;
    }

    private List<String[]> makeitDensity(List<String[]> list) throws Exception {
        int bugindex = list.get(0).length - 1;
        int locindex = list.get(0).length - 2;
        List<String[]> newlist = new LinkedList<String[]>();
        String[] newrow = new String[list.get(0).length];
        System.arraycopy(list.get(0), 0, newrow, 0, list.get(0).length);
        newrow[bugindex] = "Density";
        newlist.add(newrow);
        for (int i = 1; i < list.size(); i++) {
            String[] row = list.get(i);
            newrow = new String[row.length];
            System.arraycopy(row, 0, newrow, 0, row.length);
            Double density = (Double.parseDouble(row[bugindex]) / Double.parseDouble(row[locindex]) * 1000);
            newrow[bugindex] = String.valueOf(density.intValue());
            newlist.add(newrow);
        }
        return newlist;
    }

    private List<String[]> splitTestSet(double splitRatio, List<String[]> mainList,
                                        List<Integer> zeroIndexs, List<Integer> bugyIndexs, String[] headers) {
        List<String[]> testList = new LinkedList<String[]>();
        testList.add(headers);
        //Spliting
        int balancedZeroCountinTest = (int) (zeroIndexs.size() * splitRatio);
        int balancedBugyCountinTest = (int) (bugyIndexs.size() * splitRatio);
        //Split Array//
        Random r = new Random();
        //Test Data Creation
        for (int l = balancedZeroCountinTest; l > 0; l--) {
            int random = r.nextInt(zeroIndexs.size());
            testList.add(mainList.get(zeroIndexs.get(random)));
            zeroIndexs.remove(random);
        }
        for (int l = balancedBugyCountinTest; l > 0; l--) {
            int random = r.nextInt(bugyIndexs.size());
            testList.add(mainList.get(bugyIndexs.get(random)));
            bugyIndexs.remove(random);
        }
        return testList;
    }

    private List<String[]> splitTrainingSet(List<String[]> mainList,
                                            List<Integer> zeroIndexs, List<Integer> bugyIndexs, String[] headers) {
        List<String[]> trainingList = new LinkedList<String[]>();
        trainingList.add(headers);
        for (Integer i : zeroIndexs) {
            trainingList.add(mainList.get(i));
        }
        for (Integer i : bugyIndexs) {
            trainingList.add(mainList.get(i));
        }
        return trainingList;
    }

    private void replicateTrainingData(List<Integer> zeroIndexs, List<Integer> bugyIndexs) {
        Random r = new Random();
        //Replication of data
        int replicationfozerobugs = 0;
        int replicationofbugy = 0;
        while (true) {
            if (zeroIndexs.size() > bugyIndexs.size()) {
                int random = r.nextInt(bugyIndexs.size());
                bugyIndexs.add(bugyIndexs.get(random));
                replicationofbugy++;
            } else if (zeroIndexs.size() < bugyIndexs.size()) {
                int random = r.nextInt(zeroIndexs.size());
                zeroIndexs.add(zeroIndexs.get(random));
                replicationfozerobugs++;
            } else {
                break;
            }
        }
    }
}
