package reinders.mike.TCsARSavegameTool.CommandTool;

import qowyn.ark.ArkSavFile;
import qowyn.ark.PropertyContainer;
import qowyn.ark.properties.Property;
import qowyn.ark.properties.PropertyArray;
import qowyn.ark.properties.PropertyStruct;
import qowyn.ark.structs.StructPropertyList;
import reinders.mike.TCsARSavegameTool.Command.Command;
import reinders.mike.TCsARSavegameTool.Exception.MissingCommandException;
import reinders.mike.TCsARSavegameTool.Exception.SaveGameException;
import reinders.mike.TCsARSavegameTool.SavegameTool;
import reinders.mike.TCsARSavegameTool.Util.ObjectA;
import reinders.mike.TCsARSavegameTool.Util.Pad;
import reinders.mike.TCsARSavegameTool.Util.StringC;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DebugCommand extends Command {

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getUsage() {
        return "[file] [optional:lookup path]";
    }

    @Override
    public String getDescription() {
        return "Displays detailed savegame structure independent informations about a given lookup path";
    }

    @Override
    public boolean execute() throws Throwable {
        if (this.getParameters().length < 1) {
            try {
                SavegameTool.getCommandManager().dispatch(new String[] {"help", this.getName()});
            } catch (MissingCommandException ignore) {}
            return true;
        }

        Object element;
        try {
            element = new ArkSavFile(Paths.get(this.getParameters()[0]));
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to load SaveGame", throwable);
        }

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("Class: ");
        strBuilder.append((String)ObjectA.getPrivateField(element, "className"));

        if (this.getParameters().length >= 2) {
            String[] parts = this.getParameters()[1].split("\\.");
            for (String part : parts) {
                if (element instanceof ArkSavFile) {
                    element = ((ArkSavFile) element).getProperty(part);
                } else if (element instanceof StructPropertyList) {
                    element = ((StructPropertyList) element).getProperty(part);
                } else if (element instanceof PropertyArray) {
                    try {
                        element = ((PropertyArray) element).getValue().get(Integer.parseInt(part));
                    } catch (NumberFormatException ignored) {
                        element = null;
                    }
                }

                if (element == null) {
                    strBuilder.append(System.lineSeparator());
                    strBuilder.append("Failed to lookup Property: Property doesn't exist.");
                    strBuilder.append(System.lineSeparator());
                    System.out.println(strBuilder.toString());
                    return true;
                }
            }
        }

        strBuilder.append(System.lineSeparator());
        strBuilder.append("Property Type:  ");

        if (element instanceof PropertyContainer) {
            PropertyContainer propertyContainer = (PropertyContainer)element;

            strBuilder.append("PropertyContainer");
            strBuilder.append(System.lineSeparator());
            strBuilder.append("Properties:");

            for (Property<?> property : propertyContainer.getProperties()) {
                strBuilder.append(System.lineSeparator());
                strBuilder.append("  ");
                strBuilder.append(StringC.pad(Pad.RIGHT, property.getTypeString(), 22));
                strBuilder.append(StringC.pad(Pad.RIGHT, property.getNameString(), 38));
                strBuilder.append("class: ");
                strBuilder.append(property.getClass().getCanonicalName());
            }
        } else if (element instanceof PropertyArray) {
            PropertyArray propertyArray = (PropertyArray)element;

            strBuilder.append(propertyArray.getType());
            strBuilder.append("<");
            strBuilder.append(propertyArray.getValue().getType());
            strBuilder.append(">");
            strBuilder.append(System.lineSeparator());

            if (propertyArray.getValue().getType().equals(PropertyStruct.TYPE)) {
                HashMap<String, Integer> properties = new HashMap<>();

                // Collect property names and their frequency
                StructPropertyList structPropertyList;
                int propertyCount;
                // Loop through all StructPropertyLists
                for (Object obj : propertyArray.getTypedValue()) {
                    assert obj instanceof StructPropertyList;
                    structPropertyList = (StructPropertyList)obj;

                    // Loop through all it's properties
                    for (Property<?> property : structPropertyList.getProperties()) {
                        propertyCount = 1;
                        // if present, find the properties count
                        for (Map.Entry<String, Integer> collectedProperty : properties.entrySet()) {
                            if (collectedProperty.getKey().equals(property.getNameString())) {
                                propertyCount = (collectedProperty.getValue() + 1);
                                break;
                            }
                        }

                        // Ensure the collected property with it's count to be present
                        properties.put(property.getNameString(), propertyCount);
                    }
                }

                // Display all properties
                strBuilder.append("Struct Properties:");
                for (Map.Entry<String, Integer> entry : properties.entrySet()) {
                    strBuilder.append(System.lineSeparator());
                    strBuilder.append("  ");
                    strBuilder.append(StringC.pad(Pad.RIGHT, entry.getKey(), 80));
                    strBuilder.append(entry.getValue());
                }
            } else {
                strBuilder.append("Properties:");

                Property<?> property;
                int index = 0;
                int indexPad = String.valueOf(propertyArray.getTypedValue().size()).length() + 2;
                for (Object obj : propertyArray.getTypedValue()) {
                    strBuilder.append(System.lineSeparator());
                    strBuilder.append("  ");
                    strBuilder.append("#");
                    strBuilder.append(StringC.pad(Pad.RIGHT, String.valueOf(index), indexPad));

                    if (obj instanceof Property<?>) {
                        property = (Property<?>)obj;

                        strBuilder.append(StringC.pad(Pad.RIGHT, property.getTypeString(), 22));
                        strBuilder.append(StringC.pad(Pad.RIGHT, property.getNameString(), 38));
                    } else {
                        strBuilder.append(StringC.pad(Pad.RIGHT, "~unknown property type~", 50));
                    }

                    strBuilder.append("class: ");
                    strBuilder.append(obj.getClass().getCanonicalName());

                    index++;
                }
            }
        } else {
            Property<?> property = (Property<?>)element;

            strBuilder.append(property.getType());
            strBuilder.append(System.lineSeparator());
            strBuilder.append("Property Value: ");
            strBuilder.append(property.getValue());
        }

        System.out.print(strBuilder.toString());

        return true;
    }

}