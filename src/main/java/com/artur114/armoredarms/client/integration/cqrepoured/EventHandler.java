package com.artur114.armoredarms.client.integration.cqrepoured;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.cqrepoured.modelrender.ArmModelRendererSlime;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("cqrepoured", "chestplate_slime", new ArmModelContainerArmor(ArmModelRendererSlime.class));
    }
}