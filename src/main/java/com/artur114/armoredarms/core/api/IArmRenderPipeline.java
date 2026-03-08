package com.artur114.armoredarms.core.api;

import com.artur114.armoredarms.core.util.IAAModContainer;

public interface IArmRenderPipeline<I extends IArmRenderPipeline<?>> extends IPrioritised {
    void registerPipeline(IAAModContainer mod, IArmRenderEngine<I> engine);
    boolean canWork(IAAModContainer mod);
    IArmRenderEngine<I> engine();
    Class<I> clazz();
}
