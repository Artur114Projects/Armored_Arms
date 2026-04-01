package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineMixin;
import com.artur114.armoredarms.client.util.GenericPriority;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ArmoredArms.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AAConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DISABLE_ARM_WEAR = BUILDER
            .comment("Disable rendering of arm wear with armor equipped")
            .define("disableArmWear", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ARM_WEAR_WITH_VANILLA_M = BUILDER
            .comment("Enable rendering arm wear for vanilla armor model")
            .define("enableArmWearWithVanillaM", true);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RENDER_BLACK_LIST = BUILDER
            .comment("Blacklist of armor for rendering")
            .defineListAllowEmpty("renderBlackList", List.of(), AAConfig::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RENDER_ARM_WEAR_LIST = BUILDER
            .comment("List of armors that require arm wear render")
            .defineListAllowEmpty("renderArmWearList", List.of("iceandfire:*", "botania:*"), AAConfig::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_RENDER_ARM_WEAR_LIST = BUILDER
            .comment("List of armors that no require arm wear render, takes precedence over renderArmWearList")
            .defineListAllowEmpty("noRenderArmWearList", List.of("create:netherite_backtank"), AAConfig::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<Double> VANILLA_ARMOR_MODEL_SIZE = BUILDER
            .comment("Vanilla armor model size")
            .define("vanillaArmorModelSize", 0.4D);

    private static final ForgeConfigSpec.BooleanValue USE_CHECK_BY_ITEM = BUILDER
            .comment("Use check by item")
            .define("useCheckByItem", false);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RENDER_SOURCES_PRIORITY = BUILDER
            .comment("Different sources can work differently, choose the one that works better", "The higher the source is on the list, the higher its priority", "Deleting a render source will prevent it from working", "Changes are applied after restarting the game", "[event] - Standard rendering source, сan always work", "[mixin] - Additional rendering source, can work if mixins loader is installed")
            .defineListAllowEmpty("renderSourcesPriority", List.of("event", "mixin"), Baked::validateSourceName);


    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disableArmWear = false;
    public static boolean enableArmWearWithVanillaM = false;
    public static String[] renderBlackList = new String[0];
    public static String[] renderArmWearList = new String[0];
    public static String[] noRenderArmWearList = new String[0];
    public static String[] renderSourcesPriority = new String[0];
    public static double vanillaArmorModelSize = 0.4D;
    public static boolean useCheckByItem = false;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        disableArmWear = DISABLE_ARM_WEAR.get();
        enableArmWearWithVanillaM = ENABLE_ARM_WEAR_WITH_VANILLA_M.get();
        renderBlackList = RENDER_BLACK_LIST.get().toArray(new String[0]);
        noRenderArmWearList = RENDER_ARM_WEAR_LIST.get().toArray(new String[0]);
        noRenderArmWearList = NO_RENDER_ARM_WEAR_LIST.get().toArray(new String[0]);
        renderSourcesPriority = RENDER_SOURCES_PRIORITY.get().toArray(new String[0]);
        vanillaArmorModelSize = VANILLA_ARMOR_MODEL_SIZE.get();
        useCheckByItem = USE_CHECK_BY_ITEM.get();

        Baked.reload();
    }

    private static boolean validateItemName(final Object obj) {
        if (obj instanceof String str && !str.isEmpty()) {
            try {
                ShapelessLocation location = ShapelessLocation.location(str);
                if (location.isEmpty()) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static class Baked {
        public static List<ShapelessLocation> renderBlackList = new ArrayList<>();
        public static List<ShapelessLocation> renderArmWearList = new ArrayList<>();
        public static List<ShapelessLocation> noRenderArmWearList = new ArrayList<>();
        public static Map<Class<? extends IArmRenderPipeline<?>>, IPriority> pipelinesPriority = new HashMap<>();

        public static void reload() {
            renderBlackList.clear(); renderBlackList.addAll(ShapelessLocation.location(AAConfig.renderBlackList));
            renderArmWearList.clear(); renderArmWearList.addAll(ShapelessLocation.location(AAConfig.renderArmWearList));
            noRenderArmWearList.clear(); noRenderArmWearList.addAll(ShapelessLocation.location(AAConfig.noRenderArmWearList));

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
            return switch (id) {
                case "event" -> ArmRenderPipelineForge.class;
                case "mixin" -> ArmRenderPipelineMixin.class;
                default -> null;
            };
        }

        public static boolean validateSourceName(Object id) {
            if (!(id instanceof String)) {
                return false;
            }
            return switch ((String) id) {
                case "event", "mixin" -> true;
                default -> false;
            };
        }
    }
}
