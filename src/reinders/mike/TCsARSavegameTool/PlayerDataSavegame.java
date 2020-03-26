package reinders.mike.TCsARSavegameTool;

import com.fasterxml.jackson.core.*;
import qowyn.ark.ArkSavFile;
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
    public static final Float[] KNOWN__MOD_VERSIONS = new Float[] { 12.6f, 12.7f, 12.8f };

    private Float modVersion = null;
    private List<Player> players = null;

    public PlayerDataSavegame() {
        this.modVersion = PlayerDataSavegame.latestModVersion();
        this.players = new ArrayList<>();
    }

    public PlayerDataSavegame(Path path) throws SaveGameException {
        this.load(path);
    }

    public PlayerDataSavegame(Path path, boolean ignoreVersion) throws SaveGameException {
        this.load(path, ignoreVersion);
    }

    private static Float latestModVersion() {
        if (PlayerDataSavegame.KNOWN__MOD_VERSIONS.length > 1) {
            return PlayerDataSavegame.KNOWN__MOD_VERSIONS[PlayerDataSavegame.KNOWN__MOD_VERSIONS.length - 1];
        } else {
            return null;
        }
    }

    private static boolean matchModVersion(Float modVersion) {
        if (modVersion == null) {
            return false;
        }

        for (float f : PlayerDataSavegame.KNOWN__MOD_VERSIONS) {
            if (f == modVersion) {
                return true;
            }
        }

        return false;
    }

    public void load(Path path) throws SaveGameException {
        this.load(path, false);
    }

    public void load(Path path, boolean ignoreVersion) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile(path);

            String className = (String)ObjectA.getPrivateField(file, PlayerKnownProperties.CLASS_NAME);
            if (className == null || !className.equals(PlayerDataSavegame.KNOWN__CLASS_NAME)) {
                throw new SaveGameException("File is not a PlayerData-Savegame!");
            }

            // Result Variables
            Float modVersion = null;
            List<Player> playerData = null;

            // Cache-References
            Property<?> prop;

            if ((prop = file.getProperty(PlayerKnownProperties.MOD_VERSION)) != null) {
                modVersion = ((PropertyFloat)prop).getValue();
            }

            if (!ignoreVersion && !PlayerDataSavegame.matchModVersion(modVersion)) {
                throw new ModVersionMismatchException(PlayerDataSavegame.latestModVersion(), modVersion == null? 12.5f: modVersion);
            }

            if ((prop = file.getProperty(PlayerKnownProperties.PLAYER_DATA)) != null) {
                playerData = new ArrayList<>();
                for (Object obj : ((PropertyArray)prop).getValue()) {
                    StructPropertyList playerPropertyList = (StructPropertyList)obj;

                    Player player = new Player();

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.PLAYER_NAME)) != null) {
                        player.setName(((PropertyStr)prop).getValue());
                    }

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.STEAM_ID_64)) != null) {
                        player.setSteamID64(Long.parseLong(((PropertyStr)prop).getValue()));
                    }

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.POINTS)) != null) {
                        player.setPoints(((PropertyInt)prop).getValue());
                    }

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.TOTAL_EARNED)) != null) {
                        player.setTotalEarned(((PropertyInt)prop).getValue());
                    }

                    // player specific bonus amount per timespan
                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.INCOME)) != null) {
                        player.setIncome(((PropertyInt)prop).getValue());
                    }

                    // time played (used to payout income when target amount is reached; then it resets to 0)
                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.INCOME_FRACTION)) != null) {
                        player.setIncomeFraction(((PropertyFloat)prop).getValue());
                    }

                    // float: in seconds
                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.TOTAL_PLAYED_TIME)) != null) {
                        player.setTotalPlayedTime(((PropertyFloat)prop).getValue());
                    }

                    // time played (used to payout bonus when target amount is reached; then it resets to 0)
                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.INCOME_FRACTION)) != null) {
                        player.setTimeFraction(((PropertyFloat)prop).getValue());
                    }

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.ELIGIBLE_FOR_BONUS)) != null) {
                        player.setEligibleForBonus(((PropertyBool)prop).getValue());
                    }

                    // player specific bonus amount per timespan
                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.BONUS_AMOUNT)) != null) {
                        player.setBonusAmount(((PropertyInt)prop).getValue());
                    }

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.NOTIFY)) != null) {
                        player.setNotify(((PropertyBool)prop).getValue());
                    }

                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.PLAYER_VERSION)) != null) {
                        player.setPlayerVersion(((PropertyInt)prop).getValue());
                    }

                    // Query List Values
                    if ((prop = playerPropertyList.getProperty(PlayerKnownProperties.PACK_REQUIREMENTS)) != null) {
                        StructPropertyList packRequirements = (StructPropertyList)(((PropertyStruct)prop).getValue());

                        // Final Lists
                        List<String> customTagsList = null;
                        List<String> purchasePIDsList = null;
                        HashMap<String, Integer> purchaseLimitsList = null;
                        HashMap<String, Float> purchaseCooldownsList = null;

                        if ((prop = packRequirements.getProperty(PlayerKnownProperties.CUSTOM_TAGS)) != null) {
                            customTagsList = new ArrayList<>();

                            for(Object obj2 : ((PropertyArray)prop).getValue()) {
                                customTagsList.add((String)obj2);
                            }
                        }

                        if ((prop = packRequirements.getProperty(PlayerKnownProperties.PURCHASED_PIDs)) != null) {
                            purchasePIDsList = new ArrayList<>();

                            for(Object obj2 : ((PropertyArray)prop).getValue()) {
                                purchasePIDsList.add((String)obj2);
                            }
                        }

                        if ((prop = packRequirements.getProperty(PlayerKnownProperties.PURCHASE_LIMITS)) != null) {
                            purchaseLimitsList = new HashMap<>();

                            for (Object obj2 : ((PropertyArray)prop).getValue()) {
                                purchaseLimitsList.put(
                                        ((PropertyStr) ((StructPropertyList)obj2).getProperty(PlayerKnownProperties.PURCHASE_LIMITS_PID)).getValue(),
                                        ((PropertyInt) ((StructPropertyList)obj2).getProperty(PlayerKnownProperties.PURCHASE_LIMITS_REMAINING)).getValue()
                                );
                            }
                        }

                        if ((prop = packRequirements.getProperty(PlayerKnownProperties.PURCHASE_COOLDOWNS)) != null) {
                            purchaseCooldownsList = new HashMap<>();

                            for (Object obj2 : ((PropertyArray)prop).getValue()) {
                                purchaseCooldownsList.put(
                                        ((PropertyStr) ((StructPropertyList) obj2).getProperty(PlayerKnownProperties.PURCHASE_COOLDOWNS_PID)).getValue(),
                                        ((PropertyFloat) ((StructPropertyList) obj2).getProperty(PlayerKnownProperties.PURCHASE_COOLDOWNS_UNLOCK_TIME)).getValue()
                                );
                            }
                        }

                        // Add Lists/Nulls to Player Object
                        player.setCustomTags(customTagsList);
                        player.setPurchasedPIDs(purchasePIDsList);
                        player.setPurchaseLimits(purchaseLimitsList);
                        player.setPurchaseCooldowns(purchaseCooldownsList);
                    }

                    playerData.add(player);
                }
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
        this.loadJson(path, false);
    }

    public void loadJson(Path path, boolean ignoreVersion) throws SaveGameException {
        try {
            JsonFactory jsonFactory = new JsonFactory();

            Float modVersion = null;
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
                        case PlayerKnownPropertiesSimplified.MOD_VERSION:
                            modVersion = jsonParser.getFloatValue();
                            break;
                        case PlayerKnownPropertiesSimplified.PLAYER_DATA:
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
                                            case PlayerKnownPropertiesSimplified.PLAYER_NAME:
                                                player.setName(jsonParser.getText());
                                                break;
                                            case PlayerKnownPropertiesSimplified.STEAM_ID_64:
                                                player.setSteamID64(jsonParser.getLongValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.POINTS:
                                                player.setPoints(jsonParser.getIntValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.TOTAL_EARNED:
                                                player.setTotalEarned(jsonParser.getIntValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.INCOME:
                                                player.setIncome(jsonParser.getIntValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.INCOME_FRACTION:
                                                player.setIncomeFraction(jsonParser.getFloatValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.TOTAL_PLAYED_TIME:
                                                player.setTotalPlayedTime(jsonParser.getFloatValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.TIME_FRACTION:
                                                player.setTimeFraction(jsonParser.getFloatValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.ELIGIBLE_FOR_BONUS:
                                                player.setEligibleForBonus(jsonParser.getBooleanValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.BONUS_AMOUNT:
                                                player.setBonusAmount(jsonParser.getIntValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.NOTIFY:
                                                player.setNotify(jsonParser.getBooleanValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.PLAYER_VERSION:
                                                player.setPlayerVersion(jsonParser.getIntValue());
                                                break;
                                            case PlayerKnownPropertiesSimplified.PACK_REQUIREMENTS:
                                                if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
                                                    throw new SaveGameException("Invalid Savegame Format: Expected Json Object, got '" + jsonParser.currentToken().toString() + "'");
                                                }

                                                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                                    fieldName3 = jsonParser.getCurrentName();
                                                    jsonParser.nextToken();

                                                    switch (fieldName3) {
                                                        case PlayerKnownPropertiesSimplified.CUSTOM_TAGS:
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
                                                        case PlayerKnownPropertiesSimplified.PURCHASED_PIDs:
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
                                                        case PlayerKnownPropertiesSimplified.PURCHASE_LIMITS:
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
                                                                                case PlayerKnownPropertiesSimplified.PURCHASE_LIMITS_PID:
                                                                                    limitPID = jsonParser.getText();
                                                                                    break;
                                                                                case PlayerKnownPropertiesSimplified.PURCHASE_LIMITS_REMAINING:
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
                                                        case PlayerKnownPropertiesSimplified.PURCHASE_COOLDOWNS:
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
                                                                                case PlayerKnownPropertiesSimplified.PURCHASE_LIMITS_PID:
                                                                                    cooldownPID = jsonParser.getText();
                                                                                    break;
                                                                                case PlayerKnownPropertiesSimplified.PURCHASE_LIMITS_REMAINING:
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

            if (!ignoreVersion && !PlayerDataSavegame.matchModVersion(modVersion)) {
                throw new ModVersionMismatchException(PlayerDataSavegame.latestModVersion(), modVersion == null? 12.5f: modVersion);
            }

            this.modVersion = modVersion;
            this.players = playerData;
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to load SaveGame from json", throwable);
        }
    }

    public void unload() {
        this.modVersion = null;
        this.players = null;
    }

    public void save(Path path) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile();
            ObjectA.setPrivateField(file, PlayerKnownProperties.CLASS_NAME, PlayerDataSavegame.KNOWN__CLASS_NAME);

            List<Property<?>> fileProperties = new ArrayList<>();

            // ModVersion
            PropertyFloat modVersion = new PropertyFloat(PlayerKnownProperties.MOD_VERSION, this.modVersion);
            fileProperties.add(modVersion);

            // PlayerData
            ArkArrayStruct playerDataArray = new ArkArrayStruct();
            PropertyArray playerData = new PropertyArray(PlayerKnownProperties.PLAYER_DATA, playerDataArray);

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
                playerProperties.add(new PropertyStr(PlayerKnownProperties.PLAYER_NAME, player.getName()));
                playerProperties.add(new PropertyStr(PlayerKnownProperties.STEAM_ID_64, String.valueOf(player.getSteamID64())));
                playerProperties.add(new PropertyInt(PlayerKnownProperties.POINTS, player.getPoints()));
                playerProperties.add(new PropertyInt(PlayerKnownProperties.TOTAL_EARNED, player.getTotalEarned()));
                playerProperties.add(new PropertyInt(PlayerKnownProperties.INCOME, player.getIncome()));
                playerProperties.add(new PropertyFloat(PlayerKnownProperties.INCOME_FRACTION, player.getIncomeFraction()));
                playerProperties.add(new PropertyFloat(PlayerKnownProperties.TOTAL_PLAYED_TIME, player.getTotalPlayedTime()));
                playerProperties.add(new PropertyFloat(PlayerKnownProperties.TIME_FRACTION, player.getTimeFraction()));
                playerProperties.add(new PropertyBool(PlayerKnownProperties.ELIGIBLE_FOR_BONUS, player.isEligibleForBonus()));
                playerProperties.add(new PropertyInt(PlayerKnownProperties.BONUS_AMOUNT, player.getBonusAmount()));
                playerProperties.add(new PropertyBool(PlayerKnownProperties.NOTIFY, player.isNotify()));
                playerProperties.add(new PropertyInt(PlayerKnownProperties.PLAYER_VERSION, player.getPlayerVersion()));

                // pack requirements
                packRequirements = new ArrayList<>();

                // Tags
                packCustomTagsArray = new ArkArrayString();
                packCustomTagsArray.addAll(player.getCustomTags());
                packRequirements.add(new PropertyArray(PlayerKnownProperties.CUSTOM_TAGS, packCustomTagsArray));

                // Purchased PIDs
                packPurchasedPIDsArray = new ArkArrayString();
                packPurchasedPIDsArray.addAll(player.getPurchasedPIDs());
                packRequirements.add(new PropertyArray(PlayerKnownProperties.PURCHASED_PIDs, packPurchasedPIDsArray));

                // Purchase Limits
                packPurchasedLimits = new ArkArrayStruct();
                for (Map.Entry<String, Integer> entry : player.getPurchaseLimits().entrySet()) {
                    packPurchasedLimitsList = new ArrayList<>();
                    packPurchasedLimitsList.add(new PropertyStr(PlayerKnownProperties.PURCHASE_LIMITS_PID, entry.getKey()));
                    packPurchasedLimitsList.add(new PropertyInt(PlayerKnownProperties.PURCHASE_LIMITS_REMAINING, entry.getValue()));
                    packPurchasedLimits.add(new StructPropertyList(packPurchasedLimitsList));
                }
                packRequirements.add(new PropertyArray(PlayerKnownProperties.PURCHASE_LIMITS, packPurchasedLimits));

                // Purchase Cooldowns
                packPurchaseCooldowns = new ArkArrayStruct();
                for (Map.Entry<String, Float> entry : player.getPurchaseCooldowns().entrySet()) {
                    packPurchaseCooldownsList = new ArrayList<>();
                    packPurchaseCooldownsList.add(new PropertyStr(PlayerKnownProperties.PURCHASE_COOLDOWNS_PID, entry.getKey()));
                    packPurchaseCooldownsList.add(new PropertyFloat(PlayerKnownProperties.PURCHASE_COOLDOWNS_UNLOCK_TIME, entry.getValue()));
                    packPurchaseCooldowns.add(new StructPropertyList(packPurchaseCooldownsList));
                }
                packRequirements.add(new PropertyArray(PlayerKnownProperties.PURCHASE_COOLDOWNS, packPurchaseCooldowns));

                // finally add pack requirements
                playerProperties.add(new PropertyStruct(PlayerKnownProperties.PACK_REQUIREMENTS, new StructPropertyList(packRequirements), PlayerKnownProperties.PACK_REQUIREMENTS_STRUCT_TYPE));

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
                jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.MOD_VERSION, this.getModVersion());

                // PlayerData
                jsonGenerator.writeArrayFieldStart(PlayerKnownPropertiesSimplified.PLAYER_DATA);

                // Write all Players
                for (Player player : this.getPlayers()) {
                    // Player
                    jsonGenerator.writeStartObject();

                    jsonGenerator.writeStringField(PlayerKnownPropertiesSimplified.PLAYER_NAME, player.getName());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.STEAM_ID_64, player.getSteamID64());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.POINTS, player.getPoints());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.TOTAL_EARNED, player.getTotalEarned());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.INCOME, player.getIncome());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.INCOME_FRACTION, player.getIncomeFraction());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.TOTAL_PLAYED_TIME, player.getTotalPlayedTime());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.TIME_FRACTION, player.getTimeFraction());
                    jsonGenerator.writeBooleanField(PlayerKnownPropertiesSimplified.ELIGIBLE_FOR_BONUS, player.isEligibleForBonus());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.BONUS_AMOUNT, player.getBonusAmount());
                    jsonGenerator.writeBooleanField(PlayerKnownPropertiesSimplified.NOTIFY, player.isNotify());
                    jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.PLAYER_VERSION, player.getPlayerVersion());

                    // Pack Requirements
                    jsonGenerator.writeObjectFieldStart(PlayerKnownPropertiesSimplified.PACK_REQUIREMENTS);

                    // Custom Tags
                    jsonGenerator.writeArrayFieldStart(PlayerKnownPropertiesSimplified.CUSTOM_TAGS);
                    for (String tag : player.getCustomTags()) {
                        jsonGenerator.writeString(tag);
                    }
                    jsonGenerator.writeEndArray();

                    // Purchased PIDs
                    jsonGenerator.writeArrayFieldStart(PlayerKnownPropertiesSimplified.PURCHASED_PIDs);
                    for (String pid : player.getPurchasedPIDs()) {
                        jsonGenerator.writeString(pid);
                    }
                    jsonGenerator.writeEndArray();

                    // Purchase Limits
                    jsonGenerator.writeArrayFieldStart(PlayerKnownPropertiesSimplified.PURCHASE_LIMITS);
                    for (Map.Entry<String, Integer> limit : player.getPurchaseLimits().entrySet()) {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeStringField(PlayerKnownPropertiesSimplified.PURCHASE_LIMITS_PID, limit.getKey());
                        jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.PURCHASE_LIMITS_REMAINING, limit.getValue());
                        jsonGenerator.writeEndObject();
                    }
                    jsonGenerator.writeEndArray();

                    // Purchase Cooldowns
                    jsonGenerator.writeArrayFieldStart(PlayerKnownPropertiesSimplified.PURCHASE_COOLDOWNS);
                    for (Map.Entry<String, Float> cooldown : player.getPurchaseCooldowns().entrySet()) {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeStringField(PlayerKnownPropertiesSimplified.PURCHASE_COOLDOWNS_PID, cooldown.getKey());
                        jsonGenerator.writeNumberField(PlayerKnownPropertiesSimplified.PURCHASE_COOLDOWNS_UNLOCK_TIME, cooldown.getValue());
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