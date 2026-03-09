package com.artur114.armoredarms.core.api;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IAAModContainer;

public interface IArmRenderLayer<E extends IArmRenderEngine<?>> extends IPrioritised, IArmRenderComponent {
    void update(E engine);
    void render(E engine, EnumHandSideAA handSide);
    void init(E engine, IAAModContainer mod);
    boolean needRender(E engine, boolean renderEngineState);
    Class<E> targetEngine();

    @Override
    default String type() {
        return "layer";
    }
}
