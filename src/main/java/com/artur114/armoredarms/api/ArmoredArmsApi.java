package com.artur114.armoredarms.api;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.RenderEngines;
import com.artur114.armoredarms.core.util.RenderPipelines;
import com.artur114.armoredarms.main.ArmoredArms;


public class ArmoredArmsApi {
    public static void registerPipeline(IArmRenderPipeline<?> pipeline) {
        RenderPipelines.registerPipeline(pipeline);
    }

    public static void registerPipelineIfModLoaded(Class<? extends IArmRenderPipeline<?>> pipeline, String modId) {
        RenderPipelines.registerPipelineIfModLoaded(ArmoredArms.ARMORED_ARMS, pipeline, modId);
    }

    public static void registerEngine(IArmRenderEngine<?> engine) {
        RenderEngines.registerEngine(engine);
    }

    public static void registerEngineIfModLoaded(Class<? extends IArmRenderEngine<?>> engine, String modId) {
        RenderEngines.registerEngineIfModLoaded(ArmoredArms.ARMORED_ARMS, engine, modId);
    }

    public static IArmRenderPipeline<?> currentPipeline() {
        return ArmoredArms.ARMORED_ARMS.pipeline();
    }
}
