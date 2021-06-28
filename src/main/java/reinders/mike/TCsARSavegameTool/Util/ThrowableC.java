package reinders.mike.TCsARSavegameTool.Util;

public final class ThrowableC {

    private ThrowableC() {
        // Empty
    }

    public static String toString(Throwable throwable) {
        StringBuilder strBuilder = new StringBuilder();

        do {
            if (strBuilder.length() == 0) {
                strBuilder.append("Exception in thread \"");
                strBuilder.append(Thread.currentThread().getName());
                strBuilder.append("\" ");
            } else {
                strBuilder.append("Caused by: ");
            }

            strBuilder.append(throwable.getClass().getCanonicalName());
            strBuilder.append(": ");
            strBuilder.append(throwable.getMessage());
            strBuilder.append(System.lineSeparator());

            for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                strBuilder.append("        at ");
                strBuilder.append(stackTraceElement.getClassName());
                strBuilder.append(".");
                strBuilder.append(stackTraceElement.getMethodName());
                strBuilder.append("(");
                strBuilder.append(stackTraceElement.getFileName());
                strBuilder.append(":");
                strBuilder.append(stackTraceElement.getLineNumber());
                strBuilder.append(")");
                strBuilder.append(System.lineSeparator());
            }
        } while ((throwable = throwable.getCause()) != null);

        return strBuilder.toString();
    }

}