package reinders.mike.TCsARSavegameTool.Command;

import com.sun.istack.internal.NotNull;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandManager {

    private static Pattern ArgumentPattern = Pattern.compile("--(?:([^\\-][^=]*)(?:=([^=]*))?)?");

    private LinkedList<Command> commands = new LinkedList<>();

    public void register(@NotNull Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Given command must not be null");
        }

        if (command.getCommandManager() != null) {
            command.getCommandManager().unregister(command);
        }
        command.setCommandManager(this);

        this.commands.addFirst(command);
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

    public Command match(String name) {
        name = name.trim();

        for (Command command : this.commands) {
            if (command.getName() == null || command.getName().trim().equals(name)) {
                return command;
            }

            if (command.getAlias() != null) {
                for (String alias : command.getAlias()) {
                    if (alias.trim().equals(name)) {
                        return command;
                    }
                }
            }
        }

        return null;
    }

    public Command find(String name) {
        return this.find(name, true);
    }

    public Command find(String name, boolean exactMatch) {
        for (Command command : this.commands) {
            if (exactMatch? command.getName().equals(name): command.getName().contains(name)) {
                return command;
            }
        }

        return null;
    }

    public Command[] search(String name) {
        return this.search(name, true);
    }

    public Command[] search(String name, boolean exactMatches) {
        List<Command> returnCommands = new ArrayList<>();

        for (Command command : this.commands) {
            if (exactMatches? command.getName().equals(name): command.getName().contains(name)) {
                returnCommands.add(command);
            }
        }

        return returnCommands.toArray(new Command[0]);
    }

    public boolean dispatch(String[] args) throws Throwable {
        Command command = null;

        if (args.length > 0) {
            command = this.match(args[0].trim());
        }

        if (command == null) {
            return false;
        }

        this.dispatch(command, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    public void dispatch(@NotNull Command command, String[] args) throws Throwable {
        HashMap<String, List<String>> arguments = new HashMap<>();
        HashMap<String, String[]> argumentsArray = new HashMap<>();
        String[] parameters;

        // Parse Arguments
        int index = 0;
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
        for (Map.Entry<String, List<String>> arg : arguments.entrySet()) {
            argumentsArray.put(arg.getKey(), arg.getValue().toArray(new String[0]));
        }

        // Parse Parameters
        if (args.length > index) {
            parameters = Arrays.copyOfRange(args, index, args.length);
        } else {
            parameters = new String[0];
        }

        // Finally dispatch the command
        this.dispatch(command, argumentsArray, parameters);
    }

    public boolean dispatch(@NotNull String name) throws Throwable {
        return this.dispatch(name, null, null);
    }

    public boolean dispatch(@NotNull String name, HashMap<String, String[]> arguments) throws Throwable {
        return this.dispatch(name, arguments, null);
    }

    public boolean dispatch(@NotNull String name, HashMap<String, String[]> arguments, String[] parameters) throws Throwable {
        if (name == null) {
            return false;
        }

        Command command = this.match(name);
        if (command == null) {
            return false;
        }

        this.dispatch(command, arguments, parameters);
        return true;
    }

    public void dispatch(@NotNull Command command) throws Throwable {
        this.dispatch(command, null, null);
    }

    public void dispatch(@NotNull Command command, HashMap<String, String[]> arguments) throws Throwable {
        this.dispatch(command, arguments, null);
    }

    public void dispatch(@NotNull Command command, HashMap<String, String[]> arguments, String[] parameters) throws Throwable {
        if (command == null) {
            throw new MissingCommandException("A command must be given in order to execute it");
        }

        command.setCommandName(null);
        command.setArguments(arguments == null? new HashMap<>(): arguments);
        command.setParameters(parameters == null? new String[0]: parameters);
        try {
            command.execute();
        } finally {
            command.setCommandName(null);
            command.setArguments(null);
            command.setParameters(null);
        }
    }

}