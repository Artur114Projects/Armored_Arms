package com.artur114.armoredarms.client.integration.brimm;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.brimm.modelrender.ArmModelRendererBrimm;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("brimm", "*", new ArmModelContainerArmor(ArmModelRendererBrimm.class));
    }
}