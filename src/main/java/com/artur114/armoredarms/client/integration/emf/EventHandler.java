package com.artur114.armoredarms.client.integration.emf;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.client.integration.emf.modelrender.ArmModelManagerAnimEMF;
import com.artur114.armoredarms.client.integration.emf.modelrender.ArmModelManagerEMF;
import com.artur114.armoredarms.client.integration.playeranimator.modelrender.ArmModelManagerPlayerAnim;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initModelManagersEvent(InitModelManagersEvent e) {
        e.registerManagerIfModLoaded(new ArmModelManagerEMF(), "entity_model_features");

        if (ModList.get().isLoaded("entity_model_features")) {
            e.registerManagerIfModLoaded(new ArmModelManagerAnimEMF(), "playeranimator");
        }
    }
}
