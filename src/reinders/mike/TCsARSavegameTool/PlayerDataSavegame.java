package reinders.mike.TCsARSavegameTool;

import com.fasterxml.jackson.core.*;
import qowyn.ark.ArkSavFile;
import qowyn.ark.arrays.ArkArray;
import qowyn.ark.arrays.ArkArrayString;
import qowyn.ark.arrays.ArkArrayStruct;
import qowyn.ark.properties.*;
import qowyn.ark.structs.StructPropertyList;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.Exception.SaveGameException;
import reinders.mike.TCsARSavegameTool.Util.ObjectA;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDataSavegame {

    public static final String KNOWN__CLASS_NAME = "TCsAR_SavedPlayerData_C";
    public static final Float KNOWN__MOD_VERSION = 1.12f;

    private float modVersion = -1;
    private List<Player> players;

    public PlayerDataSavegame() {
        this.modVersion = PlayerDataSavegame.KNOWN__MOD_VERSION;
        this.players = new ArrayList<>();
    }

    public PlayerDataSavegame(Path path) throws SaveGameException {
        this.load(path);
    }

    public void load(Path path) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile(path);

            String className = (String)ObjectA.getPrivateField(file, KnownProperties.CLASS_NAME);
            if (className == null || !className.equals(PlayerDataSavegame.KNOWN__CLASS_NAME)) {
                throw new SaveGameException("File is not a PlayerData-Savegame!");
            }

            float modVersion = ((PropertyFloat)file.getProperty(KnownProperties.MOD_VERSION)).getValue();
            if (modVersion != PlayerDataSavegame.KNOWN__MOD_VERSION) {
                throw new ModVersionMismatchException(PlayerDataSavegame.KNOWN__MOD_VERSION);
            }

            List<Player> playerData = new ArrayList<>();
            ArkArray<?> playerData_array = ((PropertyArray)file.getProperty(KnownProperties.PLAYER_DATA)).getValue();

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
                    purchaseCooldownsList.put(
                            ((PropertyStr)purchaseCooldown.getProperty(KnownProperties.PURCHASE_COOLDOWNS_PID)).getValue(),
                            ((PropertyFloat)purchaseCooldown.getProperty(KnownProperties.PURCHASE_COOLDOWNS_UNLOCK_TIME)).getValue()
                    );
                }

                // Add Lists to Player Object
                player.setCustomTags(customTagsList);
                player.setPurchasedPIDs(purchasePIDsList);
                player.setPurchaseLimits(purchaseLimitsList);
                player.setPurchaseCooldowns(purchaseCooldownsList);

                playerData.add(player);
            }

            this.modVersion = modVersion;
            this.players = playerData;
        } catch (SaveGameException ex) {
            throw ex;
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to load SaveGame", throwable);
        }
    }

    public void loadJson(Path path) throws SaveGameException {
        try {
            JsonFactory jsonFactory = new JsonFactory();

            float modVersion = -1;
            List<Player> playerData = new ArrayList<>();

            try (JsonParser jsonParser = jsonFactory.createParser(path.toFile())) {
                if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                    throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                }

                String fieldName;
                String fieldName2;
                String fieldName3;
                String fieldName4;
                Player player;
                List<String> customTags;
                List<String> purchasedPIDs;
                HashMap<String, Integer> purchaseLimits;
                HashMap<String, Float> purchaseCooldowns;
                String limitPID = null;
                Integer limitRemaining = null;
                String cooldownPID = null;
                Float cooldownUnlockTime = null;

                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    fieldName = jsonParser.getCurrentName();
                    jsonParser.nextToken();

                    switch (fieldName) {
                        case KnownPropertiesSimplified.MOD_VERSION:
                            modVersion = jsonParser.getFloatValue();
                            break;
                        case KnownPropertiesSimplified.PLAYER_DATA:
                            if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                    if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
                                        throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                                    }

                                    player = new Player();
                                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                        fieldName2 = jsonParser.getCurrentName();
                                        jsonParser.nextToken();

                                        switch (fieldName2) {
                                            case KnownPropertiesSimplified.PLAYER_NAME:
                                                player.setName(jsonParser.getText());
                                                break;
                                            case KnownPropertiesSimplified.STEAM_ID_64:
                                                player.setSteamID64(jsonParser.getLongValue());
                                                break;
                                            case KnownPropertiesSimplified.POINTS:
                                                player.setPoints(jsonParser.getIntValue());
                                                break;
                                            case KnownPropertiesSimplified.TOTAL_EARNED:
                                                player.setTotalEarned(jsonParser.getIntValue());
                                                break;
                                            case KnownPropertiesSimplified.INCOME:
                                                player.setIncome(jsonParser.getIntValue());
                                                break;
                                            case KnownPropertiesSimplified.INCOME_FRACTION:
                                                player.setIncomeFraction(jsonParser.getFloatValue());
                                                break;
                                            case KnownPropertiesSimplified.TOTAL_PLAYED_TIME:
                                                player.setTotalPlayedTime(jsonParser.getFloatValue());
                                                break;
                                            case KnownPropertiesSimplified.TIME_FRACTION:
                                                player.setTimeFraction(jsonParser.getFloatValue());
                                                break;
                                            case KnownPropertiesSimplified.ELIGIBLE_FOR_BONUS:
                                                player.setEligibleForBonus(jsonParser.getBooleanValue());
                                                break;
                                            case KnownPropertiesSimplified.BONUS_AMOUNT:
                                                player.setBonusAmount(jsonParser.getIntValue());
                                                break;
                                            case KnownPropertiesSimplified.NOTIFY:
                                                player.setNotify(jsonParser.getBooleanValue());
                                                break;
                                            case KnownPropertiesSimplified.PACK_REQUIREMENTS:
                                                if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
                                                    throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                                                }

                                                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                                    fieldName3 = jsonParser.getCurrentName();
                                                    jsonParser.nextToken();

                                                    switch (fieldName3) {
                                                        case KnownPropertiesSimplified.CUSTOM_TAGS:
                                                            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                                                                customTags = new ArrayList<>();

                                                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                                    customTags.add(jsonParser.getText());
                                                                }

                                                                player.setCustomTags(customTags);
                                                            } else if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                                                throw new SaveGameException("Invalid Savegame Format: Expected Json Array, got '" + jsonParser.currentToken().toString() + "'");
                                                            }
                                                            break;
                                                        case KnownPropertiesSimplified.PURCHASED_PIDs:
                                                            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                                                                purchasedPIDs = new ArrayList<>();

                                                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                                    purchasedPIDs.add(jsonParser.getText());
                                                                }

                                                                player.setPurchasedPIDs(purchasedPIDs);
                                                            } else if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                                                throw new SaveGameException("Invalid Savegame Format: Expected Json Array, got '" + jsonParser.currentToken().toString() + "'");
                                                            }
                                                            break;
                                                        case KnownPropertiesSimplified.PURCHASE_LIMITS:
                                                            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                                                                purchaseLimits = new HashMap<>();

                                                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                                    if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                                                                        limitPID = null;
                                                                        limitRemaining = null;

                                                                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                                                            fieldName4 = jsonParser.getCurrentName();
                                                                            jsonParser.nextToken();

                                                                            switch (fieldName4) {
                                                                                case KnownPropertiesSimplified.PURCHASE_LIMITS_PID:
                                                                                    limitPID = jsonParser.getText();
                                                                                    break;
                                                                                case KnownPropertiesSimplified.PURCHASE_LIMITS_REMAINING:
                                                                                    limitRemaining = jsonParser.getIntValue();
                                                                                    break;
                                                                                default:
                                                                                    jsonParser.skipChildren();
                                                                                    break;
                                                                            }
                                                                        }

                                                                        if (limitPID == null || limitRemaining == null) {
                                                                            throw new SaveGameException("Invalid Savegame Format: Missing values in Purchase Limits");
                                                                        }

                                                                        purchaseLimits.put(limitPID, limitRemaining);
                                                                    } else if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                                                        throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                                                                    }
                                                                }

                                                                player.setPurchaseLimits(purchaseLimits);
                                                            } else if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                                                throw new SaveGameException("Invalid Savegame Format: Expected Json Array, got '" + jsonParser.currentToken().toString() + "'");
                                                            }
                                                            break;
                                                        case KnownPropertiesSimplified.PURCHASE_COOLDOWNS:
                                                            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                                                                purchaseCooldowns = new HashMap<>();

                                                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                                    if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                                                                        cooldownPID = null;
                                                                        cooldownUnlockTime = null;

                                                                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                                                            fieldName4 = jsonParser.getCurrentName();
                                                                            jsonParser.nextToken();

                                                                            switch (fieldName4) {
                                                                                case KnownPropertiesSimplified.PURCHASE_LIMITS_PID:
                                                                                    cooldownPID = jsonParser.getText();
                                                                                    break;
                                                                                case KnownPropertiesSimplified.PURCHASE_LIMITS_REMAINING:
                                                                                    cooldownUnlockTime = jsonParser.getFloatValue();
                                                                                    break;
                                                                                default:
                                                                                    jsonParser.skipChildren();
                                                                                    break;
                                                                            }
                                                                        }

                                                                        if (cooldownPID == null || cooldownUnlockTime == null) {
                                                                            throw new SaveGameException("Invalid Savegame Format: Missing values in Purchase Cooldowns");
                                                                        }

                                                                        purchaseCooldowns.put(cooldownPID, cooldownUnlockTime);
                                                                    } else if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                                                        throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                                                                    }
                                                                }

                                                                player.setPurchaseCooldowns(purchaseCooldowns);
                                                            } else if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                                                throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                                                            }

                                                            break;
                                                        default:
                                                            jsonParser.skipChildren();
                                                            break;
                                                    }
                                                }
                                                break;
                                            default:
                                                jsonParser.skipChildren();
                                                break;
                                        }
                                    }
                                    playerData.add(player);
                                }
                            } else if (jsonParser.currentToken() != JsonToken.VALUE_NULL) {
                                throw new SaveGameException("Invalid Savegame Format: Expected Json Array or null, got '" + jsonParser.currentToken().toString() + "'");
                            }
                            break;
                        default:
                            jsonParser.skipChildren();
                            break;
                    }
                }
            }

            this.modVersion = modVersion;
            this.players = playerData;
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to load SaveGame from json", throwable);
        }
    }

    public void unload() {
        this.modVersion = -1;
        this.players = null;
    }

    public void save(Path path) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile();
            ObjectA.setPrivateField(file, KnownProperties.CLASS_NAME, PlayerDataSavegame.KNOWN__CLASS_NAME);

            List<Property<?>> fileProperties = new ArrayList<>();

            // ModVersion
            PropertyFloat modVersion = new PropertyFloat(KnownProperties.MOD_VERSION, this.modVersion);
            fileProperties.add(modVersion);

            // PlayerData
            ArkArrayStruct playerDataArray = new ArkArrayStruct();
            PropertyArray playerData = new PropertyArray(KnownProperties.PLAYER_DATA, playerDataArray);

            // Pack PlayerData
            List<Property<?>> playerProperties;
            List<Property<?>> packRequirements;
            ArkArrayString packCustomTagsArray;
            ArkArrayString packPurchasedPIDsArray;
            ArkArrayStruct packPurchasedLimits;
            List<Property<?>> packPurchasedLimitsList;
            ArkArrayStruct packPurchaseCooldowns;
            List<Property<?>> packPurchaseCooldownsList;
            for (Player player : this.players) {
                playerProperties = new ArrayList<>();

                // add simple properties
                playerProperties.add(new PropertyStr(KnownProperties.PLAYER_NAME, player.getName()));
                playerProperties.add(new PropertyStr(KnownProperties.STEAM_ID_64, String.valueOf(player.getSteamID64())));
                playerProperties.add(new PropertyInt(KnownProperties.POINTS, player.getPoints()));
                playerProperties.add(new PropertyInt(KnownProperties.TOTAL_EARNED, player.getTotalEarned()));
                playerProperties.add(new PropertyInt(KnownProperties.INCOME, player.getIncome()));
                playerProperties.add(new PropertyFloat(KnownProperties.INCOME_FRACTION, player.getIncomeFraction()));
                playerProperties.add(new PropertyFloat(KnownProperties.TOTAL_PLAYED_TIME, player.getTotalPlayedTime()));
                playerProperties.add(new PropertyFloat(KnownProperties.TIME_FRACTION, player.getTimeFraction()));
                playerProperties.add(new PropertyBool(KnownProperties.ELIGIBLE_FOR_BONUS, player.isEligibleForBonus()));
                playerProperties.add(new PropertyInt(KnownProperties.BONUS_AMOUNT, player.getBonusAmount()));
                playerProperties.add(new PropertyBool(KnownProperties.NOTIFY, player.isNotify()));

                // pack requirements
                packRequirements = new ArrayList<>();

                // Tags
                packCustomTagsArray = new ArkArrayString();
                packCustomTagsArray.addAll(player.getCustomTags());
                packRequirements.add(new PropertyArray(KnownProperties.CUSTOM_TAGS, packCustomTagsArray));

                // Purchased PIDs
                packPurchasedPIDsArray = new ArkArrayString();
                packPurchasedPIDsArray.addAll(player.getPurchasedPIDs());
                packRequirements.add(new PropertyArray(KnownProperties.PURCHASED_PIDs, packPurchasedPIDsArray));

                // Purchase Limits
                packPurchasedLimits = new ArkArrayStruct();
                for (Map.Entry<String, Integer> entry : player.getPurchaseLimits().entrySet()) {
                    packPurchasedLimitsList = new ArrayList<>();
                    packPurchasedLimitsList.add(new PropertyStr(KnownProperties.PURCHASE_LIMITS_PID, entry.getKey()));
                    packPurchasedLimitsList.add(new PropertyInt(KnownProperties.PURCHASE_LIMITS_REMAINING, entry.getValue()));
                    packPurchasedLimits.add(new StructPropertyList(packPurchasedLimitsList));
                }
                packRequirements.add(new PropertyArray(KnownProperties.PURCHASE_LIMITS, packPurchasedLimits));

                // Purchase Cooldowns
                packPurchaseCooldowns = new ArkArrayStruct();
                for (Map.Entry<String, Float> entry : player.getPurchaseCooldowns().entrySet()) {
                    packPurchaseCooldownsList = new ArrayList<>();
                    packPurchaseCooldownsList.add(new PropertyStr(KnownProperties.PURCHASE_COOLDOWNS_PID, entry.getKey()));
                    packPurchaseCooldownsList.add(new PropertyFloat(KnownProperties.PURCHASE_COOLDOWNS_UNLOCK_TIME, entry.getValue()));
                    packPurchaseCooldowns.add(new StructPropertyList(packPurchaseCooldownsList));
                }
                packRequirements.add(new PropertyArray(KnownProperties.PURCHASE_COOLDOWNS, packPurchaseCooldowns));

                // finally add pack requirements
                playerProperties.add(new PropertyStruct(KnownProperties.PACK_REQUIREMENTS, new StructPropertyList(packRequirements), KnownProperties.PACK_REQUIREMENTS_STRUCT_TYPE));

                // add player
                playerDataArray.add(new StructPropertyList(playerProperties));
            }
            fileProperties.add(playerData);

            // add fileProperties
            file.setProperties(fileProperties);

            file.writeBinary(path);
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to save SaveGame", throwable);
        }
    }

    public void saveJson(Path path) throws SaveGameException {
        this.saveJson(path, false);
    }

    public void saveJson(Path path, boolean pretty) throws SaveGameException {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(path.toFile(), JsonEncoding.UTF8)) {
                if (pretty) {
                    jsonGenerator.useDefaultPrettyPrinter();
                }

                // JsonFile
                jsonGenerator.writeStartObject();

                // ModVersion
                jsonGenerator.writeNumberField(KnownPropertiesSimplified.MOD_VERSION, this.getModVersion());

                // PlayerData
                jsonGenerator.writeArrayFieldStart(KnownPropertiesSimplified.PLAYER_DATA);

                // Write all Players
                for (Player player : this.getPlayers()) {
                    // Player
                    jsonGenerator.writeStartObject();

                    jsonGenerator.writeStringField(KnownPropertiesSimplified.PLAYER_NAME, player.getName());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.STEAM_ID_64, player.getSteamID64());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.POINTS, player.getPoints());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.TOTAL_EARNED, player.getTotalEarned());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.INCOME, player.getIncome());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.INCOME_FRACTION, player.getIncomeFraction());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.TOTAL_PLAYED_TIME, player.getTotalPlayedTime());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.TIME_FRACTION, player.getTimeFraction());
                    jsonGenerator.writeBooleanField(KnownPropertiesSimplified.ELIGIBLE_FOR_BONUS, player.isEligibleForBonus());
                    jsonGenerator.writeNumberField(KnownPropertiesSimplified.BONUS_AMOUNT, player.getBonusAmount());
                    jsonGenerator.writeBooleanField(KnownPropertiesSimplified.NOTIFY, player.isNotify());

                    // Pack Requirements
                    jsonGenerator.writeObjectFieldStart(KnownPropertiesSimplified.PACK_REQUIREMENTS);

                    // Custom Tags
                    jsonGenerator.writeArrayFieldStart(KnownPropertiesSimplified.CUSTOM_TAGS);
                    for (String tag : player.getCustomTags()) {
                        jsonGenerator.writeString(tag);
                    }
                    jsonGenerator.writeEndArray();

                    // Purchased PIDs
                    jsonGenerator.writeArrayFieldStart(KnownPropertiesSimplified.PURCHASED_PIDs);
                    for (String pid : player.getPurchasedPIDs()) {
                        jsonGenerator.writeString(pid);
                    }
                    jsonGenerator.writeEndArray();

                    // Purchase Limits
                    jsonGenerator.writeArrayFieldStart(KnownPropertiesSimplified.PURCHASE_LIMITS);
                    for (Map.Entry<String, Integer> limit : player.getPurchaseLimits().entrySet()) {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeStringField(KnownPropertiesSimplified.PURCHASE_LIMITS_PID, limit.getKey());
                        jsonGenerator.writeNumberField(KnownPropertiesSimplified.PURCHASE_LIMITS_REMAINING, limit.getValue());
                        jsonGenerator.writeEndObject();
                    }
                    jsonGenerator.writeEndArray();

                    // Purchase Cooldowns
                    jsonGenerator.writeArrayFieldStart(KnownPropertiesSimplified.PURCHASE_COOLDOWNS);
                    for (Map.Entry<String, Float> cooldown : player.getPurchaseCooldowns().entrySet()) {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeStringField(KnownPropertiesSimplified.PURCHASE_COOLDOWNS_PID, cooldown.getKey());
                        jsonGenerator.writeNumberField(KnownPropertiesSimplified.PURCHASE_COOLDOWNS_UNLOCK_TIME, cooldown.getValue());
                        jsonGenerator.writeEndObject();
                    }
                    jsonGenerator.writeEndArray();

                    // Pack Requirements
                    jsonGenerator.writeEndObject();

                    // Player
                    jsonGenerator.writeEndObject();
                }

                // PlayerData
                jsonGenerator.writeEndArray();

                // JsonFile
                jsonGenerator.writeEndObject();
            }
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to save SaveGame as json format", throwable);
        }
    }

    public float getModVersion() {
        return this.modVersion;
    }

    public void setModVersion(float modVersion) {
        this.modVersion = modVersion;
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

    public boolean hasPlayer(String steam64ID) {
        return this.hasPlayer(Long.parseLong(steam64ID));
    }

    public boolean hasPlayer(long steam64ID) {
        for (Player player : this.players) {
            if (player.getSteamID64() == steam64ID) {
                return true;
            }
        }

        return false;
    }

    public Player getPlayerByName(String name) {
        return this.getPlayerByName(name, false);
    }

    public Player getPlayerByName(String name, boolean ignoreCase) {
        for (Player player : this.players) {
            if ((ignoreCase? player.getName().equalsIgnoreCase(name): player.getName().equals(name))) {
                return player;
            }
        }

        return null;
    }

    public Player putPlayer(Player player) {
        Player plr;
        for (int i = 0; i < this.players.size(); i++) {
            plr = this.players.get(i);
            if (plr.getSteamID64() == player.getSteamID64()) {
                this.players.set(i, player);
                return plr;
            }
        }

        return null;
    }

}