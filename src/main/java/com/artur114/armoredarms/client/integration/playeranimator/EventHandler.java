package com.artur114.armoredarms.client.integration.playeranimator;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.client.integration.playeranimator.modelrender.ArmModelManagerPlayerAnim;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initModelManagersEvent(InitModelManagersEvent e) {
        e.registerManagerIfModLoaded(new ArmModelManagerPlayerAnim(), "playeranimator");
    }
}
