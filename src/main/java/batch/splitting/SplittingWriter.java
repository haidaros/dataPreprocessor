package batch.splitting;

import batch.model.SplittingData;
import com.opencsv.CSVWriter;
import org.springframework.batch.item.ItemWriter;
import util.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by eg on 27/04/15.
 */
public class SplittingWriter implements ItemWriter<SplittingData> {
    public void write(List<? extends SplittingData> splittingDatas) throws Exception {
        String mainfolderName = new File(ResourceUtils.getConfig().getString("input-path")).getParentFile().getAbsolutePath() + "/splitted";
        new File(mainfolderName).mkdir();
        for (SplittingData s : splittingDatas) {
            String projectFolderName = mainfolderName + "/" + s.getFile().getParentFile().getName();
            new File(projectFolderName).mkdir();
            //Mode 1 Number Of Bugs
            String realName = s.getFile().getName().substring(0, s.getFile().getName().lastIndexOf("."));
            String fileNameTraining = projectFolderName + "/" + realName + "-trainig-NumberOfBugs.csv";
            String fileNameTest = projectFolderName + "/" + realName + "-test-NumberOfBugs.csv";
            CSVWriter csvWriter = new CSVWriter(new FileWriter(fileNameTraining), ';');
            csvWriter.writeAll(s.getTrainingList());
            csvWriter = new CSVWriter(new FileWriter(fileNameTest), ';');
            csvWriter.writeAll(s.getTestList());
            csvWriter.close();
            //Mode 2 Buggy
            fileNameTraining = projectFolderName + "/" + realName + "-trainig-Buggy.csv";
            fileNameTest = projectFolderName + "/" + realName + "-test-Buggy.csv";
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining), ';');
            csvWriter.writeAll(s.getTrainingListBuggy());
            csvWriter = new CSVWriter(new FileWriter(fileNameTest), ';');
            csvWriter.writeAll(s.getTestListBuggy());
            csvWriter.close();
            //Mode 3 Density
            fileNameTraining = projectFolderName + "/" + realName + "-trainig-Density.csv";
            fileNameTest = projectFolderName + "/" + realName + "-test-Density.csv";
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining), ';');
            csvWriter.writeAll(s.getTrainingListDensity());
            csvWriter = new CSVWriter(new FileWriter(fileNameTest), ';');
            csvWriter.writeAll(s.getTestListDensity());
            csvWriter.close();
        }
    }


}
