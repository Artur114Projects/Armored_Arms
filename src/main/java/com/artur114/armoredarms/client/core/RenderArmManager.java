package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.api.events.AARenderLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.util.RMException;
import com.artur114.armoredarms.client.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderArmManager {
    public Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers = null;
    public HumanoidArmorModel<AbstractClientPlayer> actualHumanoidModel = null;
    public final Minecraft mc = Minecraft.getInstance();
    public double lastModelSize = Double.MIN_VALUE;
    public ItemRenderer itemRenderer = null;
    public boolean initTick = true;
    public boolean render = false;
    public boolean died = false;


    public void renderArmEvent(RenderArmEvent e) {
        if (this.died || !this.render) {
            return;
        }

        try {
            this.tryRender(e);
        } catch (RMException rm) {
            this.onException(rm.setMethod(RMException.Method.RENDER));
        } catch (Throwable exp) {
            this.onException(new RMException(exp).setMethod(RMException.Method.RENDER));
        }
    }

    public void tickEventClientTickEvent(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.START || this.mc.player == null || this.mc.isPaused() || this.died) {
            return;
        }

        try {
            this.tryTick(e);
        } catch (RMException rm) {
            this.onException(rm.setMethod(RMException.Method.TICK));
        } catch (Throwable exp) {
            this.onException(new RMException(exp).setMethod(RMException.Method.TICK));
        }
    }

    public void tryRender(RenderArmEvent e) {
        this.renderArm(e.getPoseStack(), e.getMultiBufferSource(), e.getPlayer(), e.getArm(), e.getPackedLight()); e.setCanceled(true);
    }

    public void tryTick(TickEvent.ClientTickEvent e) {
        LocalPlayer player = this.mc.player;

        if (this.initTick) {
            try {
                this.init(player); this.initTick = false;
            } catch (Throwable exp) {
                throw new RMException("It was not possible to load RenderArmManager, custom hands will not be rendered!", exp).setFatal();
            }
        }

        if (this.lastModelSize != AAConfig.vanillaArmorModelSize) {
            this.actualHumanoidModel = new HumanoidArmorModel<>(HumanoidArmorModel.createBodyLayer(new CubeDeformation((float) AAConfig.vanillaArmorModelSize)).getRoot().bake(64, 32));
        }

        boolean render = false;

        for (IArmRenderLayer layer : this.renderLayers.values()) {

            try {
                layer.update(player);
            } catch (RMException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RMException(tr).setFatalLayer(layer);
            }

            render |= layer.needRender(player, render);
        }

        this.render = render;
    }

    @SuppressWarnings("unchecked")
    public <T extends IArmRenderLayer> T getLayer(Class<T> tClass) {
        return (T) this.renderLayers.get(tClass);
    }

    public boolean killLayer(Class<? extends IArmRenderLayer> tClass) {
        return this.renderLayers.remove(tClass) != null;
    }

    public void init(LocalPlayer player) {
        InitRenderLayersEvent event = new InitRenderLayersEvent();
        event.addLayer(ArmRenderLayerVanilla.class);
        event.addLayer(ArmRenderLayerArmor.class);
        MinecraftForge.EVENT_BUS.post(event);
        this.renderLayers = event.renderLayers();

        for (IArmRenderLayer layer : this.renderLayers.values()) {
            try {
                layer.init(player);
            } catch (RMException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RMException(tr).setFatalLayer(layer);
            }
        }

        this.itemRenderer = this.mc.getItemRenderer();
    }

    public void onException(RMException exception) {
        if (exception.isFatal()) {
            this.died = true;
        }
        if (exception.isFatalOnLayer()) {
            this.renderLayers.remove(exception.fatalLayer().getClass());
        }

        for (Component message : exception.messageForPlayer()) {
//            this.mc.player.displayClientMessage(message.getSiblings().set(new Style().setColor(TextFormatting.RED)), false);
        }

        exception.printStackTrace(System.err);
    }

    public void renderArm(PoseStack poseStack, MultiBufferSource buffer, AbstractClientPlayer player, HumanoidArm side, int combinedLight) {
        IArmRenderLayer vanilla = this.getLayer(ArmRenderLayerVanilla.class); // spaghetti!

        if (vanilla.needRender(player, this.render)) {
            if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, side, this.render))) {
                vanilla.renderTransformed(poseStack, buffer, player, side, combinedLight);
            }
        }

        for (IArmRenderLayer layer : this.renderLayers.values()) {
            if (layer == vanilla) {
                continue;
            }
            if (layer.needRender(player, this.render)) {
                try {
                    if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, side, this.render))) {
                        layer.renderTransformed(poseStack, buffer, player, side, combinedLight);
                    }
                } catch (RMException rm) {
                    throw rm;
                } catch (Throwable tr) {
                    throw new RMException(tr).setFatalLayer(layer);
                }
            }
        }
    }
}
