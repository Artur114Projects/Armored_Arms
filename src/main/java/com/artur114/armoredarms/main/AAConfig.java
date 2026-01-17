package com.artur114.armoredarms.main;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

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

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disableArmWear = true;
    public static boolean enableArmWearWithVanillaM = true;
    public static String[] renderBlackList = new String[0];
    public static String[] renderArmWearList = new String[] {"iceandfire:*", "botania:*"};
    public static String[] noRenderArmWearList = new String[] {"create:netherite_backtank"};
    public static double vanillaArmorModelSize = 0.4D;
    public static boolean useCheckByItem = false;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        disableArmWear = DISABLE_ARM_WEAR.get();
        enableArmWearWithVanillaM = ENABLE_ARM_WEAR_WITH_VANILLA_M.get();
        renderBlackList = RENDER_BLACK_LIST.getPath().toArray(new String[0]);
        noRenderArmWearList = RENDER_ARM_WEAR_LIST.getPath().toArray(new String[0]);
        noRenderArmWearList = NO_RENDER_ARM_WEAR_LIST.getPath().toArray(new String[0]);
        vanillaArmorModelSize = VANILLA_ARMOR_MODEL_SIZE.get();
        useCheckByItem = USE_CHECK_BY_ITEM.get();
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String;
    }
}
