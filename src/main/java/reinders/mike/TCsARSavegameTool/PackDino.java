package reinders.mike.TCsARSavegameTool;

public class PackDino {

    private String name;
    private byte type;
    private String dinoClass;
    private boolean dinoClassShort;
    private Integer wildLevelMin;
    private Integer wildLevel;
    private Integer tamedLevelMin;
    private Integer tamedLevel;
    private String entry;
    private boolean entryShort;
    private int quantity;
    private boolean isMultipleChoice;
    private boolean isGenderChoice;
    private byte gender;
    private boolean isNeutered;

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

    public String getDinoClass() {
        return dinoClass;
    }

    public void setDinoClass(String dinoClass) {
        this.dinoClass = dinoClass;
    }

    public boolean isDinoClassShort() {
        return dinoClassShort;
    }

    public void setDinoClassShort(boolean dinoClassShort) {
        this.dinoClassShort = dinoClassShort;
    }

    public Integer getWildLevelMin() {
        return wildLevelMin;
    }

    public void setWildLevelMin(Integer wildLevelMin) {
        this.wildLevelMin = wildLevelMin;
    }

    public Integer getWildLevel() {
        return wildLevel;
    }

    public void setWildLevel(Integer wildLevel) {
        this.wildLevel = wildLevel;
    }

    public Integer getTamedLevelMin() {
        return tamedLevelMin;
    }

    public void setTamedLevelMin(Integer tamedLevelMin) {
        this.tamedLevelMin = tamedLevelMin;
    }

    public Integer getTamedLevel() {
        return tamedLevel;
    }

    public void setTamedLevel(Integer tamedLevel) {
        this.tamedLevel = tamedLevel;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public boolean isEntryShort() {
        return entryShort;
    }

    public void setEntryShort(boolean entryShort) {
        this.entryShort = entryShort;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
    }

    public boolean isGenderChoice() {
        return isGenderChoice;
    }

    public void setGenderChoice(boolean genderChoice) {
        isGenderChoice = genderChoice;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public boolean isNeutered() {
        return isNeutered;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

}