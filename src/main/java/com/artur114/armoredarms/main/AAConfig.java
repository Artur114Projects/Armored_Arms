package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineCleanRoom;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.GenericPriority;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Config.LangKey("armoredarms.cfg.noRenderArmWearList")
    public static String[] noRenderArmWearList = new String[0];

    @Config.RequiresMcRestart
    @Config.Comment(value = {"Different sources can work differently, choose the one that works better", "The higher the source is on the list, the higher its priority", "Deleting a render source will prevent it from working", "Changes are applied after restarting the game", "[event] - Standard rendering source, сan always work", "[cleanroom] - Additional rendering source, can work if the mod is running on the cleanroom mod loader"})
    @Config.LangKey("armoredarms.cfg.renderSourcesPriority")
    public static String[] renderSourcesPriority = new String[] {"cleanroom", "event"};

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

            Baked.reload();
        }
    }

    protected static void init() {
        Baked.reload();
    }

    public static class Baked {
        public static final Map<Class<? extends IArmRenderPipeline<?>>, IPriority> pipelinesPriority = new HashMap<>();
        public static final List<ShapelessLocation> renderArmorBlackList = new ArrayList<>();
        public static final List<ShapelessLocation> noRenderWearList = new ArrayList<>();
        public static final List<ShapelessLocation> renderWearList = new ArrayList<>();

        public static void reload() {
            renderArmorBlackList.clear(); renderArmorBlackList.addAll(ShapelessLocation.location(AAConfig.renderBlackList));
            noRenderWearList.clear(); noRenderWearList.addAll(ShapelessLocation.location(AAConfig.noRenderArmWearList));
            renderWearList.clear(); renderWearList.addAll(ShapelessLocation.location(AAConfig.renderArmWearList));

            reloadPipelinesPriority();
        }

        private static void reloadPipelinesPriority() {
            pipelinesPriority.clear();

            for (int i = 0; i != renderSourcesPriority.length; i++) {
                Class<? extends IArmRenderPipeline<?>> clazz = classFromId(renderSourcesPriority[i]);
                if (clazz != null) {
                    pipelinesPriority.put(clazz, new GenericPriority(-i));
                }
            }
        }

        private static Class<? extends IArmRenderPipeline<?>> classFromId(String id) {
            switch (id) {
                case "event": return ArmRenderPipelineForge.class;
                case "cleanroom": return ArmRenderPipelineCleanRoom.class;
                default: return null;
            }
        }
    }
}
