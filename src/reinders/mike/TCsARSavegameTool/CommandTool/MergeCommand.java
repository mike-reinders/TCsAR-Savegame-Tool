package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingArgumentException;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.Player;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;
import reinders.mike.TCsARSavegameTool.SavegameTool;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MergeCommand extends Command {

    public static final int MERGE_POINTS_ADD_ALL = 1;
    public static final int MERGE_POINTS_SET = 2;
    public static final int MERGE_POINTS_LOWEST = 3;
    public static final int MERGE_POINTS_HIGHEST = 4;

    public static final int MERGE_INCOME_SET = 1;
    public static final int MERGE_INCOME_LOWEST = 2;
    public static final int MERGE_INCOME_HIGHEST = 3;

    public static final int MERGE_INCOME_FRACTION_SET = 1;
    public static final int MERGE_INCOME_FRACTION_LOWEST = 2;
    public static final int MERGE_INCOME_FRACTION_HIGHEST = 3;

    public static final int MERGE_TOTAL_PLAYED_TIME_ADD_ALL = 1;
    public static final int MERGE_TOTAL_PLAYED_TIME_SET = 2;

    public static final int MERGE_TIME_FRACTION_SET = 1;
    public static final int MERGE_TIME_FRACTION_LOWEST = 2;
    public static final int MERGE_TIME_FRACTION_HIGHEST = 3;

    public static final int MERGE_ELIGIBLE_FOR_BONUS_SET = 1;

    public static final int MERGE_BONUS_AMOUNT_SET = 1;
    public static final int MERGE_BONUS_AMOUNT_LOWEST = 2;
    public static final int MERGE_BONUS_AMOUNT_HIGHEST = 3;

    public static final int MERGE_NOTIFY_SET = 1;

    public static final int MERGE_TAGS_PRESENCE = 0;
    public static final int MERGE_TAGS_SET = 1;

    public static final int MERGE_PURCHASED_PIDS_PRESENCE = 0;
    public static final int MERGE_PURCHASED_PIDS_SET = 1;

    public static final int MERGE_PURCHASE_LIMITS_SET = 1;
    public static final int MERGE_PURCHASE_LIMITS_LOWEST = 2;
    public static final int MERGE_PURCHASE_LIMITS_HIGHEST = 3;

    public static final int MERGE_PURCHASE_COOLDOWNS_SET = 1;
    public static final int MERGE_PURCHASE_COOLDOWNS_LOWEST = 2;
    public static final int MERGE_PURCHASE_COOLDOWNS_HIGHEST = 3;

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
        return "Merges one or more files into a single newly created file.";
    }

    @Override
    public String getUsage() {
        return "[...parameters] [target file] [source file] [source file] [... additional source files]" + System.lineSeparator()
                + System.lineSeparator()
                + "Parameters always start with -- (double hyphen) and are shown with [] (brackets) for documentation purpose only." + System.lineSeparator()
                + "A single parameter looks like this: --income-reset" + System.lineSeparator()
                + "The following parameters are available:" + System.lineSeparator()
                + "[--points-stack](default) | [--points-reset] | [--points-set=<value>] | [--points-lowest] | [--points-highest]" + System.lineSeparator()
                + "[[--income-reset] | [--income-set=<value>] | [--income-lowest] | [--income-highest](default)]" + System.lineSeparator()
                + "[[--income-fraction-reset] | [--income-fraction-set=<value>] | [--income-fraction-lowest] | [--income-fraction-highest](default)]" + System.lineSeparator()
                + "[[--total-played-time-stack](default) | [--total-played-time-reset] | [--total-played-time-set=<value>]]" + System.lineSeparator()
                + "[[--time-fraction-reset] | [--time-fraction-set=<value>] | [--time-fraction-lowest] | [--time-fraction-highest](default)]" + System.lineSeparator()
                + "[--eligible-for-bonus-set=<value>]" + System.lineSeparator()
                + "[[--bonus-amount-reset] | [--bonus-amount-set=<value>] | [--bonus-amount-lowest] | [--bonus-amount-highest](default)]" + System.lineSeparator()
                + "[--notify-set=<value>]" + System.lineSeparator()
                + "[[--tags-reset] | [--tags-set=<value>]]" + System.lineSeparator()
                + "[[--purchased-pids-reset] | [--purchased-pids-set=<value>]]" + System.lineSeparator()
                + "[[--purchase-limits-reset] | [--purchase-limits-set=<value>] | [--purchase-limits-lowest] | [--purchase-limits-highest](default)]" + System.lineSeparator()
                + "[[--purchase-cooldowns-reset] [--purchase-cooldowns-set=<value>] [--purchase-cooldowns-lowest] [--purchase-cooldowns-highest](default)]" + System.lineSeparator()
                + System.lineSeparator()
                + "To explain this a little. The following:" + System.lineSeparator()
                + "[[--tags-reset] | [--tags-set=<value>]]" + System.lineSeparator()
                + ".. shows that the parameters \"tags-reset\" and \"tags-set\" can be used." + System.lineSeparator()
                + "But only one of them because they exclude each other." + System.lineSeparator()
                + "The \"tags-set\" parameter also requires a value. Tags have to be delimetered by a ',' (comma)" + System.lineSeparator()
                + System.lineSeparator()
                + "Here are a few more parameters which not always work depending on what parameters have been provided already:" + System.lineSeparator()
                + "[--ensure-tag=<value>]* [--omit-tag=<value>]*" + System.lineSeparator()
                + "[--ensure-purchased-pid=<value>]* [--omit-purchased-pid=<value>]*" + System.lineSeparator()
                + "[--ensure-purchase-limit=<value>]* [--omit-purchase-limit=<value>]*" + System.lineSeparator()
                + "[--ensure-purchase-cooldown=<value>]* [--omit-purchase-cooldown=<value>]*" + System.lineSeparator()
                + System.lineSeparator()
                + "These parameters can be repeated indefinitely an require a value." + System.lineSeparator()
                + "To ensure a purchase limit or purchase cooldown the value has to be => '<key>=<value>' e.g. '--ensure-purchase-limit=<key>=<value>'";
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
            return true;
        }

        PlayerDataSavegame targetSavegame = new PlayerDataSavegame();
        PlayerDataSavegame sourceSavegame;
        Path sourceSavegamePath;

        for (int i = 1; i < this.getParameters().length; i++) {
            sourceSavegamePath = Paths.get(this.getParameters()[i]).toAbsolutePath();
            System.out.println("Merging source file '" + sourceSavegamePath.getFileName() + "'");

            try {
                sourceSavegame = new PlayerDataSavegame(sourceSavegamePath);
            } catch (ModVersionMismatchException ex) {
                System.out.println("Invalid Savegame Mod-Version: Expected version to be '" + ex.getExpectedVersion() + "', got '" + ex.getActualVersion() + "'");
                return true;
            }

            this.merge(targetSavegame, sourceSavegame);
        }

        System.out.println("Updating Players API Version once for " + targetSavegame.getPlayers().size() + " players");
        for (Player targetPlayer : targetSavegame.getPlayers()) {
            targetPlayer.setPlayerVersion(targetPlayer.getPlayerVersion() + 1);
        }

        Path targetFilePath = Paths.get(this.getParameters()[0]).toAbsolutePath();

        System.out.println("Saving Target File '" + targetFilePath.getFileName() + "'");
        targetSavegame.save(targetFilePath);

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

    public PlayerDataSavegame merge(PlayerDataSavegame targetSavegame, PlayerDataSavegame sourceSavegame) throws ModVersionMismatchException {
        if (sourceSavegame.getModVersion() != targetSavegame.getModVersion()) {
            throw new ModVersionMismatchException(targetSavegame.getModVersion(), sourceSavegame.getModVersion());
        }
        targetSavegame.setModVersion(sourceSavegame.getModVersion());

        System.out.println("Merging " + sourceSavegame.getPlayers().size() + " Players");

        Player targetPlayer;
        for (Player sourcePlayer : sourceSavegame.getPlayers()) {
            targetPlayer = targetSavegame.getPlayer(sourcePlayer.getSteamID64());

            if (targetPlayer == null) {
                targetPlayer = sourcePlayer.clone();
                targetSavegame.getPlayers().add(targetPlayer);

                System.out.println("Added Player " + targetPlayer.getSteamID64() + " (" + targetPlayer.getName() + ")");

                // Points
                if (this.merge_option_points == MergeCommand.MERGE_POINTS_SET) {
                    targetPlayer.setPoints(this.merge_option_points_set);
                }
                // Income
                if (this.merge_option_income == MergeCommand.MERGE_INCOME_SET) {
                    targetPlayer.setIncome(this.merge_option_income_set);
                }
                // Income Fraction
                if (this.merge_option_incomeFraction == MergeCommand.MERGE_INCOME_FRACTION_SET) {
                    targetPlayer.setIncomeFraction(this.merge_option_incomeFraction_set);
                }
                // Total Played Time
                if (this.merge_option_totalPlayedTime == MergeCommand.MERGE_TOTAL_PLAYED_TIME_SET) {
                    targetPlayer.setTotalPlayedTime(this.merge_option_totalPlayedTime_set);
                }
                // Time Fraction
                if (this.merge_option_timeFraction == MergeCommand.MERGE_TIME_FRACTION_SET) {
                    targetPlayer.setTimeFraction(this.merge_option_timeFraction_set);
                }

                // Eligible For Bonus
                if (this.merge_option_eligibleForBonus == MergeCommand.MERGE_ELIGIBLE_FOR_BONUS_SET) {
                    targetPlayer.setEligibleForBonus(this.merge_option_eligibleForBonus_set);
                }

                // Bonus Amount
                if (this.merge_option_bonusAmount == MergeCommand.MERGE_BONUS_AMOUNT_SET) {
                    targetPlayer.setBonusAmount(this.merge_option_bonusAmount_set);
                }

                // Notify
                if (this.merge_option_notify == MergeCommand.MERGE_NOTIFY_SET) {
                    targetPlayer.setNotify(this.merge_option_notify_set);
                }

                // Tags
                if (this.merge_option_customTags == MergeCommand.MERGE_TAGS_SET) {
                    targetPlayer.getCustomTags().clear();
                    targetPlayer.getCustomTags().addAll(Arrays.asList(this.merge_option_customTags_ensure));
                }

                // Purchased PIDs
                if (this.merge_option_purchasedPIDs == MergeCommand.MERGE_PURCHASED_PIDS_SET) {
                    targetPlayer.getPurchasedPIDs().clear();
                    targetPlayer.getPurchasedPIDs().addAll(Arrays.asList(this.merge_option_purchasedPIDs_ensure));
                }

                // Purchase Limits
                if (this.merge_option_purchaseLimits == MergeCommand.MERGE_PURCHASE_LIMITS_SET) {
                    targetPlayer.getPurchaseLimits().clear();
                    targetPlayer.getPurchaseLimits().putAll(this.merge_option_purchaseLimits_ensure);
                }

                // Purchase Cooldowns
                if (this.merge_option_purchaseCooldowns == MergeCommand.MERGE_PURCHASE_COOLDOWNS_SET) {
                    targetPlayer.getPurchaseCooldowns().clear();
                    targetPlayer.getPurchaseCooldowns().putAll(this.merge_option_purchaseCooldowns_ensure);
                }
            } else {
                System.out.println("Merging Player " + targetPlayer.getSteamID64() + " (" + targetPlayer.getName() + ")");

                // Points
                if (this.merge_option_points == MergeCommand.MERGE_POINTS_ADD_ALL) {
                    targetPlayer.setPoints(targetPlayer.getPoints() + sourcePlayer.getPoints());
                } else if (this.merge_option_points == MergeCommand.MERGE_POINTS_LOWEST) {
                    if (targetPlayer.getPoints() > sourcePlayer.getPoints()) {
                        targetPlayer.setPoints(sourcePlayer.getPoints());
                    }
                } else if (this.merge_option_points == MergeCommand.MERGE_POINTS_HIGHEST) {
                    if (targetPlayer.getPoints() < sourcePlayer.getPoints()) {
                        targetPlayer.setPoints(sourcePlayer.getPoints());
                    }
                }

                // Total Earned
                targetPlayer.setTotalEarned(targetPlayer.getTotalEarned() + sourcePlayer.getTotalEarned());

                // Income
                if (this.merge_option_income == MergeCommand.MERGE_INCOME_LOWEST) {
                    if (targetPlayer.getIncome() > sourcePlayer.getIncome()) {
                        targetPlayer.setIncome(sourcePlayer.getIncome());
                    }
                } else if (this.merge_option_income == MergeCommand.MERGE_INCOME_HIGHEST) {
                    if (targetPlayer.getIncome() < sourcePlayer.getIncome()) {
                        targetPlayer.setIncome(sourcePlayer.getIncome());
                    }
                }

                // Income Fraction
                if (this.merge_option_incomeFraction == MergeCommand.MERGE_INCOME_FRACTION_LOWEST) {
                    if (targetPlayer.getIncomeFraction() > sourcePlayer.getIncomeFraction()) {
                        targetPlayer.setIncomeFraction(sourcePlayer.getIncomeFraction());
                    }
                } else if (this.merge_option_incomeFraction == MergeCommand.MERGE_INCOME_FRACTION_HIGHEST) {
                    if (targetPlayer.getIncomeFraction() < sourcePlayer.getIncomeFraction()) {
                        targetPlayer.setIncomeFraction(sourcePlayer.getIncomeFraction());
                    }
                }

                // Total Played Time
                if (this.merge_option_totalPlayedTime == MergeCommand.MERGE_TOTAL_PLAYED_TIME_ADD_ALL) {
                    targetPlayer.setTotalPlayedTime(targetPlayer.getTotalPlayedTime() + sourcePlayer.getTotalPlayedTime());
                }

                // Time Fraction
                if (this.merge_option_timeFraction == MergeCommand.MERGE_TIME_FRACTION_LOWEST) {
                    if (targetPlayer.getTimeFraction() > sourcePlayer.getTimeFraction()) {
                        targetPlayer.setTimeFraction(sourcePlayer.getTimeFraction());
                    }
                } else if (this.merge_option_timeFraction == MergeCommand.MERGE_TIME_FRACTION_HIGHEST) {
                    if (targetPlayer.getTimeFraction() < sourcePlayer.getTimeFraction()) {
                        targetPlayer.setTimeFraction(sourcePlayer.getTimeFraction());
                    }
                }

                // Bonus Amount
                if (this.merge_option_bonusAmount == MergeCommand.MERGE_BONUS_AMOUNT_LOWEST) {
                    if (targetPlayer.getBonusAmount() > sourcePlayer.getBonusAmount()) {
                        targetPlayer.setBonusAmount(sourcePlayer.getBonusAmount());
                    }
                } else if (this.merge_option_bonusAmount == MergeCommand.MERGE_BONUS_AMOUNT_HIGHEST) {
                    if (targetPlayer.getBonusAmount() < sourcePlayer.getBonusAmount()) {
                        targetPlayer.setBonusAmount(sourcePlayer.getBonusAmount());
                    }
                }

                // Player Version
                if (targetPlayer.getPlayerVersion() < sourcePlayer.getPlayerVersion()) {
                    targetPlayer.setPlayerVersion(sourcePlayer.getPlayerVersion());
                }

                // Tags
                if (this.merge_option_customTags == MergeCommand.MERGE_TAGS_PRESENCE) {
                    for (String ensuredTag : sourcePlayer.getCustomTags()) {
                        if (!targetPlayer.getCustomTags().contains(ensuredTag)) {
                            targetPlayer.getCustomTags().add(ensuredTag);
                        }
                    }
                }

                // Purchased PIDs
                if (this.merge_option_purchasedPIDs == MergeCommand.MERGE_PURCHASED_PIDS_PRESENCE) {
                    for (String ensuredPID : sourcePlayer.getPurchasedPIDs()) {
                        if (!targetPlayer.getPurchasedPIDs().contains(ensuredPID)) {
                            targetPlayer.getPurchasedPIDs().add(ensuredPID);
                        }
                    }
                }

                // Purchase Limits
                if (this.merge_option_purchaseLimits == MergeCommand.MERGE_PURCHASE_LIMITS_LOWEST) {
                    Integer targetValue;
                    for (Map.Entry<String, Integer> sourceLimit : sourcePlayer.getPurchaseLimits().entrySet()) {
                        targetValue = targetPlayer.getPurchaseLimits().getOrDefault(sourceLimit.getKey(), null);

                        if (targetValue == null || targetValue > sourceLimit.getValue()) {
                            targetPlayer.getPurchaseLimits().put(sourceLimit.getKey(), sourceLimit.getValue());
                        }
                    }
                } else if (this.merge_option_purchaseLimits == MergeCommand.MERGE_PURCHASE_LIMITS_HIGHEST) {
                    Integer targetValue;
                    for (Map.Entry<String, Integer> sourceLimit : sourcePlayer.getPurchaseLimits().entrySet()) {
                        targetValue = targetPlayer.getPurchaseLimits().getOrDefault(sourceLimit.getKey(), null);

                        if (targetValue == null || targetValue < sourceLimit.getValue()) {
                            targetPlayer.getPurchaseLimits().put(sourceLimit.getKey(), sourceLimit.getValue());
                        }
                    }
                }

                // Purchase Cooldowns
                if (this.merge_option_purchaseCooldowns == MergeCommand.MERGE_PURCHASE_COOLDOWNS_LOWEST) {
                    Float targetValue;
                    for (Map.Entry<String, Float> sourceCooldown : sourcePlayer.getPurchaseCooldowns().entrySet()) {
                        targetValue = targetPlayer.getPurchaseCooldowns().getOrDefault(sourceCooldown.getKey(), null);

                        if (targetValue == null || targetValue > sourceCooldown.getValue()) {
                            targetPlayer.getPurchaseCooldowns().put(sourceCooldown.getKey(), sourceCooldown.getValue());
                        }
                    }
                } else if (this.merge_option_purchaseCooldowns == MergeCommand.MERGE_PURCHASE_COOLDOWNS_HIGHEST) {
                    Float targetValue;
                    for (Map.Entry<String, Float> sourceCooldown : sourcePlayer.getPurchaseCooldowns().entrySet()) {
                        targetValue = targetPlayer.getPurchaseCooldowns().getOrDefault(sourceCooldown.getKey(), null);

                        if (targetValue == null || targetValue < sourceCooldown.getValue()) {
                            targetPlayer.getPurchaseCooldowns().put(sourceCooldown.getKey(), sourceCooldown.getValue());
                        }
                    }
                }
            }

            // Tags
            if (this.merge_option_customTags == MergeCommand.MERGE_TAGS_PRESENCE) {
                if (this.merge_option_customTags_omit.length > 0) {
                    targetPlayer.getCustomTags().removeAll(Arrays.asList(this.merge_option_customTags_omit));
                }
                if (this.merge_option_customTags_ensure.length > 0) {
                    for (String ensuredTag : this.merge_option_customTags_ensure) {
                        if (!targetPlayer.getCustomTags().contains(ensuredTag)) {
                            targetPlayer.getCustomTags().add(ensuredTag);
                        }
                    }
                }
            }

            // Purchased PIDs
            if (this.merge_option_purchasedPIDs == MergeCommand.MERGE_PURCHASED_PIDS_PRESENCE) {
                if (this.merge_option_purchasedPIDs_omit.length > 0) {
                    targetPlayer.getPurchasedPIDs().removeAll(Arrays.asList(this.merge_option_purchasedPIDs_omit));
                }
                if (this.merge_option_purchasedPIDs_ensure.length > 0) {
                    for (String ensuredPID : this.merge_option_purchasedPIDs_ensure) {
                        if (!targetPlayer.getPurchasedPIDs().contains(ensuredPID)) {
                            targetPlayer.getPurchasedPIDs().add(ensuredPID);
                        }
                    }
                }
            }

            // Purchase Limits
            if (this.merge_option_purchaseLimits > MergeCommand.MERGE_PURCHASE_LIMITS_SET) {
                if (this.merge_option_purchaseLimits_omit.length > 0) {
                    targetPlayer.getPurchaseLimits().keySet().removeAll(Arrays.asList(this.merge_option_purchaseLimits_omit));
                }
                if (this.merge_option_purchaseLimits_ensure.size() > 0) {
                    for (Map.Entry<String, Integer> ensureLimit : this.merge_option_purchaseLimits_ensure.entrySet()) {
                        targetPlayer.getPurchaseLimits().put(ensureLimit.getKey(), ensureLimit.getValue());
                    }
                }
            }

            // Purchase Cooldowns
            if (this.merge_option_purchaseCooldowns > MergeCommand.MERGE_PURCHASE_COOLDOWNS_SET) {
                if (this.merge_option_purchaseCooldowns_omit.length > 0) {
                    targetPlayer.getPurchaseCooldowns().keySet().removeAll(Arrays.asList(this.merge_option_purchaseCooldowns_omit));
                }
                if (this.merge_option_purchaseCooldowns_ensure.size() > 0) {
                    for (Map.Entry<String, Float> ensureCooldown : this.merge_option_purchaseCooldowns_ensure.entrySet()) {
                        targetPlayer.getPurchaseCooldowns().put(ensureCooldown.getKey(), ensureCooldown.getValue());
                    }
                }
            }
        }

        return targetSavegame;
    }

}