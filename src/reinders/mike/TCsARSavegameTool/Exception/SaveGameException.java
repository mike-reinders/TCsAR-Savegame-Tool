package reinders.mike.TCsARSavegameTool.Exception;

public class SaveGameException extends ToolException {

    public SaveGameException() {
        super();
    }

    public SaveGameException(String message) {
        super(message);
    }

    public SaveGameException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveGameException(Throwable cause) {
        super(cause);
    }

}