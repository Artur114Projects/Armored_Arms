package com.artur114.armoredarms.main;

import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Config(modid = ArmoredArms.MODID)
@Mod.EventBusSubscriber(modid = ArmoredArms.MODID)
public class AAConfig {

    @Config.LangKey("armoredarms.cfg.disableArmWear")
    public static boolean disableArmWear = true;

    @Config.LangKey("armoredarms.cfg.enableArmWearWithVanillaM")
    public static boolean enableArmWearWithVanillaM = true;

    @Config.LangKey("armoredarms.cfg.renderBlackList")
    public static String[] renderBlackList = new String[0];

    @Config.LangKey("armoredarms.cfg.renderArmWearList")
    public static String[] renderArmWearList = new String[] {"cqrepoured:*"};

    @Config.RangeDouble(min = 0.0D, max = 10.0D)
    @Config.LangKey("armoredarms.cfg.vanillaArmorModelSize")
    public static double vanillaArmorModelSize = 0.4D;

    @Config.LangKey("armoredarms.cfg.useCheckByItem")
    @Config.Comment("will be more optimized, but in theory may cause visual bugs")
    public static boolean useCheckByItem = false;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ArmoredArms.MODID)) {
            ConfigManager.sync(ArmoredArms.MODID, Config.Type.INSTANCE);

            updateLocationList(Baked.renderWearList, renderArmWearList);
            updateLocationList(Baked.renderArmorBlackList, renderBlackList);
        }
    }

    protected static void init() {
        updateLocationList(Baked.renderWearList, renderArmWearList);
        updateLocationList(Baked.renderArmorBlackList, renderBlackList);
    }

    private static void updateLocationList(List<ShapelessLocation> list, String[] locations) {
        list.clear();
        for (String loc : locations) {
            if (loc.isEmpty()) {
                continue;
            }
            try {
                ShapelessLocation location = ShapelessLocation.location(loc);

                if (location != null && !location.isEmpty()) {
                    list.add(location);
                }
            } catch (Throwable t) {
                ArmoredArms.LOGGER.AA_LOG.warn("Failed to initialize location: [{}], check the syntax!", loc);
            }
        }
    }

    public static class Baked {
        public static final List<ShapelessLocation> renderArmorBlackList = new ArrayList<>();
        public static final List<ShapelessLocation> renderWearList = new ArrayList<>();
    }
}
