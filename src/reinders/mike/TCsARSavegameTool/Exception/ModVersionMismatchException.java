package reinders.mike.TCsARSavegameTool.Exception;

public class ModVersionMismatchException extends SaveGameException {

    public ModVersionMismatchException(float expectedVersion) {
        super("SaveGame: Mod-Version mismatches expected version '" + expectedVersion + "'");
    }

}