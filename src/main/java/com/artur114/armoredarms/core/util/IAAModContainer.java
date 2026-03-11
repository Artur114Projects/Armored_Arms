package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * java doc
 */
public interface IAAModContainer {
    Collection<IArmRenderPipeline<?>> defaultPipelines();
    Collection<IArmRenderEngine<?>> defaultEngines();
    void processException(RenderException exp);
    boolean isModLoaded(String modId);
    <R> R post(IEvent<R> event);
    Logger logger(String name);
}
