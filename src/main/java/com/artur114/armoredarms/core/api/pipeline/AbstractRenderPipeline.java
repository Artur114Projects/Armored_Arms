package com.artur114.armoredarms.core.api.pipeline;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IAAModContainer;

public abstract class AbstractRenderPipeline<I extends AbstractRenderPipeline<?>> implements IArmRenderPipeline<I> {
    protected IArmRenderEngine<I> engine = null;
    protected IAAModContainer mod = null;

    @Override
    public void registerPipeline(IAAModContainer mod, IArmRenderEngine<I> engine) {
        this.engine = engine;
        this.mod = mod;

        this.register(mod, engine);
    }

    @Override
    public IArmRenderEngine<I> engine() {
        return this.engine;
    }

    protected abstract void register(IAAModContainer mod, IArmRenderEngine<I> engine);
}
