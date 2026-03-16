package com.artur114.armoredarms.client.integration.powersuits;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.powersuits.modelrender.ArmModelRendererMPS;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("powersuits", "powerarmor_torso", new ArmModelContainerArmor(ArmModelRendererMPS.class));
    }
}