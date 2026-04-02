package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IAAModContainer;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public interface IAbstractGrabLayersEvent<E extends IArmRenderEngine<?>> {
    Class<E> engine();
    IAAModContainer mod();
    Map<Class<IArmRenderLayer<E>>, IArmRenderLayer<E>> layers();
    Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> result();

    default boolean removeLayerIfModLoaded(Class<? extends IArmRenderLayer<?>> clazz, String modId) {
        if (this.mod().isModLoaded(modId)) {
            return this.removeLayer(clazz);
        }
        return false;
    }
    default boolean removeLayer(Class<? extends IArmRenderLayer<?>> clazz) {
        return this.layers().remove(clazz) != null;
    }
    @SuppressWarnings("unchecked")
    default boolean registerLayer(Class<? extends IArmRenderLayer<?>> clazz) {
        Logger logger = mod().logger().namedLogger("ARMOREDARMS-CORE");
        try {
            IArmRenderLayer<?> layer = clazz.newInstance();
            if (layer.targetEngine().isAssignableFrom(this.engine())) {
                this.layers().put((Class<IArmRenderLayer<E>>) clazz, (IArmRenderLayer<E>) layer);
                return true;
            }
        } catch (Exception e) {
            logger.error("An error occurs while registering layer");
            logger.error("  Layer: {}", clazz);
            logger.error("  Stack trace: ", e);
        }
        return false;
    }
    default boolean registerLayerIfModLoaded(Class<? extends IArmRenderLayer<?>> clazz, String modId) {
        if (this.mod().isModLoaded(modId)) {
            return this.registerLayer(clazz);
        }
        return false;
    }
}
