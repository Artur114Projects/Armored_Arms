package com.artur114.armoredarms.main;


import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

//@Config(modid = ArmoredArms.MODID)
public class AAConfig {
    public static Configuration config;

//    @Config.LangKey("armoredarms.cfg.disableArmWear")
    public static boolean disableArmWear = true;

//    @Config.LangKey("armoredarms.cfg.enableArmWearWithVanillaM")
    public static boolean enableArmWearWithVanillaM = true;

//    @Config.RequiresMcRestart
//    @Config.LangKey("armoredarms.cfg.renderBlackList")
    public static String[] renderBlackList = new String[0];

//    @Config.RequiresMcRestart
//    @Config.LangKey("armoredarms.cfg.renderArmWearList")
    public static String[] renderArmWearList = new String[] {"cqrepoured:*"};

//    @Config.RangeDouble(min = 0.0D, max = 10.0D)
//    @Config.LangKey("armoredarms.cfg.vanillaArmorModelSize")
    public static double vanillaArmorModelSize = 0.4D;

//    @Config.LangKey("armoredarms.cfg.useCheckByItem")
//    @Config.Comment("will be more optimized, but in theory may cause visual bugs")
    public static boolean useCheckByItem = false;

    private void sync() {
        disableArmWear = config.get("base", "disableArmWear", true).getBoolean();
        enableArmWearWithVanillaM = config.get("base", "enableArmWearWithVanillaM", true).getBoolean();
        renderBlackList = config.get("base", "renderBlackList", new String[0]).getStringList();
        renderArmWearList = config.get("base", "renderArmWearList", new String[0]).getStringList();
        vanillaArmorModelSize = config.get("base", "vanillaArmorModelSize", 0.4D).getDouble();
        useCheckByItem = config.get("base", "useCheckByItem", false).getBoolean();
        config.save();
    }

    public void configChangedEventOnConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.modID.equals(ArmoredArms.MODID)) {
            this.sync();
        }
    }

    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        config = new Configuration(e.getSuggestedConfigurationFile());
        config.load();
        this.sync();
    }
}
