package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderEngine;
import com.artur114.armoredarms.core.api.IArmRenderPipeline;

import java.util.HashSet;
import java.util.Set;

public class RenderEngines {
    private static final Set<IArmRenderEngine<?>> ENGINES = new HashSet<>();

    public static boolean registerEngineIfModLoaded(IAAModContainer aa, Class<? extends IArmRenderEngine<?>> clazz, String modId) {
        if (aa.isModLoaded(modId)) {
            try {
                return ENGINES.add(clazz.newInstance());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean registerEngine(IArmRenderEngine<?> pipeline) {
        return ENGINES.add(pipeline);
    }

    @SuppressWarnings("unchecked")
    public static <P extends IArmRenderPipeline<?>> IArmRenderEngine<P> pickUp(IAAModContainer mod, Class<P> clazz) {
        for (IArmRenderEngine<?> engine : CoreUtils.sortPrioritisedList(ENGINES)) {
            if (engine.targetPipeline().isAssignableFrom(clazz) && engine.canWork(mod)) {
                return (IArmRenderEngine<P>) engine;
            }
        }
        return null;
    }
}
