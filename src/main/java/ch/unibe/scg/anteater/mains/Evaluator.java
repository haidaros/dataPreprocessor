package ch.unibe.scg.anteater.mains;

import java.io.File;
import java.util.List;

import batch.model.OutputData;
import batch.output.OutputProcessor;
import batch.output.OutputReader;
import batch.output.OutputWriter;

/**
 * 
 * @author haidaros
 *
 */

//This is the last step for making evaluations and writing the outputs in excel format
public class Evaluator {

	public static void main(String[] args) {
		try {
			OutputReader reader = new OutputReader();
			List<File> read = reader.read();
			OutputProcessor outputProcessor = new OutputProcessor();
			List<OutputData> process = outputProcessor.process(read);
			OutputWriter writer = new OutputWriter();
			writer.write(process);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
