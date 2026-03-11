package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IEvent;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.core.util.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = ArmoredArms.MODID, useMetadata = true, clientSideOnly = true)
public class ArmoredArms implements IAAModContainer {
    public static final Logger LOGGER = LogManager.getLogger("ARMOREDARMS");
    protected static final Map<String, Logger> loggers = new HashMap<>();
    protected static IArmRenderPipeline<?> pipeline = null;
    public static final String MODID = "armoredarms";

    @Mod.Instance
    public static ArmoredArms ARMORED_ARMS;

    public static IArmRenderPipeline<?> pipeline() {
        return pipeline;
    }

    public static boolean isPipelineLoaded() {
        return pipeline != null;
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        pipeline = RenderPipelines.pickUpAndRegister(this);

        if (isPipelineLoaded()) {
            LOGGER.info("Rendering pipeline successfully loaded, pipeline: {}", pipeline.getClass());
        } else {
            LOGGER.fatal("Rendering pipeline could not be loaded!");
        }
    }

    @Override
    public Collection<IArmRenderPipeline<?>> defaultPipelines() {
        return Collections.singletonList(new ArmRenderPipelineForge());
    }

    @Override
    public Collection<IArmRenderEngine<?>> defaultEngines() {
        return Collections.singletonList(new ArmRenderEngineForge());
    }

    @Override
    public void processException(RenderException exp) {
        LOGGER.error(exp);
        exp.printStackTrace(System.err);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return Loader.isModLoaded(modId);
    }

    @Override
    public <R> R post(IEvent<R> event) {
        if (event instanceof Event) {
            MinecraftForge.EVENT_BUS.post((Event) event);
            return event.result();
        }
        return null;
    }

    @Override
    public Logger logger(String name) {
        return loggers.computeIfAbsent(name, LogManager::getLogger);
    }
}
