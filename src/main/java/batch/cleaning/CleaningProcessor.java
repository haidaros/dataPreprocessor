package batch.cleaning;

import batch.model.CleaningData;
import batch.model.CleaningSpecialColumn;
import com.opencsv.CSVReader;
import dataPreprocessor.Column;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.springframework.batch.item.ItemProcessor;
import util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by eg on 24/04/15.
 */

public class CleaningProcessor implements ItemProcessor<List<File>, List<CleaningData>> {


    public List<CleaningData> process(List<File> files) throws Exception {
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
        List<CleaningData> processResult = new ArrayList<CleaningData>();
        for (File f : files) {
            List<String[]> result = cleanFile(f, rowsToRemoveMap, columnsToRemoveList);
            CleaningData cleaningData = new CleaningData(f, result);
            processResult.add(cleaningData);
        }
        return processResult;
    }

    private List<String[]> cleanFile(File file, Map<String, String> rowsToRemoveMap,
                                     List<String> columnsToRemoveList) throws Exception {
        //Getting loc headers and targetheaders
        List<Object> targetHeaderList = ResourceUtils.getConfig().getList("cleanup.target-columns.header");
        List<Object> locHeaderList = ResourceUtils.getConfig().getList("cleanup.loc-column.header");
        CleaningSpecialColumn target = new CleaningSpecialColumn(targetHeaderList);
        target.setTarget(true);
        CleaningSpecialColumn loc = new CleaningSpecialColumn(locHeaderList);
        ArrayList<CleaningSpecialColumn> specialColumns = new ArrayList<CleaningSpecialColumn>();
        specialColumns.add(target);
        specialColumns.add(loc);
        CSVReader reader = new CSVReader(new FileReader(file), ';');
        Iterator<String[]> iterator = reader.iterator();
        String[] columnNames = iterator.next();
        //Exporting Columns from file
        List<Column> fileColumns = exportColumnCSV(columnNames, rowsToRemoveMap, columnsToRemoveList, specialColumns, file);
        List<String[]> items = new LinkedList<String[]>();
        items.add(getHeaderRow(fileColumns));
        int rowCount = 0;
        while (iterator.hasNext()) {
            String[] entry = iterator.next();
            String[] values = new String[fileColumns.size()];
            boolean removethisrow = false;
            String targetValue = "";
            int i = 0;
            for (Column c : fileColumns) {
                Double value;
                if (c.isRowToCheck()) {
                    if (c.isIsnative())
                        value = Double.valueOf(entry[c.getValue()].trim());
                    else
                        value = Double.valueOf(c.getColumn().get(rowCount));
                    if (value == c.getCheckValue()) {
                        removethisrow = true;
                        break;
                    }
                }
                String v = "";
                if (c.isIsnative())
                    v = String.valueOf(entry[c.getValue()].trim()).trim();
                else
                    v = String.valueOf(c.getColumn().get(rowCount)).trim();

                if (c.isTarget()) {
                    targetValue = v;
                } else {
                    values[i++] = v;
                }
            }
            values[i++] = targetValue;
            if (!removethisrow)
                items.add(values);
            rowCount++;
        }
        return items;
    }

    private List<String> lookForNecesseryColumn(List<String> headerNames, File file) throws FileNotFoundException {
        File[] files = file.getParentFile().listFiles();
        for (File f : files) {
            if (!f.isDirectory() && f.getName().contains(".csv") && !f.getName().equals(file.getName())) {
                CSVReader reader = new CSVReader(new FileReader(f), ';');
                Iterator<String[]> iterator = reader.iterator();
                String[] headers = iterator.next();
                int i = 0;
                for (String s : headers) {
                    for (String c : headerNames) {
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

    private List<String> getColumnFromAFile(Iterator<String[]> iterator, int i) {
        List<String> column = new LinkedList<String>();
        while (iterator.hasNext()) {
            String[] next = iterator.next();
            column.add(next[i]);
        }
        return column;
    }

    private String[] getHeaderRow(List<Column> fileColumns) {
        String[] columnNames = new String[fileColumns.size()];
        int i = 0;
        String target = "";
        for (Column c : fileColumns) {
            if (!c.isTarget())
                columnNames[i++] = c.getKey();
            else
                target = c.getKey();

        }
        columnNames[i] = target;
        return columnNames;
    }

    private List<Column> exportColumnCSV(String[] headerrow, Map<String, String> rowsToRemoveMap,
                                         List<String> columnsToRemoveList, List<CleaningSpecialColumn> specialColumns, File file) throws Exception {
        List<Column> columnNames = new ArrayList<Column>();
        //Getting Existing Columns
        int specialColumnFoundCount = 0;
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

            for (CleaningSpecialColumn csc : specialColumns) {
                if (!csc.isExistbyDefault())
                    for (String s : csc.getAlternativeHeaderNames()) {
                        if (s.equals(columnName)) {
                            csc.setExistbyDefault(true);
                            column.setTarget(csc.isTarget());
                            csc.setExistingColumn(column);
                            specialColumnFoundCount++;
                            break;
                        }
                    }
            }

            if (!exclusion && !columnName.equals("")) {
                columnNames.add(column);
            }
        }
        if (specialColumnFoundCount < specialColumns.size()) {
            findSpecialColumnsFromOtherFiles(columnNames, specialColumns, file);
        } else if (specialColumnFoundCount > specialColumns.size()) {
            throw new Exception("Special Columns Error check the code ");
        }

        for (Column c : columnNames) {
            for (Map.Entry<String, String> entry : rowsToRemoveMap.entrySet()) {
                if (c.isIsnative()) {
                    if (c.getKey().equals(entry.getKey())) {
                        c.setRowToCheck(true);
                        c.setCheckValue(Double.parseDouble(entry.getValue().trim()));
                        break;
                    }
                } else {
                    for (String s : c.getAlternativeHeaderNames()) {
                        if (s.equals(entry.getKey())) {
                            c.setRowToCheck(true);
                            c.setCheckValue(Double.parseDouble(entry.getValue().trim()));
                            break;
                        }
                    }
                }
            }
        }

        return columnNames;
    }

    private void findSpecialColumnsFromOtherFiles(List<Column> columnNames, List<CleaningSpecialColumn> specialColumns, File file) throws Exception {
        for (CleaningSpecialColumn cns : specialColumns) {
            List<String> column = lookForNecesseryColumn(cns.getAlternativeHeaderNames(), file);
            if (column != null) {
                cns.setExternalColumnSet(column);
                cns.setExistbyDefault(false);
                Column cl = new Column(cns.getAlternativeHeaderNames().get(0), 0);
                cl.setAlternativeHeaderNames(cns.getAlternativeHeaderNames());
                cl.setIsnative(false);
                cl.setColumn(column);
                columnNames.add(cl);
            } else {
                throw new Exception("Necessery fields cannot be found in project directory.Necessery column :  " + cns.getAlternativeHeaderNames() + " File :" + file.getAbsolutePath());
            }
        }
    }
}