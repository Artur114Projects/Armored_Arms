package com.artur114.armoredarms.client.integration.battlegear2;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.integration.battlegear2.engine.ArmRenderEngineBattleGear2;
import com.artur114.armoredarms.client.util.IPreInitListener;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class EventHandlerBG implements IPreInitListener {
    @Override
    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        ArmoredArmsApi.registerEngineIfModLoaded(ArmRenderEngineBattleGear2.class, "battlegear2");
    }
}
