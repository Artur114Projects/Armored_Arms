package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IPrioritised;
import org.apache.logging.log4j.Logger;

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

    private final IAAModContainer mod;
    private final String name;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float offsetX;
    public float offsetY;
    public float offsetZ;

    protected Bone(IAAModContainer mod, String name) {
        this.name = name;
        this.mod = mod;
    }

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
                float rotationPointX = this.rotationPointX;
                float rotationPointY = this.rotationPointY;
                float rotationPointZ = this.rotationPointZ;
                float rotateAngleX = this.rotateAngleX;
                float rotateAngleY = this.rotateAngleY;
                float rotateAngleZ = this.rotateAngleZ;
                float offsetX = this.offsetX;
                float offsetY = this.offsetY;
                float offsetZ = this.offsetZ;

                source.set(this, obj);

                Logger logger = this.mod.logger().namedLogger("ARMOREDARMS-CORE");

                if (rotationPointX != this.rotationPointX || rotationPointY != this.rotationPointY || rotationPointZ != this.rotationPointZ) {
                    logger.debug("Bone[{}] update result: rotationPoint was changed", this.name);
                    logger.debug("    old rotationPoint: [{}, {}, {}]", rotationPointX, rotationPointY, rotationPointZ);
                    logger.debug("    new rotationPoint: [{}, {}, {}]", this.rotationPointX, this.rotationPointY, this.rotationPointZ);
                }

                if (rotateAngleX != this.rotateAngleX || rotateAngleY != this.rotateAngleY || rotateAngleZ != this.rotateAngleZ) {
                    logger.debug("Bone[{}] update result: rotateAngle was changed", this.name);
                    logger.debug("    old rotateAngle: [{}, {}, {}]", rotateAngleX, rotateAngleY, rotateAngleZ);
                    logger.debug("    new rotateAngle: [{}, {}, {}]", this.rotateAngleX, this.rotateAngleY, this.rotateAngleZ);
                }

                if (offsetX != this.offsetX || offsetY != this.offsetY || offsetZ != this.offsetZ) {
                    logger.debug("Bone[{}] update result: offset was changed", this.name);
                    logger.debug("    old offset: [{}, {}, {}]", offsetX, offsetY, offsetZ);
                    logger.debug("    new offset: [{}, {}, {}]", this.offsetX, this.offsetY, this.offsetZ);
                }

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
