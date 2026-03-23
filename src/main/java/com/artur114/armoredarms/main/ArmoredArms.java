package com.artur114.armoredarms.main;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber
@Mod(ArmoredArms.MODID)
public class ArmoredArms implements IAAModContainer {
    public static final LoggingManager LOGGER = new LoggingManager();
    public static final String MODID = "armoredarms";

    public ArmoredArms() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AAConfig.SPEC);
    }


    @Override
    public Collection<IArmRenderPipeline<?>> defaultPipelines() {
        return List.of();
    }

    @Override
    public Collection<IArmRenderEngine<?>> defaultEngines() {
        return List.of();
    }

    @Override
    public void processException(RenderException exp) {

    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean post(Object obj) {
        if (obj instanceof Event event) {
            return MinecraftForge.EVENT_BUS.post(event);
        }
        return false;
    }

    @Override
    public Logger logger(String name) {
        return LOGGER.logger(name);
    }
}
