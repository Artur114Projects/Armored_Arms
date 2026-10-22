package com.artur114.armoredarms.client.integration.curios;

import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.integration.curios.layers.ArmRenderLayerCurio;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initArmRenderLayersEvent(InitRenderLayersEvent e) {
        e.registerLayerIfModLoaded(ArmRenderLayerCurio.class, "curios");
    }
}
