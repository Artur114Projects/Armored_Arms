package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * java doc
 */
public interface IAAModContainer {
    IArmRenderPipeline<?> pipeline();
    boolean isPipelineLoaded();
    Collection<IArmRenderPipeline<?>> defaultPipelines();
    Collection<IArmRenderEngine<?>> defaultEngines();
    void processException(RenderException exp);
    boolean isModLoaded(String modId);
    AbstractLoggingManager logger();
    boolean post(Object event);

    default IArmRenderPipeline<?> initPipelineSafety() {
        IArmRenderPipeline<?> pipeline = RenderPipelines.pickUpAndRegister(this);

        try {
            if (pipeline != null) {
                this.logger().AA_LOG.info("Rendering pipeline successfully loaded");
                this.logger().AA_LOG.info("   Pipeline: {}", pipeline);
            } else {
                IArmRenderPipeline<?> pipelineTry = RenderPipelines.pickUp(this);
                this.logger().AA_LOG.fatal("Rendering pipeline could not be loaded!");
                this.logger().AA_LOG.fatal("   Try to pick up pipeline: {}", pipelineTry);
                if (pipelineTry != null) {
                    this.logger().AA_LOG.fatal("   Try to pick up engine: {}", RenderEngines.pickUp(this, pipelineTry.clazz()));
                }
            }
        } catch (Exception ex) {
            LogManager.getLogger("ARMOREDARMS").fatal("An error occurred during initialization", ex);

            return null;
        }

        return pipeline;
    }
}
