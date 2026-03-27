package com.artur114.armoredarms.client.integration.azurelib;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.azurelib.modelrender.ArmModelContainerAzure;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded(new ArmModelContainerAzure(), ShapelessLocation.ABSOLUTE, "azurelib");
    }
}
