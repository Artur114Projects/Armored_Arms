package com.artur114.armoredarms.client.integration.thaumicconcilium;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.thaumicconcilium.modelrender.ArmModelRendererPontifexRobe;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerTC implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("ThaumicConcilium", "PontifexRobeChest", new ArmModelContainerArmor(ArmModelRendererPontifexRobe.class));
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
