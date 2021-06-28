package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.PackDataSavegame;
import reinders.mike.TCsARSavegameTool.PlayerDataSavegame;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LegacyPacksToSavCommand extends Command {

    @Override
    public String getName() {
        return "legacy-packs-to-sav";
    }

    @Override
    public String getUsage() {
        return "[legacy: source file] [target file]";
    }

    @Override
    public String getDescription() {
        return "Convert legacy packs-format to sav packs-savegame";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 2) {
            this.getCommandManager().dispatch(CommandManager.HELP_COMMAND, new String[] {this.getName()});
            return true;
        }

        Path sourceSavegamePath = Paths.get(this.getParameters()[0]).toAbsolutePath();
        Path targetSavegamePath = Paths.get(this.getParameters()[1]).toAbsolutePath();

        System.out.println("Loading source file '" + sourceSavegamePath.getFileName() + "'");
        PackDataSavegame sourcePackSavegame = new PackDataSavegame();
        sourcePackSavegame.loadLegacy(sourceSavegamePath);

        System.out.println("Saving Target File '" + targetSavegamePath.getFileName() + "'");
        sourcePackSavegame.save(targetSavegamePath);

        return true;
    }

}