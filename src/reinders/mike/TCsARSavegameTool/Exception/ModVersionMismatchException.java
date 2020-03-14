package reinders.mike.TCsARSavegameTool.Exception;

public class ModVersionMismatchException extends SaveGameException {

    private float expectedVersion;

    private float actualVersion;

    public ModVersionMismatchException(float expectedVersion, float version) {
        super("SaveGame: Mod-Version mismatches expected version '" + expectedVersion + "', got '" + version + "'");
        this.expectedVersion = expectedVersion;
        this.actualVersion = version;
    }

    public float getExpectedVersion() {
        return this.expectedVersion;
    }

    public float getActualVersion() {
        return this.actualVersion;
    }

}