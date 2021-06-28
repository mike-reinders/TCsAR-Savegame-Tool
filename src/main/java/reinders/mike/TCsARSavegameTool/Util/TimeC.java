package reinders.mike.TCsARSavegameTool.Util;

public final class TimeC {

    private TimeC() {
        // Empty
    }

    public static String TimeToString(float time) {
        StringBuilder strBuilder = new StringBuilder();

        int days = (int)(time / (60 * 60 * 24));
        int hours = (int)((time = (time - (days * (60 * 60 * 24)))) / (60 * 60));
        int minutes = (int)((time = (time - (hours * (60 * 60)))) / 60);
        int seconds = (int)(time = (time - (minutes * 60)));

        if (days > 0) {
            strBuilder.append(days);
            strBuilder.append("d");
        }
        if (hours > 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(hours);
            strBuilder.append("h");
        }
        if (minutes > 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(minutes);
            strBuilder.append("m");
        }
        if (seconds > 0 || strBuilder.length() == 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(seconds);
            strBuilder.append("s");
        }

        return strBuilder.toString();
    }

}