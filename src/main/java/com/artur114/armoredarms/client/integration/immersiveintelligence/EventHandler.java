package com.artur114.armoredarms.client.integration.immersiveintelligence;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.immersiveintelligence.modelrender.ArmModelContainerII;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("immersiveintelligence", "light_engineer_armor_chestplate", new ArmModelContainerII());
    }
}