package ch.unibe.scg.anteater.mains;

import java.io.File;
import java.util.List;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import batch.model.SplittingData;
import batch.splitting.SplittingProcessor;
import batch.splitting.SplittingReader;
import batch.splitting.SplittingWriter;

/**
 * 
 * @author haidaros
 *
 */
// Splits the dataset into a training set and a test set based on the ratio in
// the config file. This step comes after the cleaning step
public class DatasetSplitter {

	public static void main(String[] args) {
		try {
			SplittingReader splittingReader = new SplittingReader();
			List<File> read;
			read = splittingReader.read();
			SplittingProcessor splittingProcessor = new SplittingProcessor();
			List<SplittingData> process = splittingProcessor.process(read);
			SplittingWriter splittingWriter = new SplittingWriter();
			splittingWriter.write(process);
		} catch (UnexpectedInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonTransientResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
