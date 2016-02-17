package ch.unibe.scg.anteater.mains;

import java.io.File;
import java.util.List;
import java.util.Map;

import batch.model.PredictionData;
import batch.prediction.PredictionProcessor;
import batch.prediction.PredictionReader;
import batch.prediction.PredictionWriter;

/**
 * 
 * @author haidaros
 *
 */
// This is the third step. This is where the predictions are made depending on
// the configurations in the config file.
public class PredictionMaker {

	public static void main(String[] args) {
		try {
			PredictionReader reader = new PredictionReader();
			Map<File, File> read = reader.read();
			PredictionProcessor processor = new PredictionProcessor();
			List<PredictionData> process = processor.process(read);
			PredictionWriter writer = new PredictionWriter();
			writer.write(process);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
