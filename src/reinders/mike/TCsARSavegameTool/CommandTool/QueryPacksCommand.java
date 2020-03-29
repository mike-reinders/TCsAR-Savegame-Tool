package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.*;
import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.Util.Pad;
import reinders.mike.TCsARSavegameTool.Util.StringC;
import reinders.mike.TCsARSavegameTool.Util.TimeC;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class QueryPacksCommand extends Command {

    private static final int PACK_DETAILS_LEFT_ROW_SIZE = 24;

    @Override
    public String getName() {
        return "query-packs";
    }

    @Override
    public String getUsage() {
        return "[--ignore-version / --legacy] [--packs / [--pack=PID]*] [file]";
    }

    @Override
    public String getDescription() {
        return "Displays detailed query informations about packs";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 1) {
            SavegameTool.getCommandManager().dispatch(CommandManager.HELP_COMMAND, new String[] {this.getName()});
            return true;
        }

        Path packDataFilePath = Paths.get(this.getParameters()[0]);
        PackDataSavegame packDataFile;
        System.out.println("Loading savegame '" + packDataFilePath.getFileName() + "'");

        try {
            if (this.isArgument("legacy")) {
                packDataFile = new PackDataSavegame();
                packDataFile.loadLegacy(packDataFilePath);
            } else {
                packDataFile = new PackDataSavegame(packDataFilePath, this.isArgument("ignore-version"));
            }
        } catch (ModVersionMismatchException ex) {
            System.out.println("Invalid Savegame Mod-Version: Expected version to be '" + ex.getExpectedVersion() + "', got '" + ex.getActualVersion() + "'");
            return true;
        }

        StringBuilder strBuilder = new StringBuilder();

        if (this.isArgument("packs")) {
            strBuilder.append(this.packsToStringList(packDataFile.getPacks()));
        } else if (this.isArgument("pack")) {
            String[] argumentValue;
            if ((argumentValue = this.getArguments("pack")) != null) {
                int i = 0;
                Pack pack;

                for (String pid : argumentValue) {
                    pack = packDataFile.getPack(pid);

                    if (i != 0) {
                        strBuilder.append(System.lineSeparator());
                    }

                    strBuilder.append(System.lineSeparator());
                    if (pack == null) {
                        strBuilder.append("Couldn't find a pack with the given PID '");
                        strBuilder.append(pid);
                        strBuilder.append("'");
                    } else {
                        strBuilder.append(this.packDetailsToString(pack));
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

    public String packDetailsToString(Pack pack) {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(StringC.pad(Pad.RIGHT, "Name:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getName());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "PID:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getPid());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Category:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getCategory());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Description:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getDescription());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Position:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getPosition());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Cost:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getCost());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Color (Hex):", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        if (pack.getColor() == null) {
            strBuilder.append("null");
        } else {
            strBuilder.append("#");
            strBuilder.append(String.format("%02x", Math.round(pack.getColor().R * 255)));
            strBuilder.append(String.format("%02x", Math.round(pack.getColor().G * 255)));
            strBuilder.append(String.format("%02x", Math.round(pack.getColor().B * 255)));
            strBuilder.append(" (");
            strBuilder.append(pack.getColor().A);
            strBuilder.append(")");
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Color (RGBA):", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        if (pack.getColor() == null) {
            strBuilder.append("null");
        } else {
            strBuilder.append(Math.round(pack.getColor().R * 255));
            strBuilder.append(" ");
            strBuilder.append(Math.round(pack.getColor().G * 255));
            strBuilder.append(" ");
            strBuilder.append(Math.round(pack.getColor().B * 255));
            strBuilder.append(" (");
            strBuilder.append(pack.getColor().A);
            strBuilder.append(")");
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Linear Color (RGBA):", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        if (pack.getColor() == null) {
            strBuilder.append("null");
        } else {
            strBuilder.append(pack.getColor().R);
            strBuilder.append("% ");
            strBuilder.append(pack.getColor().G);
            strBuilder.append("% ");
            strBuilder.append(pack.getColor().B);
            strBuilder.append("% ");
            strBuilder.append(pack.getColor().A);
            strBuilder.append("%");
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Pack Version:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getPackVersion());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Tags:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        int n = 0;
        for (String tag : pack.getTags()) {
            if (n++ > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(tag);
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Is Admin Only:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.isRequirementIsAdminOnly()? "true": "false");

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Is Prerequisite:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.isRequirementIsPrerequisite()? "true": "false");

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Prerequisite PID:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getRequirementPrerequisitePid());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Level Restriction:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getRequirementLevelRestriction());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Purchase Limit:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getRequirementPurchaseLimit());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Purchase Cooldown:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getRequirementPurchaseCooldown());

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Items:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getItems()==null? "null": (pack.getItems().size() + " Items"));

        strBuilder.append(System.lineSeparator());
        strBuilder.append(StringC.pad(Pad.RIGHT, "Dinos:", QueryPacksCommand.PACK_DETAILS_LEFT_ROW_SIZE));
        strBuilder.append(pack.getDinos()==null? "null": (pack.getDinos().size() + " Dinos"));

        return strBuilder.toString();
    }

    public String packsToStringList(Collection<Pack> packsCollection) {
        StringBuilder strBuilder = new StringBuilder();

        int indexPad = String.valueOf(packsCollection.size()).length();

        int i = 0;
        int iH = 0;
        for (Pack pack : packsCollection) {
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
                strBuilder.append(StringC.pad(Pad.RIGHT, "PID", ' ', 30));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Name", ' ', 30));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Category", ' ', 26));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Cost", ' ', 10));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Pack Version", ' ', 16));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Admin", ' ', 6));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Limit", ' ', 8));
                strBuilder.append(" | ");
                strBuilder.append(StringC.pad(Pad.RIGHT, "Cooldown", ' ', 12));
            }

            strBuilder.append(System.lineSeparator());
            strBuilder.append("  # ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(i), indexPad));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(pack.getPid()), 30));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, pack.getName(), 30));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(pack.getCategory()), 26));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(pack.getCost()), 10));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(pack.getPackVersion()), 16));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, (pack.isRequirementIsAdminOnly()? "true": ""), 6));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(pack.getRequirementPurchaseLimit()), 8));
            strBuilder.append("   ");
            strBuilder.append(StringC.pad(Pad.RIGHT, TimeC.TimeToString(pack.getRequirementPurchaseCooldown()), 12));

            // reset Header Entry Counter for every 20 entries
            if (iH == 20) {
                iH = 0;
            }
        }

        return strBuilder.toString();
    }

}