package reinders.mike.TCsARSavegameTool;

import java.util.List;

public class PackItem {

    private String name;
    private byte type;
    private String itemClass;
    private boolean itemClassShort;
    private boolean isBlueprint;
    private String qualityName;
    private Color qualityColour;
    private byte quality;
    private int quantity;
    private float itemRating;
    private List<PackItemStat> itemStats;
    private boolean isMultipleChoice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public boolean isItemClassShort() {
        return itemClassShort;
    }

    public void setItemClassShort(boolean itemClassShort) {
        this.itemClassShort = itemClassShort;
    }

    public boolean isBlueprint() {
        return isBlueprint;
    }

    public void setBlueprint(boolean blueprint) {
        isBlueprint = blueprint;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public Color getQualityColour() {
        return qualityColour;
    }

    public void setQualityColour(Color qualityColour) {
        this.qualityColour = qualityColour;
    }

    public byte getQuality() {
        return quality;
    }

    public void setQuality(byte quality) {
        this.quality = quality;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getItemRating() {
        return itemRating;
    }

    public void setItemRating(float itemRating) {
        this.itemRating = itemRating;
    }

    public List<PackItemStat> getItemStats() {
        return itemStats;
    }

    public void setItemStats(List<PackItemStat> itemStats) {
        this.itemStats = itemStats;
    }

    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
    }

}