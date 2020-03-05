package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;

public class MergeCommand extends Command {

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

        return true;
    }

}