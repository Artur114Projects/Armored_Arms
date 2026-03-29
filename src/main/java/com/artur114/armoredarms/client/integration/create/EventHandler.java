package com.artur114.armoredarms.client.integration.create;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.create.modelrender.ArmModelRendererCreate;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.modelrender.player.ArmModelContainerPlayer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("create", "netherite_backtank", new ArmModelContainerArmor(ArmModelRendererCreate.class));
    }
}