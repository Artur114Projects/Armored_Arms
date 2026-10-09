package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;

import java.util.HashSet;
import java.util.Set;

public class RenderEngines {
    private static final Set<IArmRenderEngine<?>> ENGINES = new HashSet<>();
    private static boolean loadedDefaultKit = false;

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

    public static boolean registerEngine(IArmRenderEngine<?> engine) {
        return ENGINES.add(engine);
    }

    public static void registerEngines(Iterable<IArmRenderEngine<?>> engines) {
        for (IArmRenderEngine<?> engine : engines) {
            registerEngine(engine);
        }
    }

    @SuppressWarnings("unchecked")
    public static <P extends IArmRenderPipeline<?>> IArmRenderEngine<P> pickUp(IAAModContainer mod, Class<P> clazz) {
        if (mod != null && !loadedDefaultKit) {
            registerEngines(mod.defaultEngines()); loadedDefaultKit = true;
        }
        for (IArmRenderEngine<?> engine : CoreUtils.sortPrioritisedList(ENGINES)) {
            if (engine.targetPipeline().isAssignableFrom(clazz) && engine.canWork(mod)) {
                return (IArmRenderEngine<P>) engine;
            }
        }
        return null;
    }

    private static void clear() {
        ENGINES.clear();;
    }
}
