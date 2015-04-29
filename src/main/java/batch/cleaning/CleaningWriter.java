package batch.cleaning;

import batch.model.CleaningData;
import com.opencsv.CSVWriter;
import org.springframework.batch.item.ItemWriter;
import util.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by eg on 24/04/15.
 */
public class CleaningWriter implements ItemWriter<CleaningData> {
    public void write(List<? extends CleaningData> cleaningDatas) throws Exception {
        String mainfolderName = new File(ResourceUtils.getConfig().getString("input-path")).getParentFile().getAbsolutePath() + "/cleaned";
        new File(mainfolderName).mkdir();
        for (CleaningData c : cleaningDatas) {
            String projectFolderName = mainfolderName + "/" + c.getFile().getParentFile().getName();
            new File(projectFolderName).mkdir();
            String realName = c.getFile().getName().substring(0, c.getFile().getName().lastIndexOf("."));
            String fileName = projectFolderName + "/" + realName + "-cleaned.csv";
            CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName), ';');
            int i = 0;
            for (String[] s : c.getItemsInsideFile()) {
                csvWriter.writeNext(s);
            }
            csvWriter.close();
        }
    }
}
