package reinders.mike.TCsARSavegameTool.Exception;

public class MissingArgumentException extends Exception implements ToolException {

    public MissingArgumentException() {
        super();
    }

    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingArgumentException(Throwable cause) {
        super(cause);
    }

}