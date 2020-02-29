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
    public boolean execute() {
        /*int sourceFilesCount = (args.length - 1);
            if (sourceFilesCount <= 1) {
                Path curFilePath = Paths.get(MergeTool.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath();
                System.out.println("java -jar " + curFilePath.getFileName() + " [target file] [source file] [source file] [... additional source files]");
                return;
            }

            Path targetPath = Paths.get(args[0]).toAbsolutePath();
            System.out.println("Target File: " + targetPath.getFileName());

            Path sourcePath;
            ArkSavFile sourceFile;
            for (int i = 1; i <= sourceFilesCount; i++) {
                sourcePath = Paths.get(args[i]).toAbsolutePath();
                System.out.println("Processing source file #" + i + " " + sourcePath.getFileName());

                PropertyInt playerVersion = (PropertyInt)sourceFile.getProperty("PlayerVersion");
                System.out.println("PlayerVersion: " + playerVersion.getValue());

                ArkArray<?> playerData = ((PropertyArray)sourceFile.getProperty("PlayerData")).getValue();

                System.out.println(playerData.size() + " Players have been processed.");
            }
            System.out.println("Finished merging " + sourceFilesCount + " files.");*/

        return true;
    }

}