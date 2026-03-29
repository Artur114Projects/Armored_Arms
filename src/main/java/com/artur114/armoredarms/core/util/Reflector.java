package com.artur114.armoredarms.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflector {
    public static <T> T getPrivateField(Object obj, String name) {
        return getPrivateField(obj.getClass(), obj, name);
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (Exception e) {
            Class<?> superC = clazz.getSuperclass();
            if (superC == Object.class || superC == null) {
                try {
                    throw new NoSuchMethodException();
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return findMethod(superC, name, params);
        }
    }

    public static <T> T invokeStaticMethod(Method method, Object... params) {
        return invokeMethod(method, null, params);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object obj, Object... params) {
        try {
            boolean isAcc = method.isAccessible();
            method.setAccessible(true);
            Object ret = method.invoke(obj, params);
            method.setAccessible(isAcc);
            return (T) ret;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
            Class<?> superC = clazz.getSuperclass();
            if (superC == Object.class || superC == null) {
                try {
                    throw new NoSuchMethodException();
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return invokeMethod(superC, obj, name, paramsClasses, params);
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