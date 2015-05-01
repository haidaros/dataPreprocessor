package batch.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eg on 29/04/15.
 */
public class PredictionModel {
    String predictionName;
    List<String> modes;

    public PredictionModel(String predictionName, List<Object> mod) {
        this.predictionName = predictionName;
        this.modes = new LinkedList<String>();
        for(int i = 0 ; i <mod.size();i++) {
            modes.add(String.valueOf(mod.get(i)));
        }
        System.out.println("predictionName = " + predictionName);
    }

    public String getPredictionName() {
        return predictionName;
    }

    public void setPredictionName(String predictionName) {
        this.predictionName = predictionName;
    }

    public List<String> getModes() {
        return modes;
    }

    public void setModes(List<String> modes) {
        this.modes = modes;
    }
}
