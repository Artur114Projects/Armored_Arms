package com.artur114.armoredarms.main;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.events.InitRenderPipelineEvent;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.integration.punchy.engine.ArmRenderEnginePunchy;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineMixin;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;


@EventBusSubscriber(value = Dist.CLIENT, modid = ArmoredArms.MODID, bus = EventBusSubscriber.Bus.MOD)
@Mod(ArmoredArms.MODID)
public class ArmoredArms implements IAAModContainer {
    protected IArmRenderPipeline<?> pipeline;
    public static final LoggingManager LOGGER = new LoggingManager();
    public static final String MODID = "armoredarms";
    public static ArmoredArms ARMORED_ARMS;

    public ArmoredArms(ModContainer container, IEventBus modEventBus) {
        container.registerConfig(ModConfig.Type.CLIENT, AAConfig.SPEC);
        ARMORED_ARMS = this;
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        ArmoredArmsApi.registerEngineIfModLoaded(ArmRenderEnginePunchy.class, "punchy");

        if (!NeoForge.EVENT_BUS.post(new InitRenderPipelineEvent(ARMORED_ARMS)).isCanceled()) {
            ARMORED_ARMS.pipeline = ARMORED_ARMS.initPipelineSafety();
        }
    }

    @Override
    public IArmRenderPipeline<?> pipeline() {
        return this.pipeline;
    }

    @Override
    public boolean isPipelineLoaded() {
        return this.pipeline != null;
    }

    @Override
    public Collection<IArmRenderPipeline<?>> defaultPipelines() {
        return List.of(new ArmRenderPipelineForge(), new ArmRenderPipelineMixin());
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
    public AbstractLoggingManager logger() {
        return LOGGER;
    }

    @Override
    public boolean post(Object obj) {
        if (obj instanceof Event event) {
            Event e = NeoForge.EVENT_BUS.post(event);
            return e instanceof ICancellableEvent c && c.isCanceled();
        }
        return false;
    }
}
