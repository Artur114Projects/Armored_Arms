package com.artur114.armoredarms.client.integration.hbm;

import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.hbm.modelrender.ArmModelContainerHBM;
import com.artur114.armoredarms.client.integration.hbm.modelrender.BoneAdapterModelRendererObj;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("hbm", "ncrpa_plate", new ArmModelContainerHBM("ncrpa_arm"));
        e.registerContainerIfModLoaded("hbm", "t45_plate", new ArmModelContainerHBM("rightarm", "leftarm", "item"));
        e.registerContainerIfModLoaded("hbm", "ajr_plate", new ArmModelContainerHBM("ajr_arm"));
        e.registerContainerIfModLoaded("hbm", "ajro_plate", new ArmModelContainerHBM("ajro_arm"));
        e.registerContainerIfModLoaded("hbm", "hev_plate", new ArmModelContainerHBM("hev_arm"));
        e.registerContainerIfModLoaded("hbm", "bj_plate", new ArmModelContainerHBM("bj_arm"));
        e.registerContainerIfModLoaded("hbm", "bj_plate_jetpack", new ArmModelContainerHBM("bj_arm"));
        e.registerContainerIfModLoaded("hbm", "rpa_plate", new ArmModelContainerHBM("rpa_arm"));
        e.registerContainerIfModLoaded("hbm", "fau_plate", new ArmModelContainerHBM("fau_arm"));
        e.registerContainerIfModLoaded("hbm", "dns_plate", new ArmModelContainerHBM("dnt_arm"));
        e.registerContainerIfModLoaded("hbm", "steamsuit_plate", new ArmModelContainerHBM("steamsuit_arm"));
        e.registerContainerIfModLoaded("hbm", "trenchmaster_plate", new ArmModelContainerHBM("trenchmaster_arm"));
        e.registerContainerIfModLoaded("hbm", "taurun_plate", new ArmModelContainerHBM("taurun_arm"));
        e.registerContainerIfModLoaded("hbm", "dieselsuit_plate", new ArmModelContainerHBM("dieselsuit_arm"));
        e.registerContainerIfModLoaded("hbm", "envsuit_plate", new ArmModelContainerHBM("envsuit_arm"));
        e.registerContainerIfModLoaded("hbm", "bismuth_plate", new ArmModelContainerHBM("armor_bismuth_tex"));
        e.registerContainerIfModLoaded("hbm", "t51_plate", new ArmModelContainerHBM("t51_arm"));
    }

    @SubscribeEvent
    public static void initBoneAdaptersEvent(InitBoneAdaptersEvent e) {
        e.registerAdapterIfModLoaded(BoneAdapterModelRendererObj.class, "hbm");
    }
}
