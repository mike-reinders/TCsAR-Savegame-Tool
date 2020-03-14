package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;
import reinders.mike.TCsARSavegameTool.SavegameTool;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SavToJsonCommand extends Command {

    @Override
    public String getName() {
        return "sav-to-json";
    }

    @Override
    public String getUsage() {
        return "[--pretty] [source file] [target file]";
    }

    @Override
    public String getDescription() {
        return "Convert sav savegames to json format";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 2) {
            try {
                SavegameTool.getCommandManager().dispatch(new String[] {"help", this.getName()});
            } catch (MissingCommandException ignore) {}
            return true;
        }

        Path sourceSavegamePath = Paths.get(this.getParameters()[0]).toAbsolutePath();
        Path targetSavegamePath = Paths.get(this.getParameters()[1]).toAbsolutePath();
        boolean pretty = this.isArgument("pretty");

        System.out.println("Loading source file '" + sourceSavegamePath.getFileName() + "'");

        PlayerDataSavegame sourceSavegame;
        try {
            sourceSavegame = new PlayerDataSavegame(sourceSavegamePath);
        } catch (ModVersionMismatchException ex) {
            System.out.println("Invalid Savegame Mod-Version: Expected version to be '" + ex.getExpectedVersion() + "', got '" + ex.getActualVersion() + "'");
            return true;
        }

        System.out.print("Saving Target File '" + targetSavegamePath.getFileName() + "'");
        if (pretty) {
            System.out.print(" (pretty formatted)");
        }
        System.out.println();

        sourceSavegame.saveJson(targetSavegamePath, pretty);

        return true;
    }

}