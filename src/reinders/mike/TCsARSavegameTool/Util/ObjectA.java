package reinders.mike.TCsARSavegameTool.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ObjectA {

    private ObjectA() {
        // Empty
    }

    public static Object getPrivateField(Object obj, String property) {
        try {
            Field field = ObjectA.openPrivateField(obj, property);

            if (field != null) {
                return field.get(obj);
            }
        } catch (IllegalAccessException ignored) {}
        return null;
    }

    public static Object setPrivateField(Object obj, String property, Object value) {
        try {
            Field field = ObjectA.openPrivateField(obj, property);

            if (field != null) {
                field.set(obj, value);
                return true;
            }
        } catch (IllegalAccessException ignored) {}

        return false;
    }

    public static Field openPrivateField(Object obj, String property) {
        try {
            Field field = obj.getClass().getDeclaredField(property);
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            return field;
        } catch (NoSuchFieldException ignored) {
            return null;
        }
    }

}