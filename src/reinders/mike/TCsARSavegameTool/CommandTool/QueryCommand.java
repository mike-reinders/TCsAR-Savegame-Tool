package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.SavegameTool;
import reinders.mike.TCsARSavegameTool.Player;
import reinders.mike.TCsARSavegameTool.PlayerDataFile;
import reinders.mike.TCsARSavegameTool.Util.Pad;
import reinders.mike.TCsARSavegameTool.Util.SteamIDC;
import reinders.mike.TCsARSavegameTool.Util.StringC;

import java.nio.file.Paths;
import java.util.Collection;

public class QueryCommand extends Command {

    private static final int PLAYER_DETAILS_LEFT_ROW_SIZE = 16;

    @Override
    public String getName() {
        return "query";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 1) {
            try {
                SavegameTool.getCommandManager().dispatch(new String[] {"help", this.getName()});
            } catch (MissingCommandException ignore) {}
            return true;
        }

        PlayerDataFile playerDataFile = new PlayerDataFile(Paths.get(this.getParameters()[0]));

        StringBuilder strBuilder = new StringBuilder();

        if (this.isArgument("players")) {
            strBuilder.append(this.playersToStringList(playerDataFile.getPlayers()));
        } else if (this.isArgument("player")) {
            String[] argumentValue;
            if ((argumentValue = this.getArgument("player")) != null) {
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
            SavegameTool.getCommandManager().dispatch(new String[] {"help", this.getName()});
        }

        System.out.print(strBuilder.toString());

        return true;
    }

    public String playerDetailsToString(Player player) {
        StringBuilder strBuilder = new StringBuilder();
        //return player.getSteamID64() + " " + player.getName() + " " + this.playTimeToString(player.getTotalPlayedTime());

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
        strBuilder.append(StringC.pad(Pad.RIGHT, "Points:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getPoints());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Points spent:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        int spentPoints = (player.getTotalEarned() - player.getPoints());
        if (spentPoints >= 0) {
            strBuilder.append(spentPoints);
        } else {
            strBuilder.append("n/a");
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Total Points:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getTotalEarned());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Tags:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(String.join(", ", player.getCustomTags()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Income:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(player.getIncome());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Income Count:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getIncomeFraction()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Bonus:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getBonusAmount()));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Bonus Count:", QueryCommand.PLAYER_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(this.timeToString(player.getBonusAmount()));

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
                strBuilder.append(" --");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Nr", '-', indexPad));
                strBuilder.append("--");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Steam64ID", '-', 18));
                strBuilder.append("--");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Name", '-', 20));
                strBuilder.append("--");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Points", '-', 10));
                strBuilder.append("--");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Spent", '-', 10));
                strBuilder.append("--");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Total", '-', 10));
                strBuilder.append("--");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Time-Played", '-', 16));
                strBuilder.append("--");
                strBuilder.append(StringC.fill('-', 16));
            }

            strBuilder.append(System.lineSeparator());
            strBuilder.append(" # ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(i), indexPad));
            strBuilder.append("  ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getSteamID64()), 18));
            strBuilder.append("  ");
            strBuilder.append(StringC.pad(Pad.RIGHT, player.getName(), 20));
            strBuilder.append("  ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getPoints()), 10));

            strBuilder.append("  ");
            int spentPoints = (player.getTotalEarned() - player.getPoints());
            if (spentPoints >= 0) {
                strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(spentPoints), 10));
            } else {
                strBuilder.append(StringC.pad(Pad.RIGHT, "n/a", 10));
            }

            strBuilder.append("  ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(player.getTotalEarned()), 10));

            strBuilder.append("  ");
            strBuilder.append(StringC.pad(Pad.RIGHT, this.timeToString(player.getTotalPlayedTime()), 16));

            strBuilder.append("  ");
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
        if (seconds > 0) {
            if (strBuilder.length() != 0) {
                strBuilder.append(" ");
            }
            strBuilder.append(seconds);
            strBuilder.append("s");
        }

        return strBuilder.toString();
    }

}