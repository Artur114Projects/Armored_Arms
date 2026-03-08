package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;

import java.util.Collection;

public interface IAAModContainer {
    Collection<IArmRenderPipeline<?>> defaultPipelines();
    Collection<IArmRenderEngine<?>> defaultEngines();
    void processException(RenderException exp);
    boolean isModLoaded(String modId);
    <R> R post(IEvent<R> event);
}
