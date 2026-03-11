package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IEvent;

public interface IAbstractLayerRenderEvent<E extends IArmRenderEngine<?>> extends IEvent<Boolean> {
    IArmRenderLayer<E> layer();
}
