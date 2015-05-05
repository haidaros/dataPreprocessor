package batch.output;

import batch.model.OutputData;
import batch.model.OutputEntry;
import batch.model.Prediction;
import com.opencsv.CSVReader;
import org.springframework.batch.item.ItemProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eg on 04/05/15.
 */
public class OutputProcessor implements ItemProcessor<List<File>, List<OutputData>> {
    public List<OutputData> process(List<File> files) throws Exception {
        List<OutputData> list = new LinkedList<OutputData>();
        for (File f : files) {
            if (f.getName().contains("bug-count.csv")) {
                List<OutputEntry> outputEntries = new LinkedList<OutputEntry>();
                int[] ints = readPredictionFile(f, outputEntries, true, false);
                list.add(new OutputData(outputEntries, f, ints[0], ints[1]));
            } else if (f.getName().contains("bug-density.csv") || f.getName().contains("buggy-class.csv")) {
                List<OutputEntry> outputEntries = new LinkedList<OutputEntry>();
                int[] ints = readPredictionFile(f, outputEntries, false, false);
                list.add(new OutputData(outputEntries, f, ints[0], ints[1]));
            } else {
                List<OutputEntry> outputEntries = new LinkedList<OutputEntry>();
                int[] ints = readPredictionFile(f, outputEntries, false, true);
                list.add(new OutputData(outputEntries, f, ints[0], ints[1]));
            }
        }
        return list;
    }

    private int[] readPredictionFile(File f, List<OutputEntry> list, boolean isBugCount, boolean isProne) throws FileNotFoundException {
        CSVReader reader = new CSVReader(new FileReader(f), ';');
        Iterator<String[]> iterator = reader.iterator();
        List<String> classifierHeaders = parseHeader(iterator.next());
        int totalBug = 0;
        int totalLoc = 0;
        while (iterator.hasNext()) {
            String[] s = iterator.next();
            OutputEntry pe = new OutputEntry();
            pe.setClassName(s[0]);
            pe.setLoc(Integer.parseInt(s[1]));
            totalLoc += pe.getLoc();
            pe.setBug(Integer.parseInt(s[2]));
            pe.setDensity((double) pe.getBug() / (double) pe.getLoc() * 1000);
            totalBug += pe.getBug();
            int i = 3;
            for (String header : classifierHeaders) {
                Prediction pr = new Prediction();
                pr.setPrediction(Double.parseDouble(s[i++]));
                if (isBugCount)
                    pr.setPredictionDensity(1000 * pr.getPrediction() / pe.getLoc());
                if (isProne)
                    pr.setPredictionProbability(Double.parseDouble(s[i++]));
                pe.addPrediction(pr);
            }
            pe.setPredictionNames(classifierHeaders);
            list.add(pe);
        }
        return new int[]{totalBug, totalLoc};
    }

    private List<String> parseHeader(String[] header) {
        int length = header.length;
        List<String> predictionHeaders = new LinkedList<String>();
        for (int i = 3; i < length; i++) {
            if (!header[i].equals("Probabilty"))
                predictionHeaders.add(header[i]);
        }
        return predictionHeaders;
    }
}
