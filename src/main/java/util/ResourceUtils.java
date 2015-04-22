package util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class ResourceUtils {
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
}