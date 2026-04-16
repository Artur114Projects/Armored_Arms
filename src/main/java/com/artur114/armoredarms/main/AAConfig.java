package com.artur114.armoredarms.main;


import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.GenericPriority;
import com.artur114.armoredarms.client.util.IPreInitListener;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.*;

public class AAConfig implements IPreInitListener {
    public static Configuration config;

    public static String[] renderSourcesPriority = new String[0];
    public static String[] renderArmWearList = new String[0];
    public static String[] renderBlackList = new String[0];
    public static String[] noRenderArmWearList = new String[0];
    public static boolean enableArmWearWithVanillaM = true;
    public static double vanillaArmorModelSize = 0.4D;
    public static boolean useCheckByItem = false;
    public static boolean disableArmWear = true;

    private void sync() {
        renderBlackList = config.get("base", "renderBlackList", new String[0], "Blacklist of armor for rendering").getStringList();
        vanillaArmorModelSize = config.get("base", "vanillaArmorModelSize", 0.4D, "Vanilla armor model size").getDouble();
        useCheckByItem = config.get("base", "useCheckByItem", false, "Use check by item").getBoolean();

        renderSourcesPriority = config.get("base", "renderSourcesPriority", new String[] {"event"}, "Render sources priority").getStringList();
        noRenderArmWearList = config.get("base", "noRenderArmWearList", new String[0], "List of armors that require disable arm wear render").getStringList();
        renderArmWearList = config.get("base", "renderArmWearList", new String[0], "List of armors that require arm wear render. Works with the mod installed that ports new skins.").getStringList();
        enableArmWearWithVanillaM = config.get("base", "enableArmWearWithVanillaM", true, "Enable rendering arm wear for vanilla armor model. Works with the mod installed that ports new skins.").getBoolean();
        disableArmWear = config.get("base", "disableArmWear", true, "Disable rendering of arm wear with armor equipped. Works with the mod installed that ports new skins.").getBoolean();

        if (config.hasChanged()) {
            config.save();
        }

        Baked.reload();
    }

    public void fMLPreInitializationEvent(FMLPreInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(this);
        config = new Configuration(e.getSuggestedConfigurationFile());
        config.load();
        this.sync();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.modID.equals(ArmoredArms.MODID)) {
            this.sync();
        }
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
                default: return null;
            }
        }
    }

    public static class ConfigGuiFactory implements IModGuiFactory {

        @Override
        public void initialize(Minecraft minecraft) {

        }

        @Override
        public Class<? extends GuiScreen> mainConfigGuiClass() {
            return AAConfigGui.class;
        }

        @Override
        public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
            return null;
        }

        @Override
        public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement runtimeOptionCategoryElement) {
            return null;
        }
    }

    public static class AAConfigGui extends GuiConfig {

        public AAConfigGui(GuiScreen parentScreen) {
            super(parentScreen, new ConfigElement<>(AAConfig.config.getCategory("base")).getChildElements(), ArmoredArms.MODID, false, true, "ArmoredArms Configuration");
        }
    }
}
