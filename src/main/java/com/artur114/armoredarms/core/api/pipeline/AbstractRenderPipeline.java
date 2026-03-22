package com.artur114.armoredarms.core.api.pipeline;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.IAAModContainer;

public abstract class AbstractRenderPipeline<I extends AbstractRenderPipeline<?>> implements IArmRenderPipeline<I> {
    protected IArmRenderEngine<I> engine = null;
    protected IAAModContainer mod = null;
    public boolean deactivated = false;
    public boolean initTick = true;

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

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    protected abstract void register(IAAModContainer mod, IArmRenderEngine<I> engine);
}
