package reinders.mike.TCsARSavegameTool.Exception;

public class MissingCommandException extends ToolException {

    public MissingCommandException() {
        super();
    }

    public MissingCommandException(String message) {
        super(message);
    }

    public MissingCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingCommandException(Throwable cause) {
        super(cause);
    }

}