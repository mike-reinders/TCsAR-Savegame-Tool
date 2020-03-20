package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.SavegameTool;
import reinders.mike.TCsARSavegameTool.Player;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;
import reinders.mike.TCsARSavegameTool.Util.Pad;
import reinders.mike.TCsARSavegameTool.Util.SteamIDC;
import reinders.mike.TCsARSavegameTool.Util.StringC;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class QueryCommand extends Command {

    private static final int PLAYER_DETAILS_LEFT_ROW_SIZE = 24;

    @Override
    public String getName() {
        return "query";
    }

    @Override
    public String getUsage() {
        return "[--ignore-version] [--players / [--player=steam64ID]*] [file]";
    }

    @Override
    public String getDescription() {
        return "Displays detailed query informations about players";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 1) {
            SavegameTool.getCommandManager().dispatch(CommandManager.HELP_COMMAND, new String[] {this.getName()});
            return true;
        }

        Path playerDataFilePath = Paths.get(this.getParameters()[0]);
        PlayerDataSavegame playerDataFile;
        System.out.println("Loading savegame '" + playerDataFilePath.getFileName() + "'");

        try {
            playerDataFile = new PlayerDataSavegame(playerDataFilePath, this.isArgument("ignore-version"));
        } catch (ModVersionMismatchException ex) {
            System.out.println("Invalid Savegame Mod-Version: Expected version to be '" + ex.getExpectedVersion() + "', got '" + ex.getActualVersion() + "'");
            return true;
        }

        StringBuilder strBuilder = new StringBuilder();

        if (this.isArgument("players")) {
            strBuilder.append(this.playersToStringList(playerDataFile.getPlayers()));
        } else if (this.isArgument("player")) {
            String[] argumentValue;
            if ((argumentValue = this.getArguments("player")) != null) {
                int i = 0;
                Player player;

                for (String steam64ID : argumentValue) {
                    player = playerDataFile.getPlayer(steam64ID);

                    if (i != 0) {
                        strBuilder.append(System.lineSeparator());
                    }

                    strBuilder.append(System.lineSeparator());
                    if (player == null) {
                        strBuilder.append("Couldn't find a player with the given Steam64ID '");
                        strBuilder.append(steam64ID);
                        strBuilder.append("'");
                    } else {
                        strBuilder.append(this.playerDetailsToString(player));
                    }
                    i++;
                }
            }
        } else {
            SavegameTool.getCommandManager().dispatch(CommandManager.HELP_COMMAND, new String[] {this.getName()});
        }

        System.out.print(strBuilder.toString());

        return true;
    }

    public String playerDetailsToString(Player player) {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(StringC.pad(Pad.RIGHT, "Name:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getName());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "SteamID64:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getSteamID64());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "SteamID3:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(SteamIDC.getSteamID3(player.getSteamID64()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "SteamID:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(SteamIDC.getSteamID(player.getSteamID64()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Profile:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append("https://steamcommunity.com/profiles/");
        strBuilder.append(player.getSteamID64());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Time Played:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getTotalPlayedTime()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "ARc:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getPoints());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "ARc spent:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        int spentPoints = (player.getTotalEarned() - player.getPoints());
        if (spentPoints >= 0) {
            strBuilder.append(spentPoints);
        } else {
            strBuilder.append("n/a");
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Total ARc:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getTotalEarned());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Tags:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(String.join(", ", player.getCustomTags()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Income:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getIncome());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Income Fraction:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getIncomeFraction()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Eligible for Bonus:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append((player.isEligibleForBonus()? "true": "false"));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Bonus:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getBonusAmount()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Bonus Fraction:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getBonusAmount()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Notify:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append((player.isNotify()? "true": "false"));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "PlayerVersion:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getPlayerVersion());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Tags:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        int n = 0;
        for (String tag : player.getCustomTags()) {
            if (n++ > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(tag);
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Purchased PIDs:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        int o = 0;
        for (String purchasedPID : player.getPurchasedPIDs()) {
            if (o++ > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(purchasedPID);
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Purchase Limits:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        int p = 0;
        for (Map.Entry<String, Integer> purchaseLimit : player.getPurchaseLimits().entrySet()) {
            if (p++ > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(purchaseLimit.getKey());
            strBuilder.append(":");
            strBuilder.append(purchaseLimit.getValue());
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Notify:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        int q = 0;
        for (Map.Entry<String, Float> purchaseCooldown : player.getPurchaseCooldowns().entrySet()) {
            if (q++ > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(purchaseCooldown.getKey());
            strBuilder.append(":");
            strBuilder.append(purchaseCooldown.getValue());
        }

        return strBuilder.toString();
    }

    public String playersToStringList(Collection<Player> playersCollection) {
        StringBuilder strBuilder = new StringBuilder();

        int indexPad = String.valueOf(playersCollection.size()).length();

        int i = 0;
        int iH = 0;
        for (Player player : playersCollection) {
            i++;
            iH++;

            // Add Headers every 20 entries
            if (iH == 1) {
                if (i != 1) {
                    strBuilder.append(System.lineSeparator());
                }
                strBuilder.append(System.lineSeparator());
                strBuilder.append("| ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Nr", ' ', indexPad + 2));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Steam64ID", ' ', 18));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Name", ' ', 20));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "ARc", ' ', 10));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Spent ARc", ' ', 10));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Total ARc", ' ', 10));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Player Version", ' ', 16));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Time-Played", ' ', 16));
            }

            strBuilder.append(System.lineSeparator());
            strBuilder.append("  # ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(i), indexPad));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getSteamID64()), 18));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, player.getName(), 20));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getPoints()), 10));

            strBuilder.append("   ");
            int spentPoints = (player.getTotalEarned() - player.getPoints());
            if (spentPoints >= 0) {
                strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(spentPoints), 10));
            } else {
                strBuilder.append(StringC.pad(Pad.RIGHT, "n/a", 10));
            }

            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getTotalEarned()), 10));

            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getPlayerVersion()), 16));

            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, this.timeToString(player.getTotalPlayedTime()), 16));

            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, "(" + player.getTotalPlayedTime() + "s)", 16));

            // reset Header Entry Counter for every 20 entries
            if (iH == 20) {
                iH = 0;
            }
        }

        return strBuilder.toString();
    }

    public String timeToString(float time) {
        StringBuilder strBuilder = new StringBuilder();

        int days = (int)(time / (60 * 60 * 24));
        int hours = (int)((time = (time - (days * (60 * 60 * 24)))) / (60 * 60));
        int minutes = (int)((time = (time - (hours * (60 * 60)))) / 60);
        int seconds = (int)(time = (time - (minutes * 60)));

        if (days > 0) {
            strBuilder.append(days);
            strBuilder.append("d");
        }
        if (hours > 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(hours);
            strBuilder.append("h");
        }
        if (minutes > 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(minutes);
            strBuilder.append("m");
        }
        if (seconds > 0 || strBuilder.length() == 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(seconds);
            strBuilder.append("s");
        }

        return strBuilder.toString();
    }

}