package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.core.AAClientCommandsManager;
import com.artur114.armoredarms.client.core.RenderArmManager;
import com.artur114.armoredarms.client.integration.EventsRetranslators;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderHandEvent;

@Mod(modid = ArmoredArms.MODID, name = ArmoredArms.NAME, version = ArmoredArms.VERSION, useMetadata = true)
public class ArmoredArms {
    public static final AAClientCommandsManager AA_CLIENT_COMMANDS_MANAGER = new AAClientCommandsManager();
    public static final RenderArmManager RENDER_ARM_MANAGER = new RenderArmManager();
    public static final String VERSION = "v1.0.0-1.7.10-release";
    public static final String MODID = "armoredarms";
    public static final String NAME = "Armored Arms";

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        EventsRetranslators.init();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderHand(RenderHandEvent e) {
        RENDER_ARM_MANAGER.renderHandEvent(e);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void clientTick(TickEvent.ClientTickEvent e) {
        RENDER_ARM_MANAGER.tickEventClientTickEvent(e);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void clientChat(ClientChatEvent e) {
        AA_CLIENT_COMMANDS_MANAGER.clientChatEvent(e);
    }
}
