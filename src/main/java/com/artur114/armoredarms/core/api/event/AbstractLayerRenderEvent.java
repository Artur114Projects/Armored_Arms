package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IEvent;

public abstract class AbstractLayerRenderEvent implements IEvent<Boolean> {
    protected final IArmRenderLayer<?> layer;

    protected AbstractLayerRenderEvent(IArmRenderLayer<?> layer) {
        this.layer = layer;
    }

    public IArmRenderLayer<?> layer() {
        return this.layer;
    }
}
