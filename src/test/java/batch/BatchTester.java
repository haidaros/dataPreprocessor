package batch;

import batch.cleaning.CleaningProcessor;
import batch.cleaning.CleaningReader;
import batch.cleaning.CleaningWriter;
import batch.model.CleaningData;
import batch.model.SplittingData;
import batch.splitting.SplittingProcessor;
import batch.splitting.SplittingReader;
import batch.splitting.SplittingWriter;
import org.junit.Test;
import util.ResourceUtils;

import java.io.File;
import java.util.List;

/**
 * Created by eg on 27/04/15.
 */
public class BatchTester {
    @Test
    public void configTest() {
        List<Object> list = ResourceUtils.getConfig().getList("cleanup.target-columns.header");
        double aDouble = ResourceUtils.getConfig().getDouble("splitting.test-ratio");
        System.out.println("aDouble = " + aDouble);
        System.out.println("list.size() = " + list.size());

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
}
