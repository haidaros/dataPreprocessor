package batch.output;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eg on 04/05/15.
 */
public class OutputReader implements ItemReader<List<File>> {
    public List<File> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String folderName = new File(ResourceUtils.getConfig().getString("input-path")).getParent() + "/result/predictions";
        File folder = new File(folderName);
        List<File> files = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                for (File ff : fileEntry.listFiles()) {
                    if (!ff.isDirectory() && ff.getName().contains(".csv")) {
                        files.add(ff);
                    }
                }
            }

        }
        return files;
    }
}
