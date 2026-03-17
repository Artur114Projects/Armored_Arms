package com.artur114.armoredarms.main;

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

// TODO Доделать логирование ошибок
// TODO Сделать ArmoredArmsApi
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
            LOGGER.info("Rendering pipeline successfully loaded");
            LOGGER.info("   Pipeline: {}", pipeline.getClass());
        } else {
            IArmRenderPipeline<?> pipeline = RenderPipelines.pickUp(this);
            LOGGER.fatal("Rendering pipeline could not be loaded!");
            LOGGER.fatal("   Try to pick up pipeline: {}", pipeline);
            LOGGER.fatal("   Try to pick up engine: {}", RenderEngines.pickUp(this, pipeline.clazz()));
        }

        AAConfig.init();
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
        IArmRenderComponent broken = exp.brokenComponent();
        Level level = Level.ERROR;

        if (exp.type() == EnumExceptionType.FATAL) {
            level = Level.FATAL;
        }

        if (exp.type() == EnumExceptionType.WARN) {
            level = Level.WARN;
        }

        if (exp.type() != EnumExceptionType.WARN && broken != null) {
            broken.deactivate();
        }
        if (exp.type() == EnumExceptionType.FATAL) {
            pipeline.deactivate();
        }

        String component = "?unknown-component?";
        String message = "an error occurred in component: ";

        if (broken != null) {
            component = broken.type();
        }

        switch (exp.type()) {
            case WARN:
                message = "Warn an error occurred in component: ";
            break;
            case ERROR:
                message = "An error occurred in component: ";
            break;
            case FATAL:
                message = "An fatal error occurred in component: ";
            break;
        }

        LOGGER.log(level, message + component, exp);

        if (exp.messageForPlayer() != null) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(TextFormatting.RED + exp.messageForPlayer()));
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED + exp.getLocalizedMessage()));
        }
    }

    @Override
    public boolean isModLoaded(String modId) {
        return Loader.isModLoaded(modId);
    }

    @Override
    public boolean post(Object event) {
        if (event instanceof Event) {
            return MinecraftForge.EVENT_BUS.post((Event) event);
        }
        return false;
    }

    @Override
    public Logger logger(String name) {
        return loggers.computeIfAbsent(name, LogManager::getLogger);
    }
}
