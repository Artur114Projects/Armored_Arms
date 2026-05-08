package com.artur114.armoredarms.client.integration.punchy;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderPipelineEvent;
import com.artur114.armoredarms.client.integration.punchy.engine.ArmRenderEnginePunchy;
import com.artur114.armoredarms.client.integration.punchy.modelrender.ArmModelManagerPunchy;
import com.artur114.armoredarms.client.integration.punchy.modelrender.BoneAdapterAzBonePunchy;
import com.artur114.armoredarms.client.integration.punchy.modelrender.BoneAdapterGeoBonePunchy;
import com.artur114.armoredarms.client.integration.punchy.modelrender.BoneAdapterModelPartPunchy;
import com.artur114.armoredarms.client.integration.punchy.pipeline.ArmRenderPipelinePunchy;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initAdapters(InitBoneAdaptersEvent e) {
        e.registerAdapterIfModLoaded(BoneAdapterModelPartPunchy.class, "punchy");

        if (EnumMods.PUNCHY.isLoaded()) {
            e.registerAdapterIfModLoaded(BoneAdapterGeoBonePunchy.class, "geckolib");
            e.registerAdapterIfModLoaded(BoneAdapterAzBonePunchy.class, "azurelib");
        }
    }

    @SubscribeEvent
    public static void initModelManager(InitModelManagersEvent e) {
        e.registerManagerIfModLoaded(new ArmModelManagerPunchy(), "punchy");
    }
}
