package com.artur114.armoredarms.client.integration.botania;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.botania.modelrender.ArmModelContainerBotania;
import com.artur114.armoredarms.client.util.IPreInitListener;
import com.artur114.armoredarms.core.api.Priority;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerBO implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("Botania", "*", new ArmModelContainerBotania());
        e.registerContainerIfModLoaded("Botania", "terrasteelChest", new ArmModelContainerBotania(Priority.HIGH, "armr", "armL"));
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
