package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.RenderArmManager;
import com.artur114.armoredarms.client.util.Overriders;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
@Mod(modid = ArmoredArms.MODID, name = ArmoredArms.NAME, version = ArmoredArms.VERSION, useMetadata = true)
public class ArmoredArms {
    public static final RenderArmManager RENDER_ARM_MANAGER = new RenderArmManager();
    public static final String VERSION = "v1.2.0-release";
    public static final String MODID = "armoredarms";
    public static final String NAME = "Armored Arms";

    @Mod.EventHandler
    public static void Init(FMLInitializationEvent event) {
        RENDER_ARM_MANAGER.addToBlackList(AAConfig.renderBlackList);
        Overriders.init();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderHand(RenderSpecificHandEvent e) {
        RENDER_ARM_MANAGER.renderSpecificHandEvent(e);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void clientTick(TickEvent.ClientTickEvent e) {
        RENDER_ARM_MANAGER.tickEventClientTickEvent(e);
    }
}
