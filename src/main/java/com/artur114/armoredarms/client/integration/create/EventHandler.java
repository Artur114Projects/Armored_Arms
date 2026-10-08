package com.artur114.armoredarms.client.integration.create;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.create.modelrender.ArmModelRendererCreate;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("create", "netherite_backtank", new ArmModelContainerArmor(ArmModelRendererCreate.class));
    }
}