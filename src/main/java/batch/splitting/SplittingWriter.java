package batch.splitting;

import batch.model.SplittingData;
import com.opencsv.CSVWriter;
import org.springframework.batch.item.ItemWriter;
import util.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
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
            fileNameTraining = projectFolderName + "/" + realName + "-trainig-Class.csv";
            fileNameTest = projectFolderName + "/" + realName + "-test-Class.csv";
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining), ';');
            csvWriter.writeAll(makeitBuggy(s.getTrainingList()));
            csvWriter = new CSVWriter(new FileWriter(fileNameTest), ';');
            csvWriter.writeAll(makeitBuggy(s.getTestList()));
            csvWriter.close();
            //Mode 2 Buggy
            fileNameTraining = projectFolderName + "/" + realName + "-trainig-Density.csv";
            fileNameTest = projectFolderName + "/" + realName + "-test-Density.csv";
            csvWriter = new CSVWriter(new FileWriter(fileNameTraining), ';');
            csvWriter.writeAll(makeitDensity(s.getTrainingList()));
            csvWriter = new CSVWriter(new FileWriter(fileNameTest), ';');
            csvWriter.writeAll(makeitDensity(s.getTestList()));
            csvWriter.close();
        }
    }

    private List<String[]> makeitBuggy(List<String[]> list) {
        int bugindex = list.get(0).length - 1;
        List<String[]> newlist = new LinkedList<String[]>();
        list.get(0)[bugindex] = "Class";
        newlist.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            String[] row = list.get(i);
            String[] newrow = new String[row.length];
            System.arraycopy(row, 0, newrow, 0, row.length);
            newrow[bugindex] = Integer.parseInt(row[bugindex]) > 0 ? "Yes" : "No";
            newlist.add(newrow);
        }
        return newlist;
    }

    private List<String[]> makeitDensity(List<String[]> list) throws Exception {
        int bugindex = list.get(0).length - 1;
        int locindex = findLocIndex(list.get(0));
        list.get(0)[bugindex] = "Density";
        List<String[]> newlist = new LinkedList<String[]>();
        newlist.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            String[] row = list.get(i);
            String[] newrow = new String[row.length];
            System.arraycopy(row, 0, newrow, 0, row.length);
            Double density = (Double.parseDouble(row[bugindex]) / Double.parseDouble(row[locindex]) * 1000);
            newrow[bugindex] = String.valueOf(density.intValue());
            newlist.add(newrow);
        }
        return newlist;
    }

    private int findLocIndex(String[] strings) throws Exception {
        int i = 0;
        List<Object> list = ResourceUtils.getConfig().getList("cleanup.loc-header-names.header");
        for (String s : strings) {
            for (Object o : list) {
                if (s.equals(o.toString()))
                    return i;
            }
            i++;
        }
        throw new Exception("loc not found check the code");
    }
}
