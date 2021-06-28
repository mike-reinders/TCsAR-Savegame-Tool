package reinders.mike.TCsARSavegameTool.Util;

public final class StringC {

    private StringC() {
        // Empty
    }

    public static String fill(int targetLength) {
        return StringC.fill(' ', targetLength);
    }

    public static String fill(char chr, int targetLength) {
        char[] newValue = new char[targetLength];

        for (int i = 0; i < targetLength; i++) {
            newValue[i] = chr;
        }

        return String.valueOf(newValue);
    }

    public static String pad(Pad pad, String value, int targetLength) {
        return StringC.pad(pad, value, ' ', targetLength);
    }

    public static String pad(Pad pad, String value, char chr, int targetLength) {
        if (value.length() >= targetLength) {
            return value;
        }

        int l = value.length();
        int m = (targetLength - value.length());
        char[] newValue = new char[targetLength];

        if (pad == Pad.LEFT) {
            value.getChars(0, value.length(), newValue, m);
            for (int i = 0; i < m; i++) {
                newValue[i] = chr;
            }
        } else if (pad == Pad.RIGHT) {
            value.getChars(0, value.length(), newValue, 0);
            for (int i = 0; i < m; i++) {
                newValue[l + i] = chr;
            }
        } else {
            throw new IllegalArgumentException("PAD.LEFT or PAD.RIGHT only");
        }

        return String.valueOf(newValue);
    }

    public static String padLines(String text, char chr, int length) {
        char[] pad = new char[length];
        for (int i = 0; i < length; i++) {
            pad[i] = chr;
        }
        String padStr = String.valueOf(pad);

        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\\n");

        for (String line : lines) {
            result.append(padStr);
            result.append(line);
            result.append(System.lineSeparator());
        }

        return result.toString();
    }

}