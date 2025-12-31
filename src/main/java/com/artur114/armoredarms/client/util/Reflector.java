package com.artur114.armoredarms.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;

import java.lang.reflect.Field;
import java.util.List;

public class Reflector {
    public static <T> T getPrivateField(Object obj, String name) {
        return getPrivateField(obj.getClass(), obj, name);
    }

    public static ItemRenderer getItemRenderer(Minecraft mc) {
        try {
            Field field = null;
            Field[] fields = Minecraft.class.getDeclaredFields();

            for (Field rField : fields) {
                boolean isAcc = rField.isAccessible();
                rField.setAccessible(true);
                if (rField.get(mc) instanceof ItemRenderer) {
                    field = rField;
                }
                rField.setAccessible(isAcc);
            }

            if (field == null) {
                throw new IllegalStateException();
            }

            boolean isAcc = field.isAccessible();
            field.setAccessible(true);
            ItemRenderer renderer = (ItemRenderer) field.get(mc);
            field.setAccessible(isAcc);
            return renderer;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrivateField(Class<?> clazz, Object obj, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            boolean isAcc = field.isAccessible();
            field.setAccessible(true);
            Object ret = field.get(obj);
            field.setAccessible(isAcc);
            return (T) ret;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Class<?> superC = clazz.getSuperclass();
            if (superC == Object.class || superC == null) {
                try {
                    throw new NoSuchFieldException();
                } catch (NoSuchFieldException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return getPrivateField(superC, obj, name);
        }
    }

    public static boolean isClassExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
