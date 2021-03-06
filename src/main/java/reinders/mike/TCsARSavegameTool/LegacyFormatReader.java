package reinders.mike.TCsARSavegameTool;

import reinders.mike.TCsARSavegameTool.Exception.SaveGameException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Calendar;

public class LegacyFormatReader {

    public static final char DELIMITER_PLAYER = '$';
    public static final char DELIMITER_PLAYER_PROPERTY = '^';

    public static final char DELIMITER_PACK = ']';
    public static final char DELIMITER_PACK_PROPERTY = '[';
    public static final char DELIMITER_PACK_ITEM = ';';
    public static final char DELIMITER_PACK_ITEM_TAG = ',';
    public static final char DELIMITER_PACK_ITEM_PROPERTY = '|';
    public static final char DELIMITER_PACK_ITEM_STAT = '>';
    public static final char DELIMITER_PACK_DINO = ')';
    public static final char DELIMITER_PACK_DINO_PROPERTY = '(';

    public static final char DELIMITER_COLOUR = '%';

    public static final int ITEM_TYPE_ITEM = 1;
    public static final int ITEM_TYPE_DINO = 2;
    public static final int ITEM_TYPE_PACK_END = 3;
    public static final int ITEM_TYPE_NONE = 0;

    private String buffer;
    private int limit = -1;
    private int position = 0;

    public LegacyFormatReader() {}

    public LegacyFormatReader(Path path) throws IOException {
        this.load(path);
    }

    public void load(Path path) throws IOException {
        try (FileInputStream fs = new FileInputStream(path.toFile())) {
            if (fs.getChannel().size() > Integer.MAX_VALUE) {
                throw new RuntimeException("Input file is too large.");
            }

            byte[] buffer = new byte[(int)fs.getChannel().size()];

            int left = buffer.length;
            int totalRead = 0;
            int read;
            while (totalRead < buffer.length) {
                if ((read = fs.read(buffer, totalRead, Math.min(left, 8192))) == 0) {
                    throw new IOException("Insufficient Data");
                }

                left -= read;
                totalRead += read;
            }

            this.buffer = new String(buffer, StandardCharsets.UTF_8);
            this.limit = this.buffer.length() - 1;
            this.position = 0;
        }
    }

    public void unload() {
        this.buffer = null;
        this.limit = -1;
        this.position = 0;
    }

