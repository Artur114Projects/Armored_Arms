package com.artur114.armoredarms.core.api.layer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IAAModContainer;

public interface IArmRenderLayer<E extends IArmRenderEngine<?>> extends IPrioritised, IArmRenderComponent {
    void update(E engine);
    void render(E engine, EnumHandSideAA handSide);
    void init(E engine, IAAModContainer mod);
    boolean needRender(E engine, boolean renderEngineState);
    Class<E> targetEngine();
    E engine();

    @Override
    default String type() {
        return "layer";
    }
}
