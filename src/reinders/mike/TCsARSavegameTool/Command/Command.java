package reinders.mike.TCsARSavegameTool.Command;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import reinders.mike.TCsARSavegameTool.Exception.MissingArgumentException;

import java.util.*;

public abstract class Command {

    private CommandManager commandManager;

    private String commandName;
    private HashMap<String, String[]> arguments;
    private String[] parameters;

    public abstract  @Nullable String getName();

    public @Nullable String[] getAlias() {
        return null;
    }

    public @Nullable String getDescription() {
        return null;
    }

    public final @NotNull String getDescriptionString() {
        String description =  this.getDescription();

        return description == null? "": description;
    }

    public @Nullable String getUsage() {
        return null;
    }

    public final @NotNull String getUsageString() {
        String usage =  this.getUsage();

        return usage == null? "": usage;
    }

    public abstract boolean execute() throws Throwable;

    final void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public final CommandManager getCommandManager() {
        return this.commandManager;
    }

    final void setCommandName(String name) {
        this.commandName = name;
    }

    public final String getCommandName() {
        return this.commandName;
    }

    final void setArguments(HashMap<String, String[]> arguments) {
        this.arguments = arguments;
    }

    public final Set<Map.Entry<String, String[]>> getArguments() {
        return this.arguments.entrySet();
    }

    final void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public final String[] getParameters() {
        return this.parameters;
    }

    public final String getArgument(@NotNull String ...names) {
        return this.getArgumentDefault("", names);
    }

    public final String getArgumentDefault(String defaultValue, @NotNull String ...names) {
        String[] arguments;

        if (names.length == 1) {
            arguments = this.getArguments(names[0]);
        } else {
            arguments = this.getArguments(names);
        }

        if (arguments.length > 0) {
            return arguments[arguments.length - 1];
        }

        return defaultValue;
    }

    public final String[] getArguments(@NotNull  String ...names) {
        List<String[]> foundItems = new ArrayList<>();

        if (names.length > 0) {
            for (Map.Entry<String, String[]> entry : this.getArguments()) {
                for (String name : names) {
                    if (entry.getKey().equals(name)) {
                        foundItems.add(entry.getValue());
                    }
                }
            }
        }

        if (foundItems.size() == 0) {
            return new String[0];
        } else if (foundItems.size() == 1) {
            return foundItems.get(0);
        } else {
            int totalLength = 0;
            for (String[] items : foundItems) {
                totalLength += items.length;
            }

            String[] finalItems = new String[totalLength];
            int index = 0;
            for (String[] items : foundItems) {
                System.arraycopy(items, 0, finalItems, index, items.length);
                index += items.length;
            }

            return finalItems;
        }
    }

    public final String[] requireArgument(@NotNull  String name) throws MissingArgumentException {
        String[] value = this.getArguments(name);

        if (value == null) {
            throw new MissingArgumentException("Missing argument '" + name + "'");
        }

        return value;
    }

    public final boolean isArgument(@NotNull String name) {
        for (Map.Entry<String, String[]> entry : this.getArguments()) {
            if (entry.getKey().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public final int getOption(@NotNull String ...names) {
        Boolean[] options = new Boolean[names.length];

        for (int i = 1; i < names.length; i++) {
            if (this.isArgument(names[i])) {
                return i;
            }
        }

        return 0;
    }

    public final int getOptionDefault(@NotNull String ...names) {
        return this.getOptionDefault(1, names);
    }

    public final int getOptionDefault(int defaultOption, @NotNull String ...names) {
        int option = this.getOption(names);

        if (option > 0) {
            return option;
        }

        return defaultOption;
    }



}