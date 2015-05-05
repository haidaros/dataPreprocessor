package batch;

import batch.cleaning.CleaningProcessor;
import batch.cleaning.CleaningReader;
import batch.cleaning.CleaningWriter;
import batch.model.CleaningData;
import batch.model.OutputData;
import batch.model.PredictionData;
import batch.model.SplittingData;
import batch.output.OutputProcessor;
import batch.output.OutputReader;
import batch.output.OutputWriter;
import batch.prediction.PredictionProcessor;
import batch.prediction.PredictionReader;
import batch.prediction.PredictionWriter;
import batch.splitting.SplittingProcessor;
import batch.splitting.SplittingReader;
import batch.splitting.SplittingWriter;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by eg on 27/04/15.
 */
public class BatchTester {
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
        PredictionWriter writer = new PredictionWriter();
        writer.write(process);
    }

    @Test
    public void testOutputStep() throws Exception {
        OutputReader reader = new OutputReader();
        List<File> read = reader.read();
        OutputProcessor outputProcessor = new OutputProcessor();
        List<OutputData> process = outputProcessor.process(read);
        OutputWriter writer = new OutputWriter();
        writer.write(process);
    }

    @Test
    public void testAllSteps() throws Exception {
        System.out.println("Cleaning Step Started");
        testCleaningStep();
        System.out.println("Cleaning Step End");
        System.out.println("Splitting Step Started");
        testSplittingStep();
        System.out.println("Splitting Step End");
        System.out.println("Prediction Step Started");
        testPredictionStep();
        System.out.println("Prediction Step End");
        System.out.println("Output Step Started");
        testOutputStep();
        System.out.println("All Tests end");
    }
}
