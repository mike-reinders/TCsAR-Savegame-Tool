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

    public final String getArgument(@NotNull String name) {
        return this.getArgument(name, "");
    }

    public final String getArgument(@NotNull String name, String defaults) {
        String[] arguments = this.getArguments(name);

        if (arguments.length > 0) {
            return arguments[arguments.length - 1];
        }

        return defaults;
    }

    public final String getArgument(@NotNull String ...names) {
        return this.getArgument("", names);
    }

    public final String getArgument(String defaults, @NotNull String ...names) {
        String[] arguments = this.getArguments(names);

        if (arguments.length > 0) {
            return arguments[arguments.length - 1];
        }

        return defaults;
    }

    public final String[] getArguments(@NotNull String name) {
        for (Map.Entry<String, String[]> entry : this.getArguments()) {
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }

        return new String[0];
    }

    public final String[] getArguments(@NotNull  String ...names) {
        List<String[]> found = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : this.getArguments()) {
            for (String name : names) {
                if (entry.getKey().equals(name)) {
                    found.add(entry.getValue());
                }
            }
        }

        int totalLength = 0;
        for (String[] items : found) {
            totalLength += items.length;
        }

        String[] finalItems = new String[totalLength];
        int index = 0;
        for (String[] items : found) {
            System.arraycopy(items, 0, finalItems, index, items.length);
            index += items.length;
        }

        return finalItems;
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

}