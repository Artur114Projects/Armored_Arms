package com.artur114.armoredarms.main;

import com.artur114.armoredarms.api.events.InitRenderPipelineEvent;
import com.artur114.armoredarms.client.engines.ArmRenderEngineCleanRoom;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineCleanRoom;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = ArmoredArms.MODID, useMetadata = true, clientSideOnly = true)
public class ArmoredArms implements IAAModContainer {
    public static final LoggingManager LOGGER = new LoggingManager();
    public static final String MODID = "armoredarms";

    protected static IArmRenderPipeline<?> pipeline = null;

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
        try {
            AAConfig.init();

            if (!this.post(new InitRenderPipelineEvent(this))) {
                pipeline = RenderPipelines.pickUpAndRegister(this);
            }

            if (isPipelineLoaded()) {
                LOGGER.AA_LOG.info("Rendering pipeline successfully loaded");
                LOGGER.AA_LOG.info("   Pipeline: {}", pipeline.getClass());
            } else {
                IArmRenderPipeline<?> pipeline = RenderPipelines.pickUp(this);
                LOGGER.AA_LOG.fatal("Rendering pipeline could not be loaded!");
                LOGGER.AA_LOG.fatal("   Try to pick up pipeline: {}", pipeline);
                if (pipeline != null) {
                    LOGGER.AA_LOG.fatal("   Try to pick up engine: {}", RenderEngines.pickUp(this, pipeline.clazz()));
                }
            }
        } catch (Exception ex) {
            LogManager.getLogger("ARMOREDARMS").fatal("An error occurred during initialization", ex);
        }
    }

    @Override
    public Collection<IArmRenderPipeline<?>> defaultPipelines() {
        return Arrays.asList(new ArmRenderPipelineForge(), new ArmRenderPipelineCleanRoom());
    }

    @Override
    public Collection<IArmRenderEngine<?>> defaultEngines() {
        return Arrays.asList(new ArmRenderEngineForge(), new ArmRenderEngineCleanRoom());
    }

    @Override
    public void processException(RenderException exp) {
        LOGGER.processException(exp);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return Loader.isModLoaded(modId);
    }

    @Override
    public AbstractLoggingManager logger() {
        return LOGGER;
    }

    @Override
    public boolean post(Object event) {
        if (event instanceof Event) {
            return MinecraftForge.EVENT_BUS.post((Event) event);
        }
        return false;
    }
}
