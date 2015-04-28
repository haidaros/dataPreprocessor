package util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

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

}