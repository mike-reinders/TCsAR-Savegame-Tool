package reinders.mike.TCsARSavegameTool.Command;

import com.sun.istack.internal.NotNull;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandManager {

    private static Pattern ArgumentPattern = Pattern.compile("--(?:([^\\-][^=]*)(?:=([^=]*))?)?");

    private List<Command> commands = new ArrayList<>();

    public void register(@NotNull Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Given command must not be null");
        }

        if (command.getCommandManager() != null) {
            command.getCommandManager().unregister(command);
        }
        command.setCommandManager(this);

        this.commands.add(command);
    }

    public boolean unregister(@NotNull Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Given command must not be null");
        }

        command.setCommandManager(null);
        return this.commands.remove(command);
    }

    public Command[] getAll() {
        return this.commands.toArray(new Command[0]);
    }

    public Command[] find(String name) {
        return this.find(name, true);
    }

    public Command[] find(String name, boolean exactMatches) {
        List<Command> returnCommands = new ArrayList<>();
        for (Command command : this.commands) {
            if (command.getName() == name || (exactMatches? command.getName().equals(name): command.getName().contains(name))) {
                returnCommands.add(command);
            }
        }

        return returnCommands.toArray(new Command[0]);
    }

    public boolean dispatch(String[] args) throws Throwable {
        String commandName = args.length > 0? args[0].trim(): null;
        if (commandName == null || commandName.length() == 0) {
            throw new MissingCommandException("A command must be given in order to execute it");
        }

        // Parse Arguments
        HashMap<String, List<String>> arguments = new HashMap<>();
        int index = 1;
        while (index < args.length) {
            Matcher matcher = CommandManager.ArgumentPattern.matcher(args[index].trim());

            if (!matcher.matches()) {
                // Step to next procedure => Parameter-Parsing
                break;
            }

            // Increase index for the next loop
            index++;

            if (matcher.groupCount() == 1) {
                // Skip Argument Parsing because argument "--" is given
                break;
            }

            // Parse Argument and save it
            String key = matcher.group(1) == null? null: matcher.group(1).trim();
            String value = matcher.group(2) == null? null: matcher.group(2).trim();
            List<String> valueList = arguments.get(key);

            if (valueList == null) {
                valueList = new ArrayList<>();
            }

            valueList.add(value);
            arguments.put(key, valueList);
        }

        // Finalize Arguments
        HashMap<String, String[]> argumentsArray = new HashMap<>();

        for (Map.Entry<String, List<String>> arg : arguments.entrySet()) {
            argumentsArray.put(arg.getKey(), arg.getValue().toArray(new String[0]));
        }

        // Parse Parameters
        String[] parameters = Arrays.copyOfRange(args, index, args.length);

        // Finally dispatch the command
        return this.dispatch(commandName, argumentsArray, parameters);
    }

    public boolean dispatch(@NotNull String name, @NotNull HashMap<String, String[]> arguments, @NotNull String[] parameters) throws Throwable {
        if (name == null || (name = name.trim()).length() == 0) {
            throw new MissingCommandException("A command must be given in order to execute it");
        }

        Command command;
        for (int i = (this.commands.size() - 1); i >= 0; i--) {
            command = this.commands.get(i);
            if (this.matchesCommandName(command, name)) {
                command.setCommandName(name);
                command.setArguments(arguments);
                command.setParameters(parameters);
                try {
                    if (command.execute()) {
                        return true;
                    }
                } finally {
                    command.setCommandName(null);
                    command.setArguments(null);
                    command.setParameters(null);
                }
            }
        }

        return false;
    }

    private boolean matchesCommandName(@NotNull Command command, String name) {
        if (command.getName() == null || command.getName().trim().equals(name)) {
            return true;
        }

        if (command.getAlias() != null) {
            for (String alias : command.getAlias()) {
                if (alias.trim().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

}