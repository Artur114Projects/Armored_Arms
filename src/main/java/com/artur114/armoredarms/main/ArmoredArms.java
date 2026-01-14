package com.artur114.armoredarms.main;

import com.artur114.armoredarms.api.AANonEventsApiProcessor;
import com.artur114.armoredarms.client.core.AAClientCommandsManager;
import com.artur114.armoredarms.client.core.RenderArmManager;
import com.artur114.armoredarms.client.integration.EventsRetranslators;
import com.artur114.armoredarms.client.integration.Overriders;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ArmoredArms.MODID, name = ArmoredArms.NAME, version = ArmoredArms.VERSION, guiFactory = ArmoredArms.GUI_FACTORY, useMetadata = true)
public class ArmoredArms {
    public static final int CHEST_PLATE_ID = 1;
    public static final RenderArmManager RENDER_ARM_MANAGER = new RenderArmManager();
    public static final AAConfig CONFIGS = new AAConfig();
    public static final String GUI_FACTORY = "com.artur114.armoredarms.main.AAConfig$ConfigGuiFactory";
    public static final String VERSION = "v1.2.0-1.7.10-release";
    public static final String MODID = "armoredarms";
    public static final String NAME = "Armored Arms";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        CONFIGS.fMLPreInitializationEvent(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        EventsRetranslators.init();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Overriders());
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(new AANonEventsApiProcessor());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void renderHand(RenderHandEvent e) {
        RENDER_ARM_MANAGER.renderHandEvent(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent e) {
        RENDER_ARM_MANAGER.tickEventClientTickEvent(e);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        CONFIGS.configChangedEventOnConfigChangedEvent(e);
    }
}
