package reinders.mike.TCsARSavegameTool;

import qowyn.ark.ArkSavFile;
import qowyn.ark.arrays.ArkArray;
import qowyn.ark.properties.*;
import qowyn.ark.structs.StructPropertyList;
import reinders.mike.TCsARSavegameTool.Exception.SaveGameException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerDataFile {

    public static final String KNOWN__CLASS_NAME = "TCsAR_SavedPlayerData_C";

    private int playerVersion = -1;
    private List<Player> players;

    public PlayerDataFile() {
        // Empty
    }

    public PlayerDataFile(Path path) throws SaveGameException {
        this.load(path);
    }

    public void load(Path path) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile(path);

            String className = PlayerDataFile.getClassName(file);
            if (className == null || !className.equals(PlayerDataFile.KNOWN__CLASS_NAME)) {
                throw new SaveGameException("File is not a PlayerData-Savegame!");
            }

            int playerVersion = ((PropertyInt)file.getProperty("PlayerVersion")).getValue();
            if (playerVersion < 0) {
                throw new SaveGameException("SaveGame: PlayerVersion must not be negative.");
            }

            List<Player> playerData = new ArrayList<>();
            ArkArray<?> playerData_array = ((PropertyArray)file.getProperty("PlayerData")).getValue();

            for (Object obj : playerData_array) {
                StructPropertyList playerPropertyList = (StructPropertyList)obj;

                Player player = new Player();
                player.setName(((PropertyStr)playerPropertyList.getProperty(KnownProperties.PLAYER_NAME)).getValue());
                player.setSteamID64(Long.parseLong(((PropertyStr)playerPropertyList.getProperty(KnownProperties.STEAM_ID_64)).getValue()));
                player.setPoints(((PropertyInt)playerPropertyList.getProperty(KnownProperties.POINTS)).getValue());
                player.setTotalEarned(((PropertyInt)playerPropertyList.getProperty(KnownProperties.TOTAL_EARNED)).getValue());
                player.setIncome(((PropertyInt)playerPropertyList.getProperty(KnownProperties.INCOME)).getValue()); // player specific bonus amount per timespan
                player.setIncomeFraction(((PropertyFloat)playerPropertyList.getProperty(KnownProperties.INCOME_FRACTION)).getValue()); // time played (used to payout income when target amount is reached; then it resets to 0)
                player.setTotalPlayedTime(((PropertyFloat)playerPropertyList.getProperty(KnownProperties.TOTAL_PLAYED_TIME)).getValue()); // float: in seconds
                player.setTimeFraction(((PropertyFloat)playerPropertyList.getProperty(KnownProperties.TIME_FRACTION)).getValue()); // time played (used to payout bonus when target amount is reached; then it resets to 0)
                player.setEligibleForBonus(((PropertyBool)playerPropertyList.getProperty(KnownProperties.ELIGIBLE_FOR_BONUS)).getValue());
                player.setBonusAmount(((PropertyInt)playerPropertyList.getProperty(KnownProperties.BONUS_AMOUNT)).getValue()); // player specific bonus amount per timespan
                // player.setTags(null); // Always null // @Deprecated value
                player.setNotify(((PropertyBool)playerPropertyList.getProperty(KnownProperties.NOTIFY)).getValue());

                // Query List Values
                StructPropertyList packRequirements = (StructPropertyList)((PropertyStruct)playerPropertyList.getProperty(KnownProperties.PACK_REQUIREMENTS)).getValue();
                ArkArray<?> customTags = ((PropertyArray)packRequirements.getProperty(KnownProperties.CUSTOM_TAGS)).getValue(); // List of String
                ArkArray<?> purchasePIDs = ((PropertyArray)packRequirements.getProperty(KnownProperties.PURCHASED_PIDs)).getValue(); // List of String
                ArkArray<?> purchaseLimits = ((PropertyArray)packRequirements.getProperty(KnownProperties.PURCHASE_LIMITS)).getValue(); // List of Array{ String:PID, Integer:Remaining }
                ArkArray<?> purchaseCooldowns = ((PropertyArray)packRequirements.getProperty(KnownProperties.PURCHASE_COOLDOWNS)).getValue(); // List of Array{ String:PID, Float:UnlockTime }

                // Prepare Final Lists
                List<String> customTagsList = new ArrayList<>();
                List<String> purchasePIDsList = new ArrayList<>();
                HashMap<String, Integer> purchaseLimitsList = new HashMap<>();
                HashMap<String, Float> purchaseCooldownsList = new HashMap<>();

                // Add values to all lists
                for(Object obj2 : customTags) {
                    customTagsList.add((String)obj2);
                }
                for(Object obj2 : purchasePIDs) {
                    purchasePIDsList.add((String)obj2);
                }
                for(Object obj2 : purchaseLimits) {
                    StructPropertyList purchaseLimit = (StructPropertyList)obj2;
                    purchaseLimitsList.put(
                            ((PropertyStr)purchaseLimit.getProperty(KnownProperties.PURCHASE_LIMITS_PID)).getValue(),
                            ((PropertyInt)purchaseLimit.getProperty(KnownProperties.PURCHASE_LIMITS_REMAINING)).getValue()
                    );
                }
                for(Object obj2 : purchaseCooldowns) {
                    StructPropertyList purchaseCooldown = (StructPropertyList)obj2;
                    purchaseLimitsList.put(
                            ((PropertyStr)purchaseCooldown.getProperty(KnownProperties.PURCHASE_COOLDOWNS_PID)).getValue(),
                            ((PropertyInt)purchaseCooldown.getProperty(KnownProperties.PURCHASE_COOLDOWNS_UNLOCK_TIME)).getValue()
                    );
                }

                // Add Lists to Player Object
                player.setCustomTags(customTagsList);
                player.setPurchasedPIDs(purchasePIDsList);
                player.setPurchaseLimits(purchaseLimitsList);
                player.setPurchaseCooldowns(purchaseCooldownsList);

                playerData.add(player);
            }

            this.playerVersion = playerVersion;
            this.players = playerData;
        } catch (SaveGameException ex) {
            throw ex;
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to load SaveGame", throwable);
        }
    }

    public void unload() {
        this.playerVersion = -1;
        this.players = null;
    }

    public void save(Path path) {
        // @TODO
    }

    public int getPlayerVersion() {
        return this.playerVersion;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public Player getPlayer(String steam64ID) {
        return this.getPlayer(Long.parseLong(steam64ID));
    }

    public Player getPlayer(long steam64ID) {
        for (Player player : this.players) {
            if (player.getSteamID64() == steam64ID) {
                return player;
            }
        }

        return null;
    }

    public Player getPlayerByName(String name) {
        return this.getPlayerByName(name, true);
    }

    public Player getPlayerByName(String name, boolean ignoreCase) {
        for (Player player : this.players) {
            if ((ignoreCase? player.getName().equalsIgnoreCase(name): player.getName().equals(name))) {
                return player;
            }
        }

        return null;
    }

    private static String getClassName(ArkSavFile arkSavFile) {
        try {
            Field field = arkSavFile.getClass().getDeclaredField("className");
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            return (String)field.get(arkSavFile);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return null;
        }
    }

}