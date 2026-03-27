package com.artur114.armoredarms.main;

import com.artur114.armoredarms.api.events.InitRenderPipelineEvent;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderEngines;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.core.util.RenderPipelines;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArmoredArms.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(ArmoredArms.MODID)
public class ArmoredArms implements IAAModContainer {
    protected static IArmRenderPipeline<?> pipeline;
    protected static ArmoredArms mod;

    public static final LoggingManager LOGGER = new LoggingManager();
    public static final String MODID = "armoredarms";

    public static ArmoredArms mod() {
        return mod;
    }

    public static IArmRenderPipeline<?> pipeline() {
        return pipeline;
    }

    public static boolean isPipelineLoaded() {
        return pipeline != null;
    }

    public ArmoredArms() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AAConfig.SPEC);
        mod = this;
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        if (!mod().post(new InitRenderPipelineEvent(mod()))) {
            pipeline = RenderPipelines.pickUpAndRegister(mod);
        }

        if (isPipelineLoaded()) {
            LOGGER.AA_LOG.info("Rendering pipeline successfully loaded");
            LOGGER.AA_LOG.info("   Pipeline: {}", pipeline);
        } else {
            IArmRenderPipeline<?> pipeline = RenderPipelines.pickUp(mod);
            LOGGER.AA_LOG.fatal("Rendering pipeline could not be loaded!");
            LOGGER.AA_LOG.fatal("   Try to pick up pipeline: {}", pipeline);
            LOGGER.AA_LOG.fatal("   Try to pick up engine: {}", RenderEngines.pickUp(mod, pipeline.clazz()));
        }
    }
    
    @Override
    public Collection<IArmRenderPipeline<?>> defaultPipelines() {
        return List.of(new ArmRenderPipelineForge());
    }

    @Override
    public Collection<IArmRenderEngine<?>> defaultEngines() {
        return List.of(new ArmRenderEngineForge());
    }

    @Override
    public void processException(RenderException exp) {
        LOGGER.processException(exp);
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
