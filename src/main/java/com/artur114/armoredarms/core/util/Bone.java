package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IPrioritised;

import java.util.ArrayList;
import java.util.List;

public class Bone {
    private static List<IBoneAdapter<Object>> sources = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static void register(IBoneAdapter<?> source) {
        sources.add((IBoneAdapter<Object>) source);
        sources = CoreUtils.sortPrioritisedList(sources);
    }

    public static boolean hasAdapterFor(Class<?> clazz) {
        for (IBoneAdapter<Object> source : sources) {
            if (source.targetObjectClass().isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float offsetX;
    public float offsetY;
    public float offsetZ;

    public void injectTo(Object obj) {
        Class<?> clazz = obj.getClass();

        for (IBoneAdapter<Object> source : sources) {
            if (source.targetObjectClass().isAssignableFrom(clazz)) {
                source.inject(this, obj);
                return;
            }
        }

        throw new IllegalArgumentException("Can't find bone source for " + clazz);
    }

    public void setFrom(Object obj) {
        Class<?> clazz = obj.getClass();

        for (IBoneAdapter<Object> source : sources) {
            if (source.targetObjectClass().isAssignableFrom(clazz)) {
                source.set(this, obj);
                return;
            }
        }

        throw new IllegalArgumentException("Can't find bone source for " + clazz);
    }

    public interface IBoneAdapter<T> extends IPrioritised {
        void inject(Bone bone, T to);
        void set(Bone bone, T from);
        Class<T> targetObjectClass();
    }
}
