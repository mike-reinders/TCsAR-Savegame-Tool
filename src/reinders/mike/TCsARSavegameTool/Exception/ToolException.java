package reinders.mike.TCsARSavegameTool.Exception;

public class ToolException extends Exception {

    public ToolException() {
        super();
    }

    public ToolException(String message) {
        super(message);
    }

    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToolException(Throwable cause) {
        super(cause);
    }

}