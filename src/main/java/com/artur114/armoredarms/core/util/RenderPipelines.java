package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;

import java.util.HashSet;
import java.util.Set;

public class RenderPipelines {
    private static final Set<IArmRenderPipeline<?>> pipelines = new HashSet<>();
    private static boolean loadedDefaultKit = false;

    public static boolean registerPipelineIfModLoaded(IAAModContainer aa, Class<? extends IArmRenderPipeline<?>> clazz, String modId) {
        if (aa.isModLoaded(modId)) {
            try {
                return pipelines.add(clazz.newInstance());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean registerPipeline(IArmRenderPipeline<?> pipeline) {
        return pipelines.add(pipeline);
    }

    public static void registerPipelines(Iterable<IArmRenderPipeline<?>> pipelines) {
        for (IArmRenderPipeline<?> engine : pipelines) {
            registerPipeline(engine);
        }
    }

    public static IArmRenderPipeline<?> pickUp(IAAModContainer mod) {
        if (mod != null && !loadedDefaultKit) {
            registerPipelines(mod.defaultPipelines()); loadedDefaultKit = true;
        }
        for (IArmRenderPipeline<?> pipeline : CoreUtils.sortPrioritisedList(pipelines)) {
            if (pipeline.canWork(mod)) {
                return pipeline;
            }
        }
        return null;
    }

    public static IArmRenderPipeline<?> pickUpAndRegister(IAAModContainer mod) {
        IArmRenderPipeline<?> pipeline = pickUp(mod);
        if (pipeline != null) {
            IArmRenderEngine<?> engine = RenderEngines.pickUp(mod, pipeline.clazz());

            if (engine != null) {
                pipeline.registerPipeline(mod, (IArmRenderEngine) engine);
                return pipeline;
            }
        }
        return null;
    }
}
