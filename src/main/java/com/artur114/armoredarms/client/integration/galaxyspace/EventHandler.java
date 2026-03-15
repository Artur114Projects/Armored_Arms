package com.artur114.armoredarms.client.integration.galaxyspace;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.galaxyspace.modelrender.ArmModelContainerGS;
import com.artur114.armoredarms.client.integration.galaxyspace.modelrender.ArmModelRenderGSOBJ;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("galaxyspace", "space_suit_chest", new ArmModelContainerGS());
    }
}