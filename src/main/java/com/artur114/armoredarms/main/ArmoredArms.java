package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.core.RenderArmManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

@Mod.EventBusSubscriber
@Mod(ArmoredArms.MODID)
public class ArmoredArms {
//    public static final AAClientCommandsManager AA_CLIENT_COMMANDS_MANAGER = new AAClientCommandsManager();
    public static final RenderArmManager RENDER_ARM_MANAGER = new RenderArmManager();
    public static final String MODID = "armoredarms";

    public ArmoredArms() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AAConfig.SPEC);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void renderHand(RenderArmEvent e) {
        RENDER_ARM_MANAGER.renderArmEvent(e);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientTick(TickEvent.ClientTickEvent e) {
        RENDER_ARM_MANAGER.tickEventClientTickEvent(e);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientChat(ClientChatEvent e) {
//        AA_CLIENT_COMMANDS_MANAGER.clientChatEvent(e);
    }
}
