package batch.cleaning;

import batch.model.CleaningData;
import batch.model.CleaningSpecialColumn;
import batch.model.Column;
import batch.model.DbItem;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.springframework.batch.item.ItemProcessor;
import util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by eg on 24/04/15.
 */

public class CleaningProcessor implements ItemProcessor<List<File>, List<CleaningData>> {


    public List<CleaningData> process(List<File> projectFolders) throws Exception {
        HashMap<String, String> rowsToRemoveMap = new HashMap<String, String>();
        List<String> columnsToRemoveList = new ArrayList<String>();
        //Getting Configurations
        List<HierarchicalConfiguration> configList = ResourceUtils.getConfig().configurationsAt("cleanup.rows-to-remove.item");
        for (HierarchicalConfiguration c : configList) {
            rowsToRemoveMap.put(c.getString("header"), c.getString("value"));
        }
        configList = ResourceUtils.getConfig().configurationsAt("cleanup.columns-to-remove.item");
        for (HierarchicalConfiguration c : configList) {
            columnsToRemoveList.add(c.getString("header"));
        }
        //creation of DB file
        Map<String, String> classLocMap = null;
        List<CleaningData> processResult = new ArrayList<CleaningData>();

        for (File project : projectFolders) {
            classLocMap = createDb(project);
            for (File file : project.listFiles()) {
                if (file.getName().contains(".csv")) {
                    List<String[]> result = cleanFile(file, rowsToRemoveMap, columnsToRemoveList, classLocMap);
                    CleaningData cleaningData = new CleaningData(file, result);
                    processResult.add(cleaningData);
                }
            }
        }
        return processResult;
    }

