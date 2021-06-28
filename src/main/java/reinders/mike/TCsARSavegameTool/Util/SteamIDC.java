package reinders.mike.TCsARSavegameTool.Util;

public final class SteamIDC {

    private SteamIDC() {
        // Empty
    }

    public static String getSteamID3(long steam64ID) {
        return "[" + SteamIDC.getTypeLetter(steam64ID) + ":" + SteamIDC.getUniverse(steam64ID) + ":" + SteamIDC.getIDNumber(steam64ID) + "]";
    }

    public static String getSteamID(long steam64ID) {
        return "STEAM_" + SteamIDC.getUniverse(steam64ID) + ":" + SteamIDC.getID(steam64ID) + ":" + SteamIDC.getNumber(steam64ID);
    }

    public static int getIDNumber(long steam64ID) {
        return (int)steam64ID;
    }

    public static int getID(long steam64ID) {
        return (int)(steam64ID & 0b1);
    }

    public static int getNumber(long steam64ID) {
        return (int)((steam64ID >> 1) & 0b01111111_11111111_11111111_11111111);
    }

    public static int getInstance(long steam64ID) {
        return (int)((steam64ID >> (1 + 31)) & 0b1111_11111111_11111111);
    }

    public static int getType(long steam64ID) {
        return (int)((steam64ID >> (1 + 31 + 20)) & 0b1111);
    }

    public static char getTypeLetter(long steam64ID) {
        int type = SteamIDC.getType(steam64ID);

        switch (type) {
            case 0:
                return 'I';
            case 1:
                return 'U';
            case 2:
                return 'M';
            case 3:
                return 'G';
            case 4:
                return 'A';
            case 5:
                return 'P';
            case 6:
                return 'C';
            case 7:
                return 'g';
            case 8:
                return 'T';
            case 9:
                return ' ';
            case 10:
                return 'a';
            default:
                return '-';
        }
    }

    public static int getUniverse(long steam64ID) {
        return (int)((steam64ID >> (1 + 31 + 20 + 4)) & 0b11111111);
    }

}