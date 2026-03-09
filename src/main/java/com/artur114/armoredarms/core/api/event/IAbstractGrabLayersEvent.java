package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.IArmRenderLayer;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IAbstractGrabLayersEvent<E extends IArmRenderEngine<?>> extends IEvent<Map<Class<IArmRenderLayer<E>>, IArmRenderLayer<E>>> {
    Class<E> engine();
    IAAModContainer mod();
    Map<Class<IArmRenderLayer<E>>, IArmRenderLayer<E>> layers();

    default boolean removeLayerIfModLoaded(Class<IArmRenderLayer<E>> clazz, String modId) {
        if (this.mod().isModLoaded(modId)) {
            return this.registerLayer(clazz);
        }
        return false;
    }
    default boolean removeLayer(Class<IArmRenderLayer<E>> clazz) {
        return this.layers().remove(clazz) != null;
    }
    default boolean registerLayer(Class<IArmRenderLayer<E>> clazz) {
        try {
            IArmRenderLayer<E> layer = clazz.newInstance();
            if (layer.targetEngine().isAssignableFrom(this.engine())) {
                this.layers().put(clazz, layer);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
    default boolean registerLayerIfModLoaded(Class<IArmRenderLayer<E>> clazz, String modId) {
        if (this.mod().isModLoaded(modId)) {
            return this.registerLayer(clazz);
        }
        return false;
    }
}
