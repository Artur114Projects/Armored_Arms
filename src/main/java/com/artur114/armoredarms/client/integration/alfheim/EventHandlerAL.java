package com.artur114.armoredarms.client.integration.alfheim;

import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.integration.alfheim.modelrender.ArmModelContainerAlfheim;
import com.artur114.armoredarms.client.integration.alfheim.modelrender.BoneAdapterAlfheim;
import com.artur114.armoredarms.client.integration.botania.modelrender.ArmModelContainerBotania;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerAL implements IPreInitListener {

    @SubscribeEvent
    public void initRenderContainersEvent(InitRenderContainersEvent e) {
        e.registerContainerIfModLoaded("alfheim", "ElementalEarthChest", new ArmModelContainerBotania());
        e.registerContainerIfModLoaded("alfheim", "FenrirChestplate", new ArmModelContainerBotania());
        e.registerContainerIfModLoaded("alfheim", "ElvoriumChestplate", new ArmModelContainerAlfheim());
    }

    @SubscribeEvent
    public void initBoneAdaptersEvent(InitBoneAdaptersEvent e) {
        e.registerAdapterIfModLoaded(BoneAdapterAlfheim.class, "alfheim");
    }

    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
