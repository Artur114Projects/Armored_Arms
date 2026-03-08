package com.artur114.armoredarms.core.api.pipeline;

import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.util.IAAModContainer;

public interface IArmRenderPipeline<I extends IArmRenderPipeline<?>> extends IPrioritised, IArmRenderComponent {
    void registerPipeline(IAAModContainer mod, IArmRenderEngine<I> engine);
    boolean canWork(IAAModContainer mod);
    IArmRenderEngine<I> engine();
    Class<I> clazz();

    @Override
    default String type() {
        return "pipeline";
    }
}
