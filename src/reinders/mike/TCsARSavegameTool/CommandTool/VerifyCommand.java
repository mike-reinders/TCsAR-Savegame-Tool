package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;
import reinders.mike.TCsARSavegameTool.SavegameTool;
import reinders.mike.TCsARSavegameTool.Util.ThrowableC;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VerifyCommand extends Command {

    @Override
    public String getName() {
        return "verify";
    }

    @Override
    public String getDescription() {
        return "Verifies one or more files";
    }

    public String getUsage() {
        return "[optional:--debug] [--exact-version] [file] [... additional files]";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 1) {
            SavegameTool.getCommandManager().dispatch(CommandManager.HELP_COMMAND, new String[] {this.getName()});
            return true;
        }

        for (String filePath : this.getParameters()) {
            Path path = Paths.get(filePath).toAbsolutePath();
            try {
                new PlayerDataSavegame(path, !this.isArgument("exact-version"));
                System.out.println("File '" + path.getFileName() + "' has been verified.");
            } catch (Throwable throwable) {
                System.out.println("Failed to verify file '" + path.getFileName() + "'");

                if (throwable instanceof IOException) {
                    System.out.println("Does the file exist? '" + path + "'");
                } else if (throwable instanceof ModVersionMismatchException) {
                    System.out.println("The mod-version seems to not match, expected '" + ((ModVersionMismatchException) throwable).getExpectedVersion() + "', got '" + ((ModVersionMismatchException) throwable).getActualVersion() + "'");
                }

                if (this.isArgument("debug")) {
                    System.out.println(ThrowableC.toString(throwable));
                }
            }
        }

        return true;
    }

}