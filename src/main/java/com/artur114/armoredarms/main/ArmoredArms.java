package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.core.AAClientCommandsManager;
import com.artur114.armoredarms.client.core.RenderArmManager;
import com.artur114.armoredarms.client.integration.EventsRetranslators;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
@Mod(modid = ArmoredArms.MODID, name = ArmoredArms.NAME, version = ArmoredArms.VERSION, useMetadata = true, clientSideOnly = true)
public class ArmoredArms {
    public static final AAClientCommandsManager AA_CLIENT_COMMANDS_MANAGER = new AAClientCommandsManager();
    public static final RenderArmManager RENDER_ARM_MANAGER = new RenderArmManager();
    public static final String VERSION = "v1.4.2-1.12.2-release";
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