    protected String unifyClass(String clazz) {
        clazz = clazz.replace("~*", "/Game/")
                .replace("~0", "PrimalEarth/CoreBlueprints/Weapons/")
                .replace("~1", "PrimalEarth/CoreBlueprints/Items/")
                .replace("~2", "PrimalEarth/CoreBlueprints/Resources/PrimalItemResource")
                .replace("~3", "PrimalEarth/Dinos/")
                .replace("~4", "PrimalEarth/CoreBlueprints/DinoEntries/DinoEntry")
                .replace("~5", "Aberration/CoreBlueprints/Resources/PrimalItemResource")
                .replace("~6", "Aberration/CoreBlueprints/Dinoentry");

        if (clazz.startsWith("\"Blueprint'")) {
            clazz = clazz.substring("\"Blueprint'".length());
        }

        if (clazz.endsWith("'\"")) {
            clazz = clazz.substring(0, clazz.length() - 2);
        } else {
            String[] split = clazz.split("/");
            clazz = clazz + "." + split[split.length - 1];
        }

        if (clazz.length() == 0) {
            return null;
        }

        if (!clazz.endsWith("_C")) {
            clazz += "_C";
        }

        return clazz;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isLimitReached() {
        return this.position > this.limit;
    }

    public String read(char until) {
        int beginIndex = this.position;
        int endIndex = this.position;

        char chr;
        while (endIndex <= this.limit) {
            chr = this.buffer.charAt(endIndex++);

            if (chr == until) {
                this.position += (endIndex - beginIndex);
                return this.buffer.substring(beginIndex, endIndex - 1);
            }
        }

        throw new BufferUnderflowException();
    }

    public String readProperty() {
        return this.read(LegacyFormatReader.DELIMITER_PACK_PROPERTY);
    }

    public String readItemProperty() {
        return this.read(LegacyFormatReader.DELIMITER_PACK_ITEM_PROPERTY);
    }

    public String readDinoProperty() {
        return this.read(LegacyFormatReader.DELIMITER_PACK_DINO_PROPERTY);
    }

    public char readDelimiter(char delimiter) {
        if (this.isLimitReached()
                || this.buffer.charAt(this.position) != delimiter
        ) {
            throw new SaveGameException("Invalid Legacy-Format: Delimiter '" + delimiter + "' (0x" + String.format("%02x", (int)delimiter) + ") expected at position " + this.position);
        }

        this.position++;
        return delimiter;
    }

    public void skipDelimiter(char delimiter) {
        if (!this.isLimitReached()
                && this.buffer.charAt(this.position) == delimiter
        ) {
            this.position++;
        }
    }

    public char readPackDelimiter() {
        return this.readDelimiter(LegacyFormatReader.DELIMITER_PACK);
    }

    public void skipPackDelimiter() {
        this.skipDelimiter(LegacyFormatReader.DELIMITER_PACK);
    }

    public char readItemDelimiter() {
        return this.readDelimiter(LegacyFormatReader.DELIMITER_PACK_ITEM);
    }

    public void skipItemDelimiter() {
        this.skipDelimiter(LegacyFormatReader.DELIMITER_PACK_ITEM);
    }

    public char readDinoDelimiter() {
        return this.readDelimiter(LegacyFormatReader.DELIMITER_PACK_DINO);
    }

    public void skipDinoDelimiter() {
        this.skipDelimiter(LegacyFormatReader.DELIMITER_PACK_DINO);
    }

    public int getNextItemType() {
        if (this.isLimitReached()
            || this.buffer.charAt(this.position) == LegacyFormatReader.DELIMITER_PACK
        ) {
            return LegacyFormatReader.ITEM_TYPE_PACK_END;
        }

        int ITEM_PROPERTY_DELIMITERS = 0;
        int DINO_PROPERTY_DELIMITERS = 0;

        char chr;
        for (int i = this.position; i < this.buffer.length(); i++) {
            chr = this.buffer.charAt(i);

            if (chr == LegacyFormatReader.DELIMITER_PACK_ITEM_PROPERTY) {
                ITEM_PROPERTY_DELIMITERS++;

                if (ITEM_PROPERTY_DELIMITERS >= 10) {
                    return LegacyFormatReader.ITEM_TYPE_ITEM;
                }
            } else if (chr == LegacyFormatReader.DELIMITER_PACK_DINO_PROPERTY) {
                DINO_PROPERTY_DELIMITERS++;

                if (DINO_PROPERTY_DELIMITERS >= 6) {
                    return LegacyFormatReader.ITEM_TYPE_DINO;
                }
            }
        }

        return LegacyFormatReader.ITEM_TYPE_NONE;
    }

    public String readPackName() {
        return this.readProperty();
    }

    public int readPackPosition() {
        return Integer.parseInt(this.readProperty());
    }

    public int readPackCost() {
        return Integer.parseInt(this.readProperty());
    }

    public String readPackPID() {
        return this.readProperty();
    }

    public String readPackPIDNew() {
        // <PID>Y<Year>D<DayOfYear>T<TimeOfDayInMilliseconds>

        String pid = this.readPackPID();

        if (pid.startsWith("PID")) {
            pid = pid.substring(3);
        }

        try {
            pid = String.valueOf(Integer.MAX_VALUE - Integer.parseInt(pid));
        } catch (NumberFormatException ignored) {}

        Calendar calendar = Calendar.getInstance();
        return pid + "Y" + calendar.get(Calendar.YEAR) + "D" + calendar.get(Calendar.DAY_OF_YEAR) + "T" + ((((((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) * 60) + calendar.get(Calendar.SECOND)) * 1000) + calendar.get(Calendar.MILLISECOND));
    }

    public String[] readTags() {
        String tags = this.readProperty();

        if (tags.length() == 0) {
            return new String[0];
        }

        return tags.split(String.valueOf(LegacyFormatReader.DELIMITER_PACK_ITEM_TAG));
    }

    public int readPackPurchaseLimit() {
        return Integer.parseInt(this.readProperty());
    }

    public boolean readPackIsAdminOnly() {
        return Integer.parseInt(this.readProperty()) == 1;
    }

    public String readItemName() {
        return this.readItemProperty();
    }

    public byte readItemType() {
        return (byte)Integer.parseInt(this.readItemProperty());
    }

    public String readItemClass() {
        return this.unifyClass(this.readItemProperty());
    }

    public boolean readItemIsBlueprint() {
        return Integer.parseInt(this.readItemProperty()) == 1;
    }

    public String readItemQualityName() {
        return this.readItemProperty();
    }

    public float[] readItemQualityColour() {
        String[] parts = this.readItemProperty().split(String.valueOf(LegacyFormatReader.DELIMITER_COLOUR));
        float[] floats = new float[4];

        if (parts.length != 4) {
            throw new NumberFormatException("Insufficient Numbers");
        }

        for (int i = 0; i < parts.length; i++) {
            floats[i] = Float.parseFloat(parts[i]);
        }

        return floats;
    }

    public byte readItemQuality() {
        return (byte)Integer.parseInt(this.readItemProperty());
    }

    public int readItemQuantity() {
        return Integer.parseInt(this.readItemProperty());
    }

    public float readItemRating() {
        return Float.parseFloat(this.readItemProperty());
    }

    public boolean readItemIsMultipleChoice() {
        return Integer.parseInt(this.readItemProperty()) == 1;
    }

    public float readItemDisplayedStat() {
        return Float.parseFloat(this.read(DELIMITER_PACK_ITEM_STAT));
    }

    public String readDinoName() {
        return this.readDinoProperty();
    }

    public byte readDinoType() {
        return (byte)Integer.parseInt(this.readDinoProperty());
    }

    public String readDinoClass() {
        return this.unifyClass(this.readDinoProperty());
    }

    public int[] readDinoWildLevel() {
        String[] parts = this.readDinoProperty().split("-");

        if (parts.length > 2) {
            throw new SaveGameException("Invalid Legacy-Format: Dino Wild Level at position " + this.position);
        }

        int[] levels = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            levels[i] = Integer.parseInt(parts[i]);
        }

        return levels;
    }

    public String readDinoEntry() {
        return this.unifyClass(this.readDinoProperty());
    }

    public int readDinoQuantity() {
        return Integer.parseInt(this.readDinoProperty());
    }

    public boolean readDinoIsMultipleChoice() {
        return Integer.parseInt(this.read(LegacyFormatReader.DELIMITER_PACK_DINO)) == 1;
    }

}