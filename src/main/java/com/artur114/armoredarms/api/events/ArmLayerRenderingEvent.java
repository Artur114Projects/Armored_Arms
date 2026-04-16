package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.event.IAbstractLayerRenderEvent;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class ArmLayerRenderingEvent extends Event implements IAbstractLayerRenderEvent<IArmRenderEngine<?>> {
    private final IArmRenderLayer<? extends IArmRenderEngine<?>> layer;
    private final EnumHandSideAA side;

    public ArmLayerRenderingEvent(IArmRenderLayer<? extends IArmRenderEngine<?>> layer, EnumHandSideAA side) {
        this.layer = layer;
        this.side = side;
    }

    public EnumHandSideAA handSide() {
        return this.side;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IArmRenderLayer<IArmRenderEngine<?>> layer() {
        return (IArmRenderLayer<IArmRenderEngine<?>>) this.layer;
    }
}