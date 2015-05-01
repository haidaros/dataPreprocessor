package batch.prediction;

import batch.model.PredictionData;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by eg on 29/04/15.
 */
public class PredictionWriter implements ItemWriter<PredictionData> {
    public void write(List<? extends PredictionData> predictionDatas) throws Exception {

    }
}
