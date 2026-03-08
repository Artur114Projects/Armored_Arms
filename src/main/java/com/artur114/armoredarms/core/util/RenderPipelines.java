package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderEngine;
import com.artur114.armoredarms.core.api.IArmRenderPipeline;

import java.util.HashSet;
import java.util.Set;

public class RenderPipelines {
    private static final Set<IArmRenderPipeline<?>> pipelines = new HashSet<>();

    public static boolean registerPipelineIfModLoaded(IAAModContainer aa, Class<IArmRenderPipeline<?>> clazz, String modId) {
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

    public static IArmRenderPipeline<?> pickUp(IAAModContainer mod) {
        try {
            for (IArmRenderPipeline<?> pipeline : CoreUtils.sortPrioritisedList(pipelines)) {
                if (pipeline.canWork(mod)) {
                    return pipeline;
                }
            }
            return null;
        } finally {
            pipelines.clear();
        }
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
