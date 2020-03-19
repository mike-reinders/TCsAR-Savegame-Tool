package reinders.mike.TCsARSavegameTool.Exception;

public class ModVersionMismatchException extends SaveGameException {

    private Float expectedVersion;

    private Float actualVersion;

    public ModVersionMismatchException(Float expectedVersion, Float version) {
        super("SaveGame: Mod-Version mismatches expected version '" + expectedVersion + "', got '" + version + "'");
        this.expectedVersion = expectedVersion;
        this.actualVersion = version;
    }

    public Float getExpectedVersion() {
        return this.expectedVersion;
    }

    public Float getActualVersion() {
        return this.actualVersion;
    }

}