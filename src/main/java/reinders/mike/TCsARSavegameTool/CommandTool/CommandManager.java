package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;

public class CommandManager extends reinders.mike.TCsARSavegameTool.Command.CommandManager {

    public static final Command MERGE_COMMAND = new MergeCommand();
    public static final Command VERIFY_COMMAND = new VerifyCommand();
    public static final Command QUERY_COMMAND = new QueryCommand();
    public static final Command QUERY_PACKS_COMMAND = new QueryPacksCommand();
    public static final Command SAV_TO_JSON_COMMAND = new SavToJsonCommand();
    public static final Command JSON_TO_SAV_COMMAND = new JsonToSavCommand();
    public static final Command DEBUG_COMMAND = new DebugCommand();
    public static final Command HELP_COMMAND = new HelpCommand();
    public static final Command LEGACY_PACKS_TO_SAV_COMMAND = new LegacyPacksToSavCommand();

    public CommandManager() {
        this.register(CommandManager.MERGE_COMMAND);
        this.register(CommandManager.VERIFY_COMMAND);
        this.register(CommandManager.QUERY_COMMAND);
        this.register(CommandManager.QUERY_PACKS_COMMAND);
        this.register(CommandManager.SAV_TO_JSON_COMMAND);
        this.register(CommandManager.JSON_TO_SAV_COMMAND);
        this.register(CommandManager.LEGACY_PACKS_TO_SAV_COMMAND);
        this.register(CommandManager.DEBUG_COMMAND);
        this.register(CommandManager.HELP_COMMAND);
    }

}