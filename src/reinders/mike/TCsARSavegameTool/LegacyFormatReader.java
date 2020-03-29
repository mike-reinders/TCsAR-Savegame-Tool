package reinders.mike.TCsARSavegameTool;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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
        clazz = clazz.replace("~*", "") // "Blueprint'/Game/
                .replace("~0", "PrimalEarth/CoreBlueprints/Weapons/")
                .replace("~1", "PrimalEarth/CoreBlueprints/Items/")
                .replace("~2", "PrimalEarth/CoreBlueprints/Resources/PrimalItemResource")
                .replace("~3", "PrimalEarth/Dinos/")
                .replace("~4", "PrimalEarth/CoreBlueprints/DinoEntries/DinoEntry")
                .replace("~5", "Aberration/CoreBlueprints/Resources/PrimalItemResource")
                .replace("~6", "Aberration/CoreBlueprints/Dinoentry");

        if (clazz.endsWith("'\"")) {
            clazz = clazz.substring(0, clazz.length() - 3);
        } else {
            String[] split = clazz.split("/");
            clazz = clazz + "." + split[split.length - 1];
        }

        return clazz;
    }

    protected boolean isLimitReached() {
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
                return this.buffer.substring(beginIndex, endIndex);
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

    public void skipPackDelimiter() {
        if (!this.isLimitReached()
            && this.buffer.charAt(this.position) == LegacyFormatReader.DELIMITER_PACK
        ) {
            this.position++;
        }
    }

    public void skipItemDelimiter() {
        if (!this.isLimitReached()
                && this.buffer.charAt(this.position) == LegacyFormatReader.DELIMITER_PACK_ITEM
        ) {
            this.position++;
        }
    }

    public int getNextItemType() {
        if (this.isLimitReached()
            || this.buffer.charAt(this.position) == LegacyFormatReader.DELIMITER_PACK
        ) {
            return LegacyFormatReader.ITEM_TYPE_PACK_END;
        }

        int ITEM_PROPERTY_POS = this.buffer.indexOf(String.valueOf(LegacyFormatReader.DELIMITER_PACK_ITEM_PROPERTY), this.position);
        int DINO_PROPERTY_POS = this.buffer.indexOf(String.valueOf(LegacyFormatReader.DELIMITER_PACK_DINO_PROPERTY), this.position);

        if (ITEM_PROPERTY_POS < DINO_PROPERTY_POS) {
            return LegacyFormatReader.ITEM_TYPE_ITEM;
        } else if (DINO_PROPERTY_POS < ITEM_PROPERTY_POS) {
            return LegacyFormatReader.ITEM_TYPE_DINO;
        } else {
            return LegacyFormatReader.ITEM_TYPE_NONE;
        }
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

    public String readPID() {
        return this.readProperty();
    }

    public String[] readTags() {
        return this.readProperty().split(String.valueOf(LegacyFormatReader.DELIMITER_PACK_ITEM_TAG));
    }

    public int readPurchaseLimit() {
        return Integer.parseInt(this.readProperty());
    }

    public boolean readAdminOnly() {
        return Integer.parseInt(this.readProperty()) == 1;
    }

    public String readItemName() {
        return this.readItemProperty();
    }

    public byte readItemType() {
        return (byte)Integer.parseInt(this.readProperty());
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

    public int readItemDisplayedStat() {
        return Integer.parseInt(this.read(DELIMITER_PACK_ITEM_STAT));
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

    public int readDinoWildLevel() {
        return Integer.parseInt(this.readDinoProperty());
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