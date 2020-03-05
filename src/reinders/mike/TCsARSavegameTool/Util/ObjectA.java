package reinders.mike.TCsARSavegameTool.Util;

import qowyn.ark.ArkSavFile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ObjectA {

    private ObjectA() {
        // Empty
    }

    public static Object getPrivateField(Object obj, String property) {
        try {
            Field field = obj.getClass().getDeclaredField("className");
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return null;
        }
    }

}