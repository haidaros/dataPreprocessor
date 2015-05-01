package batch.cleaning;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eg on 24/04/15.
 */
public class CleaningReader implements ItemReader<List<File>> {
    public List<File> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String folderName = ResourceUtils.getConfig().getString("input-path");
        File folder = new File(folderName);
        List<File> files = new ArrayList<File>();
        for (final File projectFolder : folder.listFiles()) {
            if (projectFolder.isDirectory()) {
                files.add(projectFolder);
            }
        }
        return files;
    }
}
