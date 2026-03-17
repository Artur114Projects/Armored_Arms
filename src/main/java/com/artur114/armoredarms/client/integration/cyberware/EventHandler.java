package com.artur114.armoredarms.client.integration.cyberware;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.api.events.InitRenderPipelineEvent;
import com.artur114.armoredarms.client.integration.cyberware.engines.ArmRenderEngineCyberwareForge;
import com.artur114.armoredarms.client.integration.cyberware.event.MissingEssentialsHandler;
import com.artur114.armoredarms.client.integration.cyberware.modelrender.ArmModelRendererCyberware;
import com.artur114.armoredarms.client.modelrender.player.ArmModelContainerPlayer;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.RenderEngines;
import com.artur114.armoredarms.core.util.RenderPipelines;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    public static MissingEssentialsHandler HANDLER = null;


    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        if (HANDLER == null && EnumMods.CYBERWARE.isLoaded()) {
            HANDLER = new MissingEssentialsHandler();
            MinecraftForge.EVENT_BUS.register(HANDLER);
        }

        e.registerContainerIfModLoaded(new ArmModelContainerPlayer(ArmModelRendererCyberware.class, Priority.HIGH), "cyberware");
    }

    @SubscribeEvent
    public static void initRenderPipelineEvent(InitRenderPipelineEvent e) {
        RenderEngines.registerEngine(new ArmRenderEngineCyberwareForge());
    }
}
