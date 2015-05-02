package batch;

import batch.cleaning.CleaningProcessor;
import batch.cleaning.CleaningReader;
import batch.cleaning.CleaningWriter;
import batch.model.CleaningData;
import batch.model.PredictionData;
import batch.model.PredictionModel;
import batch.model.SplittingData;
import batch.prediction.PredictionProcessor;
import batch.prediction.PredictionReader;
import batch.prediction.PredictionWriter;
import batch.splitting.SplittingProcessor;
import batch.splitting.SplittingReader;
import batch.splitting.SplittingWriter;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;
import util.ResourceUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by eg on 27/04/15.
 */
public class BatchTester {
    @Test
    public void configTest() {
        List<HierarchicalConfiguration> configList = ResourceUtils.getConfig().configurationsAt("predictions.mode");
        List<PredictionModel> list = new LinkedList<PredictionModel>();
        for (HierarchicalConfiguration c : configList) {
            PredictionModel predictionModel = new PredictionModel(c.getString("name"), c.getList("model"));
            list.add(predictionModel);
        }
    }

    @Test
    public void testCleaningStep() throws Exception {
        CleaningReader cleaningReader = new CleaningReader();
        List<File> read = cleaningReader.read();
        CleaningProcessor cleaningProcessor = new CleaningProcessor();
        List<CleaningData> process = cleaningProcessor.process(read);
        CleaningWriter cleaningWriter = new CleaningWriter();
        cleaningWriter.write(process);
    }

    @Test
    public void testSplittingStep() throws Exception {
        SplittingReader splittingReader = new SplittingReader();
        List<File> read = splittingReader.read();
        SplittingProcessor splittingProcessor = new SplittingProcessor();
        List<SplittingData> process = splittingProcessor.process(read);
        SplittingWriter splittingWriter = new SplittingWriter();
        splittingWriter.write(process);
    }

    @Test
    public void testPredictionStep() throws Exception {
        PredictionReader reader = new PredictionReader();
        Map<File, File> read = reader.read();
        PredictionProcessor processor = new PredictionProcessor();
        List<PredictionData> process = processor.process(read);
        System.out.println("yes");
        PredictionWriter writer = new PredictionWriter();
        writer.write(process);
    }
}
