package batch.model;

/**
 * Created by eg on 01/05/15.
 */
public class DbItem {
    String classname;
    String loc;
    String numberofbugs;

    public DbItem(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getNumberofbugs() {
        return numberofbugs;
    }

    public void setNumberofbugs(String numberofbugs) {
        this.numberofbugs = numberofbugs;
    }

    public String[] toArray() {
        return new String[]{classname, loc, numberofbugs};
    }
}
