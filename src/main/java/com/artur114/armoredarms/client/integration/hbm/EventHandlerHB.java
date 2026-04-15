package com.artur114.armoredarms.client.integration.hbm;

import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.botania.modelrender.ArmModelContainerBotania;
import com.artur114.armoredarms.client.integration.hbm.modelrender.ArmModelContainerHBM;
import com.artur114.armoredarms.client.util.IPreInitListener;
import com.artur114.armoredarms.core.api.Priority;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerHB implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("hbm", "item.t45_plate", new ArmModelContainerHBM("rightarm", "leftarm"));
        e.registerContainerIfModLoaded("hbm", "item.ajr_plate", new ArmModelContainerHBM("ajr_arm"));
        e.registerContainerIfModLoaded("hbm", "item.ajro_plate", new ArmModelContainerHBM("ajro_arm"));
        e.registerContainerIfModLoaded("hbm", "item.hev_plate", new ArmModelContainerHBM("hev_arm"));
        e.registerContainerIfModLoaded("hbm", "item.bj_plate", new ArmModelContainerHBM("bj_arm"));
        e.registerContainerIfModLoaded("hbm", "item.bj_plate_jetpack", new ArmModelContainerHBM("bj_arm"));
        e.registerContainerIfModLoaded("hbm", "item.rpa_plate", new ArmModelContainerHBM("rpa_arm"));
        e.registerContainerIfModLoaded("hbm", "item.fau_plate", new ArmModelContainerHBM("fau_arm"));
        e.registerContainerIfModLoaded("hbm", "item.dns_plate", new ArmModelContainerHBM("dnt_arm"));
        e.registerContainerIfModLoaded("hbm", "item.steamsuit_plate", new ArmModelContainerHBM("steamsuit_arm"));
        e.registerContainerIfModLoaded("hbm", "item.trenchmaster_plate", new ArmModelContainerHBM("trenchmaster_arm"));
        e.registerContainerIfModLoaded("hbm", "item.taurun_plate", new ArmModelContainerHBM("taurun_arm"));
        e.registerContainerIfModLoaded("hbm", "item.dieselsuit_plate", new ArmModelContainerHBM("dieselsuit_arm"));
        e.registerContainerIfModLoaded("hbm", "item.envsuit_plate", new ArmModelContainerHBM("envsuit_arm"));
        e.registerContainerIfModLoaded("hbm", "item.bismuth_plate", new ArmModelContainerHBM("armor_bismuth_tex"));
        e.registerContainerIfModLoaded("hbm", "item.t51_plate", new ArmModelContainerHBM("t51_arm"));
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
