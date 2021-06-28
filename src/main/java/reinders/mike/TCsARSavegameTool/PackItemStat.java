package reinders.mike.TCsARSavegameTool;

public class PackItemStat {

    private String name;
    private float displayed;
    private int calculated;
    private boolean isUsed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDisplayed() {
        return displayed;
    }

    public void setDisplayed(float displayed) {
        this.displayed = displayed;
    }

    public int getCalculated() {
        return calculated;
    }

    public void setCalculated(int calculated) {
        this.calculated = calculated;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

}