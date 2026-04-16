package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.api.event.IAbstractGrabRenderContainersEvent;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.SLContainer;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.List;

public class InitRenderContainersEvent extends Event implements IAbstractGrabRenderContainersEvent {
    private final List<SLContainer<IArmModelRenderContainer<?, ?>>> containersSL;
    private final List<IArmModelRenderContainer<?, ?>> containers;
    private final Class<? extends IArmRenderLayer<?>> layer;
    private final IAAModContainer mod;

    public InitRenderContainersEvent(Class<? extends IArmRenderLayer<?>> layer, IAAModContainer mod, boolean located) {
        this.layer = layer;
        this.mod = mod;

        if (located) {
            this.containersSL = new ArrayList<>();
            this.containers = null;
        } else {
            this.containers = new ArrayList<>();
            this.containersSL = null;
        }
    }

    @Override
    public List<IArmModelRenderContainer<?, ?>> containers() {
        return this.containers;
    }

    @Override
    public List<SLContainer<IArmModelRenderContainer<?, ?>>> containersSL() {
        return this.containersSL;
    }

    @Override
    public IAAModContainer mod() {
        return this.mod;
    }

    @Override
    public Class<? extends IArmRenderLayer<?>> layer() {
        return this.layer;
    }
}