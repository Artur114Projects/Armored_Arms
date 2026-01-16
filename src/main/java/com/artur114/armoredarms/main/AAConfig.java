package com.artur114.armoredarms.main;

//@Config(modid = ArmoredArms.MODID)
//@Mod.EventBusSubscriber(modid = ArmoredArms.MODID)
public class AAConfig {
//    @Config.LangKey("armoredarms.cfg.disableArmWear")
    public static boolean disableArmWear = true;

//    @Config.LangKey("armoredarms.cfg.enableArmWearWithVanillaM")
    public static boolean enableArmWearWithVanillaM = true;

//    @Config.RequiresMcRestart
//    @Config.LangKey("armoredarms.cfg.renderBlackList")
    public static String[] renderBlackList = new String[0];

//    @Config.RequiresMcRestart
//    @Config.LangKey("armoredarms.cfg.renderArmWearList")
    public static String[] renderArmWearList = new String[] {"iceandfire:*"};

//    @Config.RequiresMcRestart
//    @Config.LangKey("armoredarms.cfg.renderArmWearList")
    public static String[] noRenderArmWearList = new String[] {"create:netherite_backtank"};

//    @Config.RangeDouble(min = 0.0D, max = 10.0D)
//    @Config.LangKey("armoredarms.cfg.vanillaArmorModelSize")
    public static double vanillaArmorModelSize = 0.4D;

//    @Config.LangKey("armoredarms.cfg.useCheckByItem")
//    @Config.Comment("will be more optimized, but in theory may cause visual bugs")
    public static boolean useCheckByItem = false;

//    @SubscribeEvent
//    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
//        if (event.getModID().equals(ArmoredArms.MODID)) {
//            ConfigManager.sync(ArmoredArms.MODID, Config.Type.INSTANCE);
//        }
//    }
}
