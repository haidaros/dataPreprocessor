package batch.prediction;

import batch.model.PredictionData;
import batch.model.PredictionEntry;
import com.opencsv.CSVWriter;
import org.springframework.batch.item.ItemWriter;
import util.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by eg on 29/04/15.
 */
public class PredictionWriter implements ItemWriter<PredictionData> {
    public void write(List<? extends PredictionData> predictionDatas) throws Exception {
        String[] header = {"ClassName", "Lines of Code", "Number Of Bugs", "Prediction"};
        String[] headerProne = {"ClassName", "Lines of Code", "Number Of Bugs", "Probability"};
        String mainfolderName = new File(ResourceUtils.getConfig().getString("input-path")).getParentFile().getAbsolutePath() + "/predictions";
        new File(mainfolderName).mkdir();
        for (PredictionData pd : predictionDatas) {
            String projectName = mainfolderName + "/" + pd.getFile().getParentFile().getName();
            new File(projectName).mkdir();
            createOutput(projectName, pd.getFile(), pd.getPredictionEntries(), header);
            if (pd.getPronepredictionEntries() != null)
                createOutputProne(projectName, pd.getFile(), pd.getPronepredictionEntries(), headerProne);

        }
    }

    private void createOutput(String folderName, File f, List<PredictionEntry> predictionEntries, String[] header) throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(folderName + "/" + f.getName()));
        csvWriter.writeNext(predictionEntries.get(0).getHeaderArray());
        for (PredictionEntry p : predictionEntries) {
            csvWriter.writeNext(p.getArray());
        }
        csvWriter.close();
    }

    private void createOutputProne(String folderName, File f, List<PredictionEntry> predictionEntries, String[] header) throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(folderName + "/" + f.getName().substring(0, f.getName().indexOf("-buggy")) + "-prone.csv"));
        csvWriter.writeNext(predictionEntries.get(0).getHeaderProneArray());
        for (PredictionEntry p : predictionEntries) {
            csvWriter.writeNext(p.getProneArray());
        }
        csvWriter.close();
    }
}
