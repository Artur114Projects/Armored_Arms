package com.artur114.armoredarms.client.integration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class EventsRetranslators {
    public static void init() {
        if (Loader.isModLoaded("cyberware")) {
            MinecraftForge.EVENT_BUS.register(new EventsRetranslatorCyberware());
        }
    }

}
