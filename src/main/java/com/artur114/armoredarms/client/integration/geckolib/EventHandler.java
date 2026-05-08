package com.artur114.armoredarms.client.integration.geckolib;

import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.ArmModelContainerGecko;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.BoneAdapterGeoBone;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initAdapters(InitBoneAdaptersEvent e) {
        e.registerAdapterIfModLoaded(BoneAdapterGeoBone.class, "geckolib");
    }

    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded(new ArmModelContainerGecko(), ShapelessLocation.ABSOLUTE, "geckolib");
    }
}