    private Map<String, String> createDb(File project) throws Exception {
        List<Object> targetHeaderList = ResourceUtils.getConfig().getList("cleanup.target-columns.header");
        List<Object> locHeaderList = ResourceUtils.getConfig().getList("cleanup.loc-column.header");
        List<Object> classNameHeaderList = ResourceUtils.getConfig().getList("cleanup.class-name-column.header");
        CleaningSpecialColumn target = new CleaningSpecialColumn(targetHeaderList);
        CleaningSpecialColumn loc = new CleaningSpecialColumn(locHeaderList);
        CleaningSpecialColumn header = new CleaningSpecialColumn(classNameHeaderList);
        Map<String, String> headerColumn = lookForNecesseryColumn(header, project.listFiles()[0]);
        Map<String, String> classNameLocMap = null;
        List<DbItem> dbItemList = new LinkedList<DbItem>();
        for (Map.Entry<String, String> s : headerColumn.entrySet()) {
            dbItemList.add(new DbItem(s.getKey()));
        }
        for (File f : project.listFiles()) {
            if (!target.isFound()) {
                Map<String, String> classNameBugMap = lookForNecesseryColumn(target, f);
                target.setExternalColumnSet(classNameBugMap);
                target.setFound(true);
            }
            if (!loc.isFound()) {
                classNameLocMap = lookForNecesseryColumn(loc, f);
                loc.setExternalColumnSet(classNameLocMap);
                loc.setFound(true);
            }
            if (target.isFound() && loc.isFound())
                break;
        }
        //
        if (target.isFound() && loc.isFound()) {
            String s = project.getParentFile().getParent() + "/cleaned";
            new File(s).mkdir();
            s = s + "/" + project.getName();
            new File(s).mkdir();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(s + "/db.csv"), ';');
            String[] fileHeader = {"classname", "loc", "bug"};
            csvWriter.writeNext(fileHeader);
            for (DbItem item : dbItemList) {
                item.setLoc(loc.getExternalColumnSet().get(item.getClassname()));
                item.setNumberofbugs(target.getExternalColumnSet().get(item.getClassname()));
                csvWriter.writeNext(item.toArray());
            }
            csvWriter.close();
            return classNameLocMap;
        } else
            throw new Exception("Project folder : " + project.getName() + "is not include Target column or Lines of Code column in any file ");
    }

    private List<String[]> cleanFile(File file, Map<String, String> rowsToRemoveMap,
                                     List<String> columnsToRemoveList, Map<String, String> classLocMap) throws Exception {
        //Getting loc headers and targetheaders
        CSVReader reader = new CSVReader(new FileReader(file),';') ;
        Iterator<String[]> iterator = reader.iterator();
        String[] columnNames = iterator.next();
        //Exporting Columns from file
        List<Column> fileColumns = exportColumnCSV(columnNames, rowsToRemoveMap, columnsToRemoveList, file);
        List<String[]> items = new LinkedList<String[]>();
        items.add(getHeaderRow(fileColumns));
        while (iterator.hasNext()) {
            String[] entry = iterator.next();
            String[] values = new String[fileColumns.size()];
            boolean removethisrow = false;
            String targetValue = "";
            int i = 0;
            for (Column c : fileColumns) {
                Double value;
                //checkrowrestrictions
                if (c.isRowToCheck()) {
                    value = Double.valueOf(entry[c.getValue()].trim());
                    if (value == c.getCheckValue()) {
                        removethisrow = true;
                        break;
                    }
                }
                String v = String.valueOf(entry[c.getValue()].trim()).trim();

                if (c.isTarget()) {
                    targetValue = v;
                } else {
                    values[i++] = v;
                }
            }
            values[i++] = targetValue;
            if (!removethisrow) {
                if (Double.parseDouble(classLocMap.get(values[0])) > 0)
                    items.add(values);
            }
        }
        return items;
    }

    private Map<String, String> lookForNecesseryColumn(CleaningSpecialColumn cns, File file) throws FileNotFoundException {
        File[] files = file.getParentFile().listFiles();
        for (File f : files) {
            if (!f.isDirectory() && f.getName().contains(".csv") && !f.getName().equals(file.getName())) {
                CSVReader reader = new CSVReader(new FileReader(f), ';');
                Iterator<String[]> iterator = reader.iterator();
                String[] headers = iterator.next();
                int i = 0;
                for (String s : headers) {
                    for (String c : cns.getAlternativeHeaderNames()) {
                        if (s.trim().toLowerCase().contains(c.trim().toLowerCase())) {
                            return getColumnFromAFile(iterator, i);
                        }
                    }
                    i++;
                }
            }
        }
        return null;
    }

    private Map<String, String> getColumnFromAFile(Iterator<String[]> iterator, int i) {
        Map<String, String> column = new HashMap<String, String>();
        while (iterator.hasNext()) {
            String[] next = iterator.next();
            column.put(next[0].trim(), next[i].trim());
        }
        return column;
    }

    private String[] getHeaderRow(List<Column> fileColumns) {
        String[] columnNames = new String[fileColumns.size()];
        int i = 0;
        String target = "";
        String loc = "";
        for (Column c : fileColumns) {
            if (c.isTarget())
                target = c.getKey();
            else
                columnNames[i++] = c.getKey();
        }
        columnNames[i] = target;
        return columnNames;
    }

    private List<Column> exportColumnCSV(String[] headerrow, Map<String, String> rowsToRemoveMap,
                                         List<String> columnsToRemoveList, File file) throws Exception {
        List<Column> columnNames = new ArrayList<Column>();
        List<Object> targetHeaderList = ResourceUtils.getConfig().getList("cleanup.target-columns.header");
        //Getting Existing Columns
        boolean targetFound = false;
        for (int i = 0; i < headerrow.length; i++) {
            boolean exclusion = false;
            String columnName = headerrow[i].trim();
            if (columnsToRemoveList != null) {
                for (String s : columnsToRemoveList) {
                    if (columnName.equals(s.trim())) {
                        exclusion = true;
                    }
                }
            }
            Column column = new Column(columnName, i);
            if (!targetFound)
                for (Object o : targetHeaderList) {
                    if (o.toString().trim().equals(columnName)) {
                        column.setTarget(true);
                        targetFound = true;
                    }

                }
            if (!exclusion && !columnName.equals("")) {
                columnNames.add(column);
            }
        }
        if (!targetFound)
            throw new Exception("Target is not exist on this file" + file);

        for (Column c : columnNames) {
            for (Map.Entry<String, String> entry : rowsToRemoveMap.entrySet()) {
                if (c.getKey().equals(entry.getKey())) {
                    c.setRowToCheck(true);
                    c.setCheckValue(Double.parseDouble(entry.getValue().trim()));
                    break;
                }
            }
        }
        return columnNames;
    }
}