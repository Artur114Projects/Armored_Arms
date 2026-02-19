package com.artur114.armoredarms.main;


import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.Set;

public class AAConfig {
    public static Configuration config;

    public static String[] renderArmWearList = new String[0];
    public static String[] renderBlackList = new String[0];
    public static boolean enableArmWearWithVanillaM = true;
    public static double vanillaArmorModelSize = 0.4D;
    public static boolean useCheckByItem = false;
    public static boolean disableArmWear = true;

    private void sync() {
        renderBlackList = config.get("base", "renderBlackList", new String[0], "Blacklist of armor for rendering").getStringList();
        vanillaArmorModelSize = config.get("base", "vanillaArmorModelSize", 0.4D, "Vanilla armor model size").getDouble();
        useCheckByItem = config.get("base", "useCheckByItem", false, "Use check by item").getBoolean();

        renderArmWearList = config.get("base", "renderArmWearList", new String[0], "List of armors that require arm wear render. Works with the mod installed that ports new skins.").getStringList();
        enableArmWearWithVanillaM = config.get("base", "enableArmWearWithVanillaM", true, "Enable rendering arm wear for vanilla armor model. Works with the mod installed that ports new skins.").getBoolean();
        disableArmWear = config.get("base", "disableArmWear", true, "Disable rendering of arm wear with armor equipped. Works with the mod installed that ports new skins.").getBoolean();

        if (config.hasChanged()) {
            config.save();
        }

        System.out.println("AA Configs Is synced");
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
