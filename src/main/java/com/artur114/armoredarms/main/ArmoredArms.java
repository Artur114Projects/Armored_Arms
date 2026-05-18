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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.Bindings;
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
    protected IArmRenderPipeline<?> pipeline;
    public static final LoggingManager LOGGER = new LoggingManager();
    public static final String MODID = "armoredarms";
    public static ArmoredArms ARMORED_ARMS;

    public ArmoredArms() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AAConfig.SPEC);
        ARMORED_ARMS = this;
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        ArmoredArmsApi.registerEngineIfModLoaded(ArmRenderEnginePunchy.class, "punchy");

        if (!Bindings.getForgeBus().get().post(new InitRenderPipelineEvent(ARMORED_ARMS))) {
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
            return Bindings.getForgeBus().get().post(event);
        }
        return false;
    }
}
