package com.artur114.armoredarms.client.integration.wawelauth;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.wawelauth.modelrender.ArmModelRendererWawel;
import com.artur114.armoredarms.client.modelrender.player.ArmModelContainerPlayer;
import com.artur114.armoredarms.client.util.IPreInitListener;
import com.artur114.armoredarms.core.api.Priority;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerWA implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded(new ArmModelContainerPlayer(ArmModelRendererWawel.class, Priority.HIGHEST), "wawelauth");
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
