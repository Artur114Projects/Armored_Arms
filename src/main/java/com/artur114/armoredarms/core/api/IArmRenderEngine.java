package com.artur114.armoredarms.core.api;

import com.artur114.armoredarms.core.util.IAAModContainer;

public interface IArmRenderEngine<P extends IArmRenderPipeline<?>> extends IPrioritised {
    void init(P context, IAAModContainer mod);
    void tryRender(P context);
    void tryTick(P context);
    boolean canWork(IAAModContainer mod);
    Class<P> targetPipeline();
}
