package com.artur114.armoredarms.client.integration.backhand;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.events.InitRenderPipelineEvent;
import com.artur114.armoredarms.client.integration.backhand.engine.ArmRenderEngineBackHand;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerBH implements IPreInitListener {
    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        ArmoredArmsApi.registerEngineIfModLoaded(ArmRenderEngineBackHand.class, "backhand");
    }
}
