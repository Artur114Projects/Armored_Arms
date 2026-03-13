package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;

public interface IAbstractLayerRenderEvent<E extends IArmRenderEngine<?>> {
    IArmRenderLayer<E> layer();
    EnumHandSideAA handSide();
}
