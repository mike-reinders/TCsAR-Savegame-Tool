package reinders.mike.TCsARSavegameTool.CommandTool;

public class CommandManager extends reinders.mike.TCsARSavegameTool.Command.CommandManager {

    public CommandManager() {
        this.register(new MergeCommand());
        this.register(new VerifyCommand());
        this.register(new QueryCommand());
        this.register(new DebugCommand());
        this.register(new HelpCommand());
    }

}