package com.artur114.armoredarms.client.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Ultra dark magic class.
 */
public class Reflector {

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Class<?> clazz, Object obj, String name, Class<?>[] paramsClasses, Object[] params) {
        try {
            Method method = clazz.getDeclaredMethod(name, paramsClasses);

            boolean isAcc = method.isAccessible();
            method.setAccessible(true);
            Object ret = method.invoke(obj, params);
            method.setAccessible(isAcc);
            return (T) ret;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Class<?> superC = clazz.getSuperclass();
            if (superC == Object.class || superC == null) {
                try {
                    throw e;
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return invokeMethod(superC, obj, name, paramsClasses, params);
        }
    }

    public static <T> T getPrivateField(Object obj, String name) {
        return getPrivateField(obj.getClass(), obj, name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrivateField(Class<?> clazz, Object obj, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            boolean isAcc = field.canAccess(obj);
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
