package com.artur114.armoredarms.client.integration.smartmoving;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.client.integration.smartmoving.modelrender.ArmModelManagerSmartPlayer;
import com.artur114.armoredarms.client.util.IPreInitListener;
import com.artur114.armoredarms.core.api.Priority;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerSM implements IPreInitListener {

    @SubscribeEvent
    public void initModelManagersEvent(InitModelManagersEvent e) {
        e.registerManagerIfModLoaded(new ArmModelManagerSmartPlayer(), "SmartMoving");
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
