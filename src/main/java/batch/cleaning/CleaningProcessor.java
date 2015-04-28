package batch.cleaning;

import batch.model.CleaningData;
import batch.model.CleaningNecesseryColumn;
import batch.model.CleaningTargetHeader;
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
        ArrayList<CleaningTargetHeader> targetList = new ArrayList<CleaningTargetHeader>();
        //Getting Configurations
        List<HierarchicalConfiguration> configList = ResourceUtils.getConfig().configurationsAt("cleanup.rows-to-remove.item");
        for (HierarchicalConfiguration c : configList) {
            rowsToRemoveMap.put(c.getString("header"), c.getString("value"));
        }
        configList = ResourceUtils.getConfig().configurationsAt("cleanup.columns-to-remove.item");
        for (HierarchicalConfiguration c : configList) {
            columnsToRemoveList.add(c.getString("header"));
        }
        List<CleaningNecesseryColumn> necesseryColumns = new ArrayList<CleaningNecesseryColumn>();
        configList = ResourceUtils.getConfig().configurationsAt("cleanup.necessery-columns.item");
        for (HierarchicalConfiguration c : configList) {
            necesseryColumns.add(new CleaningNecesseryColumn(c.getList("header")));
        }

        List<Object> list = ResourceUtils.getConfig().getList("cleanup.target-columns.header");
        List<CleaningData> processResult = new ArrayList<CleaningData>();
        for (File f : files) {
            List<String[]> result = cleanFile(f, rowsToRemoveMap, columnsToRemoveList, list);
            CleaningData cleaningData = new CleaningData(f, result);
            processResult.add(cleaningData);
        }
        return processResult;
    }

    private List<String[]> cleanFile(File file, Map<String, String> rowsToRemoveMap,
                                     List<String> columnsToRemoveList, List<Object> targetList) throws Exception {
        CleaningTargetHeader cleaningTargetHeader = new CleaningTargetHeader(targetList);

        List<CleaningNecesseryColumn> necesseryColumns = new ArrayList<CleaningNecesseryColumn>();
        List<HierarchicalConfiguration> configList = ResourceUtils.getConfig().configurationsAt("cleanup.necessery-columns.item");
        for (HierarchicalConfiguration c : configList) {
            necesseryColumns.add(new CleaningNecesseryColumn(c.getList("header")));
        }
        CSVReader reader = new CSVReader(new FileReader(file), ';');
        Iterator<String[]> iterator = reader.iterator();
        String[] columnNames = iterator.next();
        List<Column> fileColumns = exportColumnCSV(columnNames, rowsToRemoveMap, columnsToRemoveList, cleaningTargetHeader, necesseryColumns);
        //check Lines of Code
        List<CleaningNecesseryColumn> nfNColumns = checkNecesseryFiles(necesseryColumns, file);
        mergeexternalColumn(fileColumns, nfNColumns, rowsToRemoveMap);
        List<String[]> items = new LinkedList<String[]>();
        items.add(getHeaderRow(fileColumns));
        int rowCount = 0;
        while (iterator.hasNext()) {
            String[] entry = iterator.next();
            String[] values = new String[fileColumns.size()];
            boolean removethisrow = false;
            String target = "";
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
                    target = v;
                } else {
                    values[i++] = v;
                }
            }
            values[i++] = target;
            if (!removethisrow)
                items.add(values);
            rowCount++;
        }
        return items;
    }

    private void mergeexternalColumn(List<Column> fileColumns, List<CleaningNecesseryColumn> nfNColumns, Map<String, String> rowsToRemoveMap) {
        for (CleaningNecesseryColumn c : nfNColumns) {
            Column col = new Column(c.getHeaderNames().get(0), 0);
            col.setColumn(c.getColumn());
            col.setIsnative(false);
            for (Map.Entry<String, String> entry : rowsToRemoveMap.entrySet()) {
                if (entry.getKey().equals(c.getHeaderNames().get(0)))
                    col.setRowToCheck(true);
            }
            fileColumns.add(col);
        }
    }

    private List<CleaningNecesseryColumn> checkNecesseryFiles(List<CleaningNecesseryColumn> necesseryColumns, File file) throws Exception {
        boolean allfound = true;
        List<CleaningNecesseryColumn> notFoundNecesseryColumns = new ArrayList<CleaningNecesseryColumn>();
        for (CleaningNecesseryColumn cns : necesseryColumns) {
            if (!cns.isExistbyDefault()) {
                allfound = false;
                notFoundNecesseryColumns.add(cns);
            }
        }

        if (!allfound) {
            for (CleaningNecesseryColumn cns : notFoundNecesseryColumns) {
                List<String> column = lookForNecesseryColumn(cns.getHeaderNames(), file);
                if (column != null) {
                    cns.setColumn(column);
                } else {
                    throw new Exception("Necessery fields cannot be found in project directory.Necessery column :  " + cns.getHeaderNames() + " File :" + file.getAbsolutePath());
                }
            }
        }
        return notFoundNecesseryColumns;
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
                                         List<String> columnsToRemoveList, CleaningTargetHeader targetHeader, List<CleaningNecesseryColumn> necesseryColumns) throws Exception {
        List<Column> columnNames = new ArrayList<Column>();
        for (int i = 0; i < headerrow.length; i++) {
            boolean exclusion = false;
            String cname = headerrow[i].trim();
            if (columnsToRemoveList != null) {
                for (String s : columnsToRemoveList) {
                    if (cname.equals(s.trim())) {
                        exclusion = true;
                    }
                }
            }
            Column column = new Column(cname, i);
            if (!targetHeader.isFound())
                for (String header : targetHeader.getAlternativeHeaderName()) {
                    if (header.equals(cname)) {
                        column.setTarget(true);
                        targetHeader.setFound(true);
                        targetHeader.setTargetColum(column);
                    }
                }


            for (Map.Entry<String, String> entry : rowsToRemoveMap.entrySet()) {
                if (entry.getKey().equals(cname))
                    column.setRowToCheck(true);
            }


            for (CleaningNecesseryColumn cns : necesseryColumns) {
                boolean found = false;
                for (String s : cns.getHeaderNames()) {
                    if (cname.contains(s))
                        cns.setExistbyDefault(true);
                }
            }

            if (!exclusion && !cname.equals("")) {
                columnNames.add(column);
            }
        }
        if (!targetHeader.isFound())
            throw new Exception("All Target Headers is not Exist on this file");
        return columnNames;
    }
}
