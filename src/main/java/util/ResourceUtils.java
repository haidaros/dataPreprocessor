package util;

import batch.model.Db;
import com.opencsv.CSVReader;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ResourceUtils {
    static XMLConfiguration config;

    //TODO this class should be instantiateable with the name of the resource.
    public static File getFile(String name) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        return new File(url.getPath());
    }

    public static FileInputStream getStream(String name) {
        return (FileInputStream) Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }

    public static String getPath(String name) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        return url.getPath();
    }

    public static void setUpconfig() {
        try {
            config = new XMLConfiguration(getPath("parameters.xml"));
        } catch (ConfigurationException ce) {
            System.out.println(ce.toString());
        }
    }

    public static XMLConfiguration getConfig() {
        if (config == null)
            setUpconfig();
        return config;
    }

    public static Db readDB(String dbName) {
        try {
            CSVReader reader = new CSVReader(new FileReader(dbName), ';');
            Iterator<String[]> iterator = reader.iterator();
            Map<String, String> locMap = new HashMap<String, String>();
            Map<String, String> targetMap = new HashMap<String, String>();
            iterator.next(); // for skiping header
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                locMap.put(next[0], next[1]);
                targetMap.put(next[0], next[2]);
            }
            return new Db(locMap, targetMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static LinkedList<String> readHeaders(String dbName) {
        try {
            LinkedList<String> list = new LinkedList<String>();
            CSVReader reader = new CSVReader(new FileReader(dbName), ';');
            Iterator<String[]> iterator = reader.iterator();
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                list.add(next[0]);
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}