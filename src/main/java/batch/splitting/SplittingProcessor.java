package batch.splitting;

import batch.model.SplittingData;
import com.opencsv.CSVReader;
import org.springframework.batch.item.ItemProcessor;
import util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
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

    private SplittingData splitFile(File file, double splitRatio) throws FileNotFoundException {
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
        return splittingData;
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
