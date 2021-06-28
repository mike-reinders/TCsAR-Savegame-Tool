package reinders.mike.TCsARSavegameTool.CommandTool;

import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Util.StringC;

public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String[] getAlias() {
        return new String[] {
                "-h",
                "--h",
                "--help",
                "/?",
                "/h",
                "/help"
        };
    }

    @Override
    public String getDescription() {
        return "Displays commands information";
    }

    public String getUsage() {
        return "[optional:command_name]";
    }

    @Override
    public boolean execute() {
        StringBuilder strBuilder = new StringBuilder();

        String queriedCommand = this.getParameters().length > 0? this.getParameters()[0].trim(): null;
        if (queriedCommand == null) {
            strBuilder.append("Commands:");

            String commandName;
            String description;
            for (Command command : this.getCommandManager().getAll()) {
                if (command.getName() != null) {
                    commandName = command.getName().trim();
                    description = command.getDescription()==null? "": command.getDescription().trim();

                    strBuilder.append(System.lineSeparator());
                    strBuilder.append("        ");
                    if (description.length() > 0) {
                        strBuilder.append(String.format("%-20s", commandName));
                        strBuilder.append(description);
                    } else {
                        strBuilder.append(commandName);
                    }
                }
            }
        } else {
            Command[] commands = this.getCommandManager().search(queriedCommand);

            if (commands.length > 0) {
                strBuilder.append("Commands:");

                String commandName;
                String description;
                String usage;
                for (Command command : commands) {
                    commandName = command.getName().trim();
                    description = command.getDescription()==null? "": command.getDescription().trim();
                    usage = command.getUsage()==null? "": command.getUsage().trim();

                    strBuilder.append(System.lineSeparator());
                    strBuilder.append("        ");
                    strBuilder.append(commandName);
                    strBuilder.append(":");
                    if (description.length() > 0) {
                        strBuilder.append(System.lineSeparator());
                        strBuilder.append(StringC.padLines(description, ' ', 12));
                    }
                    if (usage.length() > 0) {
                        strBuilder.append(System.lineSeparator());
                        strBuilder.append(StringC.padLines(usage, ' ', 12));
                    }
                }
            } else {
                strBuilder.append(System.lineSeparator());
                strBuilder.append("There is no command named '");
                strBuilder.append(queriedCommand);
                strBuilder.append("'!");
            }
        }

        System.out.print(strBuilder.toString());

        return true;
    }

}