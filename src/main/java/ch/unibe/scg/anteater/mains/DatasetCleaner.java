package ch.unibe.scg.anteater.mains;

import java.io.File;
import java.util.List;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import batch.cleaning.CleaningProcessor;
import batch.cleaning.CleaningReader;
import batch.cleaning.CleaningWriter;
import batch.model.CleaningData;

/**
 * @author haidaros
 * 
 */
// This class is an example on how to carry out the cleaning step. This is the
// first step in building the bug predictor.
public class DatasetCleaner {
	public static void main(String[] args) {
		try {
			CleaningReader cleaningReader = new CleaningReader();
			List<File> read;
			read = cleaningReader.read();
			CleaningProcessor cleaningProcessor = new CleaningProcessor();
			List<CleaningData> process = cleaningProcessor.process(read);
			CleaningWriter cleaningWriter = new CleaningWriter();
			cleaningWriter.write(process);
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
