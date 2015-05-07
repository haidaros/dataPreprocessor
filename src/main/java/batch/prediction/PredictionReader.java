package batch.prediction;

import org.springframework.batch.item.ItemReader;
import util.ResourceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eg on 29/04/15.
 */
public class PredictionReader implements ItemReader<Map<File, File>> {
    public Map<File, File> read() throws Exception {
        String folderName = new File(ResourceUtils.getConfig().getString("input-path")).getParent() + "/result/splitted";
        File folder = new File(folderName);
        Map<File, File> files = new HashMap<File, File>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                for (File ff : fileEntry.listFiles()) {
                    if (!ff.isDirectory() && ff.getName().contains(".csv") && ff.getName().contains("training")) {
                        String testFile = ff.getName().replaceAll("(training)", "test");
                        File file = new File(ff.getParent() + "/" + testFile);
                        if (file.exists())
                            files.put(ff, file);
                        else
                            throw new Exception("Test File is not exist for file" + ff + " searched file name =" + testFile);
                    }
                }
            }
        }
        return files;
    }
}
