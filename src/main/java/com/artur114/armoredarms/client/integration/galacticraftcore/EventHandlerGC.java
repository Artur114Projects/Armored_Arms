package com.artur114.armoredarms.client.integration.galacticraftcore;

import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.integration.galacticraftcore.layer.ArmRenderLayerThermalPadding;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerGC implements IPreInitListener {

    @SubscribeEvent
    public void initArmRenderLayers(InitRenderLayersEvent e) {
        e.registerLayerIfModLoaded(ArmRenderLayerThermalPadding.class, "GalacticraftCore");
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
