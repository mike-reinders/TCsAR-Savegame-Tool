package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingArgumentException;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.Player;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;
import reinders.mike.TCsARSavegameTool.SavegameTool;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class MergeCommand extends Command {

    //            String name;                      // NO OPTIONS
    // no options at all

    //            long steamID64;                   // NO OPTIONS
    // no options at all

    //            int points;                       // STACK (default), RESET, SET TO x, LOWEST, HIGHEST,
    public static final int MERGE_POINTS_ADD_ALL = 1;
    public static final int MERGE_POINTS_SET = 2;
    public static final int MERGE_POINTS_LOWEST = 3;
    public static final int MERGE_POINTS_HIGHEST = 4;

    //            int totalEarned;                  // NO OPTIONS
    // no options at all

    //            int income;                       // RESET, SET TO x, LOWEST, HIGHEST (default)
    public static final int MERGE_INCOME_SET = 1;
    public static final int MERGE_INCOME_LOWEST = 2;
    public static final int MERGE_INCOME_HIGHEST = 3;

    //            float incomeFraction;             // RESET, SET TO x, LOWEST, HIGHEST (default)
    public static final int MERGE_INCOME_FRACTION_SET = 1;
    public static final int MERGE_INCOME_FRACTION_LOWEST = 2;
    public static final int MERGE_INCOME_FRACTION_HIGHEST = 3;

    //            float totalPlayedTime;            // STACK (default), RESET, SET TO x
    public static final int MERGE_TOTAL_PLAYED_TIME_ADD_ALL = 1;
    public static final int MERGE_TOTAL_PLAYED_TIME_SET = 2;

    //            float timeFraction;               // RESET, SET TO x, LOWEST, HIGHEST (default)
    public static final int MERGE_TIME_FRACTION_SET = 1;
    public static final int MERGE_TIME_FRACTION_LOWEST = 2;
    public static final int MERGE_TIME_FRACTION_HIGHEST = 3;

    //            boolean eligibleForBonus;         // SET TO x
    public static final int MERGE_ELIGIBLE_FOR_BONUS_REGULAR = 0;
    public static final int MERGE_ELIGIBLE_FOR_BONUS_SET = 1;

    //            int bonusAmount;                  // RESET, SET TO x, LOWEST, HIGHEST (default)
    public static final int MERGE_BONUS_AMOUNT_SET = 1;
    public static final int MERGE_BONUS_AMOUNT_LOWEST = 2;
    public static final int MERGE_BONUS_AMOUNT_HIGHEST = 3;

    //            boolean notify;                   // SET TO x
    public static final int MERGE_NOTIFY_REGULAR = 0;
    public static final int MERGE_NOTIFY_SET = 1;

    //            <String> customTags;              // PRESENCE (default), RESET, SET to x, +ENSURE x, +OMIT x
    public static final int MERGE_TAGS_PRESENCE = 1;
    public static final int MERGE_TAGS_SET = 2;
    // no extra option for ENSURE
    // no extra option for OMIT

    //            <String> purchasedPIDs;           // PRESENCE (default), RESET, SET to x, +ENSURE x, +OMIT x
    public static final int MERGE_PURCHASED_PIDS_PRESENCE = 1;
    public static final int MERGE_PURCHASED_PIDS_SET = 2;
    // no extra option for ENSURE
    // no extra option for OMIT

    //            <String, Integer> purchaseLimits; // RESET, LOWEST, HIGHEST (default), +OMIT x, +SET y to X
    public static final int MERGE_PURCHASE_LIMITS_RESET = 1;
    public static final int MERGE_PURCHASE_LIMITS_LOWEST = 2;
    public static final int MERGE_PURCHASE_LIMITS_HIGHEST = 3;
    // no extra option for OMIT
    // no extra option for SET

    //            <String, Float> purchaseCooldowns;// RESET, LOWEST, HIGHEST (default), +OMIT x, +SET y to X
    public static final int MERGE_PURCHASE_COOLDOWNS_RESET = 1;
    public static final int MERGE_PURCHASE_COOLDOWNS_LOWEST = 2;
    public static final int MERGE_PURCHASE_COOLDOWNS_HIGHEST = 3;
    // no extra option for OMIT
    // no extra option for SET

    int merge_option_name;

    int merge_option_points;
    int merge_option_points_set;

    int merge_option_income;
    int merge_option_income_set;

    int merge_option_incomeFraction;
    float merge_option_incomeFraction_set;

    int merge_option_totalPlayedTime;
    float merge_option_totalPlayedTime_set;

    int merge_option_timeFraction;
    float merge_option_timeFraction_set;

    int merge_option_eligibleForBonus;
    boolean merge_option_eligibleForBonus_set;

    int merge_option_bonusAmount;
    int merge_option_bonusAmount_set;

    int merge_option_notify;
    boolean merge_option_notify_set;

    int merge_option_customTags;
    String[] merge_option_customTags_ensure;
    String[] merge_option_customTags_omit;

    int merge_option_purchasedPIDs;
    String[] merge_option_purchasedPIDs_ensure;
    String[] merge_option_purchasedPIDs_omit;

    int merge_option_purchaseLimits;
    HashMap<String, Integer> merge_option_purchaseLimits_ensure;
    String[] merge_option_purchaseLimits_omit;

    int merge_option_purchaseCooldowns;
    HashMap<String, Float> merge_option_purchaseCooldowns_ensure;
    String[] merge_option_purchaseCooldowns_omit;

    @Override
    public String getName() {
        return "merge";
    }

    @Override
    public String getDescription() {
        return "Merges two or more files into a single newly created file.";
    }

    public String getUsage() {
        return "[target file] [source file] [source file] [... additional source files]";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 2) {
            try {
                SavegameTool.getCommandManager().dispatch(new String[] {"help", this.getName()});
            } catch (MissingCommandException ignore) {}
            return true;
        }

        try {
            this.parseArguments();
        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid argument '" + ex.getMessage() + "'");
        }

        long startUsedMemory = Runtime.getRuntime().totalMemory();
        long memoryUsage;
        PlayerDataSavegame targetSavegame = new PlayerDataSavegame();
        PlayerDataSavegame sourceSavegame;
        Path sourceSavegamePath;

        for (int i = 1; i < this.getParameters().length; i++) {
            sourceSavegamePath = Paths.get(this.getParameters()[i]).toAbsolutePath();
            System.out.println("Loading source file '" + sourceSavegamePath.getFileName().toString() + "'");
            sourceSavegame = new PlayerDataSavegame(sourceSavegamePath);

            memoryUsage = (Runtime.getRuntime().totalMemory() - startUsedMemory);
            if (memoryUsage < 0) {
                memoryUsage = 0;
            }
            System.out.println("Memory usage: " + String.format("%,d", memoryUsage) + " Bytes");
            System.out.println("Merging source file '" + sourceSavegamePath.getFileName().toString() + "'");

            
        }

        return true;
    }

    private void parseArguments() throws MissingArgumentException {
        this.merge_option_points = this.getOptionDefault(
                "points-stack",
                "points-reset",
                "points-set",
                "points-lowest",
                "points-highest"
        );
        this.merge_option_income = this.getOptionDefault(
                4,
                "income-reset",
                "income-set",
                "income-lowest",
                "income-highest"
        );
        this.merge_option_incomeFraction = this.getOptionDefault(
                4,
                "income-fraction-reset",
                "income-fraction-set",
                "income-fraction-lowest",
                "income-fraction-highest"
        );
        this.merge_option_totalPlayedTime = this.getOptionDefault(
                "total-played-time-stack",
                "total-played-time-reset",
                "total-played-time-set"
        );
        this.merge_option_timeFraction = this.getOptionDefault(
                4,
                "time-fraction-reset",
                "time-fraction-set",
                "time-fraction-lowest",
                "time-fraction-highest"
        );
        this.merge_option_eligibleForBonus = this.getOption(
                "eligible-for-bonus-set"
        );
        this.merge_option_bonusAmount = this.getOptionDefault(
                4,
                "bonus-amount-reset",
                "bonus-amount-set",
                "bonus-amount-lowest",
                "bonus-amount-highest"
        );
        this.merge_option_notify = this.getOption(
                "notify-set"
        );
        this.merge_option_customTags = this.getOption(
                "tags-reset",
                "tags-set"
        );
        this.merge_option_purchasedPIDs = this.getOption(
                "purchased-pids-reset",
                "purchased-pids-set"
        );
        this.merge_option_purchaseLimits = this.getOptionDefault(
                4,
                "purchase-limits-reset",
                "purchase-limits-set",
                "purchase-limits-lowest",
                "purchase-limits-highest"
        );
        this.merge_option_purchaseCooldowns = this.getOptionDefault(
                4,
                "purchase-cooldowns-reset",
                "purchase-cooldowns-set",
                "purchase-cooldowns-lowest",
                "purchase-cooldowns-highest"
        );

        if (this.merge_option_points == 3) {
            this.merge_option_points_set = this.getInteger("points-set");
        } else {
            this.merge_option_points_set = 0;
        }

        if (this.merge_option_income == 2) {
            this.merge_option_income_set = this.getInteger("income-set");
        } else {
            this.merge_option_income_set = 0;
        }

        if (this.merge_option_incomeFraction == 2) {
            this.merge_option_incomeFraction_set = this.getFloat("income-fraction-set");
        } else {
            this.merge_option_incomeFraction_set = 0;
        }

        if (this.merge_option_totalPlayedTime == 3) {
            this.merge_option_totalPlayedTime_set = this.getFloat("total-played-time-set");
        } else {
            this.merge_option_totalPlayedTime_set = 0;
        }

        if (this.merge_option_timeFraction == 2) {
            this.merge_option_timeFraction_set = this.getFloat("time-fraction-set");
        } else {
            this.merge_option_timeFraction_set = 0;
        }

        if (this.merge_option_eligibleForBonus == 1) {
            this.merge_option_eligibleForBonus_set = this.getBoolean("eligible-for-bonus-set");
        } else {
            this.merge_option_eligibleForBonus_set = false;
        }

        if (this.merge_option_bonusAmount == 2) {
            this.merge_option_bonusAmount_set = this.getInteger("bonus-amount-set");
        } else {
            this.merge_option_bonusAmount_set = 0;
        }

        if (this.merge_option_notify == 1) {
            this.merge_option_notify_set = this.getBoolean("notify-set");
        } else {
            this.merge_option_notify_set = true;
        }

        if (this.merge_option_customTags == 1) {
            this.merge_option_customTags_ensure = new String[0];
        } else {
            if (this.merge_option_customTags == 2) {
                this.merge_option_customTags_ensure = this.getArgument("tags-set").split(",");
            } else {
                this.merge_option_customTags_ensure = this.getArguments("ensure-tag");
            }

            if (this.merge_option_customTags == 0) {
                this.merge_option_customTags_omit = this.getArguments("omit-tag");
            } else {
                this.merge_option_customTags_omit = new String[0];
            }
        }

        if (this.merge_option_purchasedPIDs == 1) {
            this.merge_option_purchasedPIDs_ensure = new String[0];
        } else {
            if (this.merge_option_purchasedPIDs == 2) {
                this.merge_option_purchasedPIDs_ensure = this.getArgument("purchased-pids-set").split(",");
            } else {
                this.merge_option_purchasedPIDs_ensure = this.getArguments("ensure-purchased-pid");
            }

            if (this.merge_option_purchasedPIDs > 2) {
                this.merge_option_purchasedPIDs_omit = this.getArguments("omit-purchased-pid");
            } else {
                this.merge_option_purchasedPIDs_omit = new String[0];
            }
        }

        this.merge_option_purchaseLimits_ensure = new HashMap<>();
        if (this.merge_option_purchaseLimits > 1) {
            String[] purchaseLimits_ensure;
            String[] purchaseLimits_split;
            if (this.merge_option_purchaseLimits == 2) {
                purchaseLimits_ensure = this.getArgument("purchase-limits-set").split(",");
            } else {
                purchaseLimits_ensure = this.getArguments("ensure-purchase-limit");
            }

            for (String ensure : purchaseLimits_ensure) {
                purchaseLimits_split = ensure.split("=");
                if (purchaseLimits_split.length != 2) {
                    throw new IllegalArgumentException("*purchase-limit*");
                }
                try {
                    this.merge_option_purchaseLimits_ensure.put(purchaseLimits_split[0], Integer.parseInt(purchaseLimits_split[1]));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("*purchase-limit*");
                }
            }

            if (this.merge_option_purchaseLimits > 2) {
                this.merge_option_purchaseLimits_omit = this.getArguments("omit-purchase-limit");
            } else {
                this.merge_option_purchaseLimits_omit = new String[0];
            }
        }

        this.merge_option_purchaseCooldowns_ensure = new HashMap<>();
        if (this.merge_option_purchaseCooldowns > 1) {
            String[] purchaseCooldowns_ensure;
            String[] purchaseCooldowns_split;
            if (this.merge_option_purchaseCooldowns == 2) {
                purchaseCooldowns_ensure = this.getArgument("purchase-cooldowns-set").split(",");
            } else {
                purchaseCooldowns_ensure = this.getArguments("ensure-purchase-cooldown");
            }

            for (String ensure : purchaseCooldowns_ensure) {
                purchaseCooldowns_split = ensure.split("=");
                if (purchaseCooldowns_split.length != 2) {
                    throw new IllegalArgumentException("*purchase-cooldown*");
                }
                try {
                    this.merge_option_purchaseCooldowns_ensure.put(purchaseCooldowns_split[0], Float.parseFloat(purchaseCooldowns_split[1]));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("*purchase-cooldown*");
                }
            }

            if (this.merge_option_purchaseCooldowns > 2) {
                this.merge_option_purchaseCooldowns_omit = this.getArguments("omit-purchase-cooldown");
            } else {
                this.merge_option_purchaseCooldowns_omit = new String[0];
            }
        }

        if (this.merge_option_points > 2) {
            this.merge_option_points -= 1;
        }
        if (this.merge_option_income > 1) {
            this.merge_option_income -= 1;
        }
        if (this.merge_option_incomeFraction > 1) {
            this.merge_option_incomeFraction -= 1;
        }
        if (this.merge_option_timeFraction > 1) {
            this.merge_option_timeFraction -= 1;
        }
        if (this.merge_option_bonusAmount > 1) {
            this.merge_option_bonusAmount -= 1;
        }
        if (this.merge_option_customTags > 1) {
            this.merge_option_customTags -= 1;
        }
        if (this.merge_option_purchasedPIDs > 1) {
            this.merge_option_purchasedPIDs -= 1;
        }
        if (this.merge_option_purchaseLimits > 1) {
            this.merge_option_purchaseLimits -= 1;
        }
        if (this.merge_option_purchaseCooldowns > 1) {
            this.merge_option_purchaseCooldowns -= 1;
        }
    }

    public void merge(PlayerDataSavegame targetSavegame, PlayerDataSavegame sourceSavegame) {
        if (sourceSavegame.getPlayerVersion() > targetSavegame.getPlayerVersion()) {
            targetSavegame.setPlayerVersion(sourceSavegame.getPlayerVersion());
        }

        Player targetPlayer;
        for (Player sourcePlayer : sourceSavegame.getPlayers()) {
            targetPlayer = targetSavegame.getPlayer(sourcePlayer.getSteamID64());

//            int points; // STACK (default), RESET, SET TO x, LOWEST, HIGHEST,
//            int totalEarned; // NO OPTIONS
//            int income; // RESET, SET TO x, LOWEST, HIGHEST (default)
//            float incomeFraction; // RESET, SET TO x, LOWEST, HIGHEST (default)
//            float totalPlayedTime; // STACK (default), RESET, SET TO x
//            float timeFraction; // RESET, SET TO x, LOWEST, HIGHEST (default)
//            boolean eligibleForBonus; // SET TO x
//            int bonusAmount; // RESET, SET TO x, LOWEST, HIGHEST (default)
//            boolean notify; // SET TO x

//            <String> customTags; // RESET, SET to x, OMIT x
//            <String> purchasedPIDs; // RESET, SET to x, OMIT x
//            <String, Integer> purchaseLimits; // RESET, LOWEST, HIGHEST, +OMIT x, +SET y to X
//            <String, Float> purchaseCooldowns; // RESET, LOWEST, HIGHEST, +OMIT x, +SET y to X


        }
    }

}