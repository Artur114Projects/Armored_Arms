package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.AbstractLoggingManager;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderException;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.Collections;

@Mod(modid = ArmoredArms.MODID, guiFactory = ArmoredArms.GUI_FACTORY, useMetadata = true)
public class ArmoredArms implements IAAModContainer {
    public static final LoggingManager LOGGER = new LoggingManager();
    public static final AAConfig CONFIGS = new AAConfig();
    public static final String GUI_FACTORY = "com.artur114.armoredarms.main.AAConfig$ConfigGuiFactory";
    public static final String MODID = "armoredarms";
    private IArmRenderPipeline<?> pipeline = null;

    @Mod.Instance
    public static ArmoredArms ARMORED_ARMS;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        CONFIGS.fMLPreInitializationEvent(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        this.pipeline = this.initPipelineSafety();
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
        return Collections.singletonList(new ArmRenderPipelineForge());
    }

    @Override
    public Collection<IArmRenderEngine<?>> defaultEngines() {
        return Collections.singletonList(new ArmRenderEngineForge());
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
            MinecraftForge.EVENT_BUS.post((Event) event);
        }
        return false;
    }
}
