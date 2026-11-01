package com.artur114.armoredarms.client.integration.curios;

import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.integration.curios.layers.ArmRenderLayerCurio;
import com.artur114.armoredarms.client.util.EnumMods;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initArmRenderLayersEvent(InitRenderLayersEvent e) {
        // ArmRenderLayerCurio links geckolib types at class-load time, so it must not be instantiated without geckolib.
        if (EnumMods.GECKO_LIB.isLoaded()) {
            e.registerLayerIfModLoaded(ArmRenderLayerCurio.class, "curios");
        }
    }
}
