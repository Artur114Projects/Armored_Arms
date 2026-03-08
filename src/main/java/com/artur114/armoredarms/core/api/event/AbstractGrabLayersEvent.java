package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractGrabLayersEvent<E extends IArmRenderEngine<?>> implements IEvent<Map<Class<IArmRenderLayer<E>>, IArmRenderLayer<E>>> {
    protected final Map<Class<IArmRenderLayer<E>>, IArmRenderLayer<E>> layers = new HashMap<>();
    protected final IAAModContainer mod;
    protected final Class<E> engine;

    public AbstractGrabLayersEvent(IAAModContainer mod, Class<E> engine) {
        this.engine = engine;
        this.mod = mod;
    }

    public Class<E> engine() {
        return this.engine;
    }

    public List<IArmRenderLayer<E>> layers() {
        return new ArrayList<>(this.layers.values());
    }

    public boolean removeLayerIfModLoaded(Class<IArmRenderLayer<E>> clazz, String modId) {
        if (this.mod.isModLoaded(modId)) {
            return this.registerLayer(clazz);
        }
        return false;
    }

    public boolean removeLayer(Class<IArmRenderLayer<E>> clazz) {
        return this.layers.remove(clazz) != null;
    }

    public boolean registerLayer(Class<IArmRenderLayer<E>> clazz) {
        try {
            IArmRenderLayer<E> layer = clazz.newInstance();
            if (layer.targetEngine().isAssignableFrom(this.engine)) {
                this.layers.put(clazz, layer);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public boolean registerLayerIfModLoaded(Class<IArmRenderLayer<E>> clazz, String modId) {
        if (this.mod.isModLoaded(modId)) {
            return this.registerLayer(clazz);
        }
        return false;
    }

    @Override
    public Map<Class<IArmRenderLayer<E>>, IArmRenderLayer<E>> result() {
        return this.layers;
    }
}
