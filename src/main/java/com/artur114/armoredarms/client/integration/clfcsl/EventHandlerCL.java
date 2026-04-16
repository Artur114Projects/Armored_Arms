package com.artur114.armoredarms.client.integration.clfcsl;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.clfcsl.modelrender.ArmModelRendererModelPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelContainerPlayer;
import com.artur114.armoredarms.client.util.IPreInitListener;
import com.artur114.armoredarms.core.api.Priority;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerCL implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded(new ArmModelContainerPlayer(ArmModelRendererModelPlayer.class, Priority.HIGH), "CompatibilityLayerForCustomSkinLoader");
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
