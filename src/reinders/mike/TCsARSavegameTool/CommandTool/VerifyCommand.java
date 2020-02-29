package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.SavegameTool;
import reinders.mike.TCsARSavegameTool.PlayerDataFile;
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
        return "[optional:--debug] [file] [... additional files]";
    }

    @Override
    public boolean execute() {
        for (String filePath : this.getParameters()) {
            Path path = Paths.get(filePath).toAbsolutePath();
            try {
                new PlayerDataFile(path);
                System.out.println("File '" + path.getFileName() + "' has been verified.");
            } catch (Throwable throwable) {
                System.out.println("Failed to verify file '" + path.getFileName() + "'");

                if (throwable instanceof IOException) {
                    System.out.println("Does the file exist? '" + path + "'");
                }

                if (this.isArgument("debug")) {
                    System.out.println(ThrowableC.toString(throwable));
                }
            }
        }

        return true;
    }

}