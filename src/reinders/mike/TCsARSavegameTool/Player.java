package reinders.mike.TCsARSavegameTool;

import reinders.mike.TCsARSavegameTool.Util.SteamIDC;

import java.util.*;

public final class Player {

    private String name;
    private long steamID64;
    private String steamID3;
    private String steamID;
    private int points;
    private int totalEarned;
    private int income;
    private float incomeFraction;
    private float totalPlayedTime;
    private float timeFraction;
    private boolean eligibleForBonus;
    private int bonusAmount;
    private boolean notify;
    private List<String> customTags = new ArrayList<>();
    private List<String> purchasedPIDs = new ArrayList<>();
    private HashMap<String, Integer> purchaseLimits = new HashMap<>();
    private HashMap<String, Float> purchaseCooldowns = new HashMap<>();

    public Player() {
        // Empty
    }

    public Player clone() throws CloneNotSupportedException {
        Player newPlayer = new Player();

        newPlayer.setName(this.getName());
        newPlayer.setSteamID64(this.getSteamID64());
        newPlayer.setPoints(this.getPoints());
        newPlayer.setTotalEarned(this.getTotalEarned());
        newPlayer.setIncome(this.getIncome());
        newPlayer.setIncomeFraction(this.getIncomeFraction());
        newPlayer.setTotalPlayedTime(this.getTotalPlayedTime());
        newPlayer.setTimeFraction(this.getTimeFraction());
        newPlayer.setEligibleForBonus(this.isEligibleForBonus());
        newPlayer.setBonusAmount(this.getBonusAmount());
        newPlayer.setNotify(this.isNotify());

        newPlayer.getCustomTags().addAll(Arrays.asList(this.getCustomTags().toArray(new String[0])));
        newPlayer.getPurchasedPIDs().addAll(Arrays.asList(this.getPurchasedPIDs().toArray(new String[0])));

        HashMap<String, Integer> purchaseLimits = new HashMap<>();
        for (Map.Entry<String, Integer> limit : this.getPurchaseLimits().entrySet()) {
            purchaseLimits.put(limit.getKey(), limit.getValue());
        }
        newPlayer.setPurchaseLimits(purchaseLimits);

        HashMap<String, Float> purchaseCooldowns = new HashMap<>();
        for (Map.Entry<String, Float> cooldown : this.getPurchaseCooldowns().entrySet()) {
            purchaseCooldowns.put(cooldown.getKey(), cooldown.getValue());
        }
        newPlayer.setPurchaseCooldowns(purchaseCooldowns);

        return newPlayer;
    }

    public String getName() {
        return this.name;
    }

    public Player setName(String name) {
        this.name = name;

        return this;
    }

    public long getSteamID64() {
        return this.steamID64;
    }

    public Player setSteamID64(long steamID64) {
        this.steamID64 = steamID64;

        return this;
    }

    public String getSteamID3() {
        if (this.steamID3 == null) {
            this.steamID3 = SteamIDC.getSteamID3(this.getSteamID64());
        }

        return this.steamID3;
    }

    public String getSteamID() {
        if (this.steamID == null) {
            this.steamID = SteamIDC.getSteamID(this.getSteamID64());
        }

        return this.steamID;
    }

    public int getPoints() {
        return points;
    }

    public Player setPoints(int points) {
        this.points = points;

        return this;
    }

    public int getTotalEarned() {
        return this.totalEarned;
    }

    public Player setTotalEarned(int totalEarned) {
        this.totalEarned = totalEarned;

        return this;
    }

    public int getIncome() {
        return income;
    }

    public Player setIncome(int income) {
        this.income = income;

        return this;
    }

    public float getIncomeFraction() {
        return incomeFraction;
    }

    public Player setIncomeFraction(float incomeFraction) {
        this.incomeFraction = incomeFraction;

        return this;
    }

    public float getTotalPlayedTime() {
        return totalPlayedTime;
    }

    public Player setTotalPlayedTime(float totalPlayedTime) {
        this.totalPlayedTime = totalPlayedTime;

        return this;
    }

    public float getTimeFraction() {
        return timeFraction;
    }

    public Player setTimeFraction(float timeFraction) {
        this.timeFraction = timeFraction;

        return this;
    }

    public boolean isEligibleForBonus() {
        return eligibleForBonus;
    }

    public Player setEligibleForBonus(boolean eligibleForBonus) {
        this.eligibleForBonus = eligibleForBonus;

        return this;
    }

    public int getBonusAmount() {
        return bonusAmount;
    }

    public Player setBonusAmount(int bonusAmount) {
        this.bonusAmount = bonusAmount;

        return this;
    }

    public boolean isNotify() {
        return this.notify;
    }

    public Player setNotify(boolean notify) {
        this.notify = notify;

        return this;
    }

    public List<String> getCustomTags() {
        return this.customTags;
    }

    void setCustomTags(List<String> customTags) {
        this.customTags = customTags;
    }

    public List<String> getPurchasedPIDs() {
        return this.purchasedPIDs;
    }

    void setPurchasedPIDs(List<String> purchasedPIDs) {
        this.purchasedPIDs = purchasedPIDs;
    }

    public HashMap<String, Integer> getPurchaseLimits() {
        return this.purchaseLimits;
    }

    void setPurchaseLimits(HashMap<String, Integer> purchaseLimits) {
        this.purchaseLimits = purchaseLimits;
    }

    public HashMap<String, Float> getPurchaseCooldowns() {
        return this.purchaseCooldowns;
    }

    void setPurchaseCooldowns(HashMap<String, Float> purchaseCooldowns) {
        this.purchaseCooldowns = purchaseCooldowns;
    }
    
}