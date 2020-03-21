package reinders.mike.TCsARSavegameTool;

import reinders.mike.TCsARSavegameTool.CommandTool.CommandManager;
import reinders.mike.TCsARSavegameTool.Util.ThrowableC;

public final class SavegameTool {

    public static final String MERGE_TOOL_VERSION = "1.7.1";

    private static reinders.mike.TCsARSavegameTool.Command.CommandManager commandManager = new CommandManager();

    public static reinders.mike.TCsARSavegameTool.Command.CommandManager getCommandManager() {
        return SavegameTool.commandManager;
    }

    public static void main(String[] args) {
        try {
            System.out.println("TCsAR Savegame Tool v" + SavegameTool.MERGE_TOOL_VERSION);
            if (args.length == 0 || !SavegameTool.commandManager.dispatch(args)) {
                SavegameTool.commandManager.dispatch(CommandManager.HELP_COMMAND);
            }
        } catch (Throwable throwable) {
            System.out.println(ThrowableC.toString(throwable));
        }
    }

}