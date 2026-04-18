package com.artur114.armoredarms.client.integration.lotr;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.lotr.modelrender.ArmModelRendererLotr;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerLT implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("lotr", "item.bodydolamroth", new ArmModelContainerArmor(ArmModelRendererLotr.class));
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
