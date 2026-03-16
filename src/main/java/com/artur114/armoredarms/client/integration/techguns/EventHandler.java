package com.artur114.armoredarms.client.integration.techguns;

import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.integration.techguns.layer.ArmRenderLayerTechGuns;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initArmRenderLayers(InitRenderLayersEvent e) {
        e.registerLayerIfModLoaded(ArmRenderLayerTechGuns.class, "techguns");
    }
}
