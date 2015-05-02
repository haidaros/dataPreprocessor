package batch.model;

import java.util.Map;

/**
 * Created by eg on 02/05/15.
 */
public class Db {
    Map<String, String> locMap;
    Map<String, String> targetMap;

    public Db(Map<String, String> locMap, Map<String, String> targetMap) {
        this.locMap = locMap;
        this.targetMap = targetMap;
    }

    public Map<String, String> getLocMap() {
        return locMap;
    }

    public void setLocMap(Map<String, String> locMap) {
        this.locMap = locMap;
    }

    public Map<String, String> getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(Map<String, String> targetMap) {
        this.targetMap = targetMap;
    }

    public int getLoc(String className) {
        Double v = Double.parseDouble(locMap.get(className.trim()));
        return v.intValue();
    }

    public int getBug(String className) {
        Double v = Double.parseDouble(targetMap.get(className.trim()));
        return v.intValue();
    }
}
