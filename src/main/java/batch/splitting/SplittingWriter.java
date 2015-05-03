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
            String fileNameTraining = projectFolderName + "/" + realName + "-training-bug-count.csv";
            String fileNameTest = projectFolderName + "/" + realName + "-test-bug-count.csv";
            CSVWriter csvWriter = new CSVWriter(new FileWriter(projectFolderName + "/" + realName + "-header.csv"));
            writeHeader(csvWriter, s.getTestList());
            csvWriter.close();
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining));
            writeAll(csvWriter, s.getTrainingList());
            csvWriter.close();
            csvWriter = new CSVWriter(new FileWriter(fileNameTest));
            writeAll(csvWriter, s.getTestList());
            csvWriter.close();
            //Mode 2 Buggy
            fileNameTraining = projectFolderName + "/" + realName + "-training-buggy-class.csv";
            fileNameTest = projectFolderName + "/" + realName + "-test-buggy-class.csv";
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining));
            writeAll(csvWriter, s.getTrainingListBuggy());
            csvWriter.close();
            csvWriter = new CSVWriter(new FileWriter(fileNameTest));
            writeAll(csvWriter, s.getTestListBuggy());
            csvWriter.close();
            //Mode 3 Density
            fileNameTraining = projectFolderName + "/" + realName + "-training-bug-density.csv";
            fileNameTest = projectFolderName + "/" + realName + "-test-bug-density.csv";
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining));
            writeAll(csvWriter, s.getTrainingListDensity());
            csvWriter.close();
            csvWriter = new CSVWriter(new FileWriter(fileNameTest));
            writeAll(csvWriter, s.getTestListDensity());
            csvWriter.close();
        }
    }

    private void writeHeader(CSVWriter csvWriter, List<String[]> testList) {
        for (String[] s : testList) {
            String[] strings = new String[1];
            System.arraycopy(s, 0, strings, 0, 1);
            csvWriter.writeNext(strings);
        }
    }

    private void writeAll(CSVWriter csvWriter, List<String[]> testList) {
        for (String[] s : testList) {
            String[] strings = new String[s.length - 1];
            System.arraycopy(s, 1, strings, 0, s.length - 1);
            csvWriter.writeNext(strings);
        }
    }

}
