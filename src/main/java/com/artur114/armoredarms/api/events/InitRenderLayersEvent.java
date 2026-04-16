package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.event.IAbstractGrabLayersEvent;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.Map;

public class InitRenderLayersEvent extends Event implements IAbstractGrabLayersEvent<IArmRenderEngine<?>> {
    private final Map<Class<IArmRenderLayer<IArmRenderEngine<?>>>, IArmRenderLayer<IArmRenderEngine<?>>> map = new HashMap<>();
    private final Class<? extends IArmRenderEngine<?>> engine;
    private final IAAModContainer mod;

    public InitRenderLayersEvent(Class<? extends IArmRenderEngine<?>> engine, IAAModContainer mod) {
        this.engine = engine;
        this.mod = mod;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<IArmRenderEngine<?>> engine() {
        return (Class<IArmRenderEngine<?>>) this.engine;
    }

    @Override
    public IAAModContainer mod() {
        return this.mod;
    }

    @Override
    public Map<Class<IArmRenderLayer<IArmRenderEngine<?>>>, IArmRenderLayer<IArmRenderEngine<?>>> layers() {
        return this.map;
    }

    @Override
    public Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> result() {
        return new HashMap<>(this.map);
    }
}