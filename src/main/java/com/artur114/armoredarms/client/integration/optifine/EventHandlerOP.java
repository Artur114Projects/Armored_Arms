package com.artur114.armoredarms.client.integration.optifine;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.integration.backhand.engine.ArmRenderEngineBackHand;
import com.artur114.armoredarms.client.integration.optifine.engine.ArmRenderEngineOptBackHand;
import com.artur114.armoredarms.client.integration.optifine.engine.ArmRenderEngineOptifine;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class EventHandlerOP implements IPreInitListener {
    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        ArmoredArmsApi.registerEngine(new ArmRenderEngineOptifine());
        ArmoredArmsApi.registerEngineIfModLoaded(ArmRenderEngineOptBackHand.class, "backhand");
    }
}
