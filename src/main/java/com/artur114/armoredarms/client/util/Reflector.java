package com.artur114.armoredarms.client.util;

import java.lang.reflect.Field;

public class Reflector {
    public static <T> T getPrivateField(Object obj, String name) {
        return getPrivateField(obj.getClass(), obj, name);
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

}
