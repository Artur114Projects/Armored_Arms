package com.artur114.armoredarms.main;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ArmoredArms.MODID)
@Mod.EventBusSubscriber(modid = ArmoredArms.MODID)
public class AAConfig {
    @Config.LangKey("armoredarms.cfg.disableArmWear")
    public static boolean disableArmWear = true;

    @Config.RequiresMcRestart
    @Config.LangKey("armoredarms.cfg.renderBlackList")
    public static String[] renderBlackList = new String[] {"minecraft:*"};

    @Config.RangeDouble(min = 0.0D, max = 10.0D)
    @Config.LangKey("armoredarms.cfg.vanillaArmorModelSize")
    public static double vanillaArmorModelSize = 0.4D;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ArmoredArms.MODID)) {
            ConfigManager.sync(ArmoredArms.MODID, Config.Type.INSTANCE);
        }
    }
}
