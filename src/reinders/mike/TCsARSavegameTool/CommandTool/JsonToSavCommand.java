package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;
import reinders.mike.TCsARSavegameTool.SavegameTool;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonToSavCommand extends Command {

    @Override
    public String getName() {
        return "json-to-sav";
    }

    @Override
    public String getUsage() {
        return "[source file] [target file]";
    }

    @Override
    public String getDescription() {
        return "Convert json format to sav savegame";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 2) {
            SavegameTool.getCommandManager().dispatch(CommandManager.HELP_COMMAND, new String[] {this.getName()});
            return true;
        }

        Path sourceSavegamePath = Paths.get(this.getParameters()[0]).toAbsolutePath();
        Path targetSavegamePath = Paths.get(this.getParameters()[1]).toAbsolutePath();

        System.out.println("Loading source file '" + sourceSavegamePath.getFileName() + "'");
        PlayerDataSavegame sourceSavegame = new PlayerDataSavegame();

        try {
            sourceSavegame.loadJson(sourceSavegamePath);
        } catch (ModVersionMismatchException ex) {
            System.out.println("Invalid Savegame Mod-Version: Expected version to be '" + ex.getExpectedVersion() + "', got '" + ex.getActualVersion() + "'");
            return true;
        }

        System.out.println("Saving Target File '" + targetSavegamePath.getFileName() + "'");
        sourceSavegame.save(targetSavegamePath);

        return true;
    }

}