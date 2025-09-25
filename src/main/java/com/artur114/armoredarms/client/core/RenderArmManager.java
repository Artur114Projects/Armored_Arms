package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.util.Api;
import com.artur114.armoredarms.client.util.RMException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

@Api.Unstable
@SideOnly(Side.CLIENT)
public class RenderArmManager {
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public final Minecraft mc = Minecraft.getMinecraft();
    public Set<IArmRenderLayer> renderLayers = null;
    public boolean initTick = true;
    public boolean render = false;
    public boolean died = false;


    public void renderSpecificHandEvent(RenderSpecificHandEvent e) {
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
        if (e.phase != TickEvent.Phase.START || this.mc.player == null || this.mc.isGamePaused() || this.died) {
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

    public void tryRender(RenderSpecificHandEvent e) {
        EntityPlayerSP player = this.mc.player;
        EnumHandSide enumhandside = e.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float interpPitch = e.getInterpolatedPitch();
        float swingProgress = e.getSwingProgress();
        float equipProgress = e.getEquipProgress();
        ItemStack stack = e.getItemStack();
        EnumHand hand = e.getHand();

        GlStateManager.pushMatrix();
        boolean flag = false;

        if (stack.isEmpty()) {
            if (hand == EnumHand.MAIN_HAND) {
                this.renderArmFirstPerson(equipProgress, swingProgress, enumhandside);

                flag = true;
            }
        } else if (stack.getItem() instanceof ItemMap) {
            if (hand == EnumHand.MAIN_HAND && player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
                this.renderMapFirstPerson(interpPitch, equipProgress, swingProgress, stack);
            } else {
                this.renderMapFirstPersonSide(equipProgress, enumhandside, swingProgress, stack);
            }

            flag = true;
        }

        if (flag) {
            e.setCanceled(true);
        }

        GlStateManager.popMatrix();
    }

    public void tryTick(TickEvent.ClientTickEvent e) {
        AbstractClientPlayer player = this.mc.player;

        if (this.initTick) {
            try {
                this.init(player); this.initTick = false;
            } catch (Throwable exp) {
                throw new RMException("It was not possible to load RenderArmManager, custom hands will not be rendered!", exp).setFatal();
            }
        }

        boolean render = false;

        for (IArmRenderLayer layer : this.renderLayers) {

            try {
                layer.update(player);
            } catch (RMException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RMException(tr).setFatalLayer(layer);
            }

            render |= layer.needRender(player, this.render);
        }

        this.render = render;
    }

    public void init(AbstractClientPlayer player) {
        ArmoredArmsApi.InitRenderLayersEvent event = new ArmoredArmsApi.InitRenderLayersEvent();
        event.addLayer(ArmRenderLayerVanilla.class);
        event.addLayer(ArmRenderLayerArmor.class);
        MinecraftForge.EVENT_BUS.post(event);
        this.renderLayers = event.renderLayers();

        for (IArmRenderLayer layer : this.renderLayers) {
            try {
                layer.init(player);
            } catch (RMException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RMException(tr).setFatalLayer(layer);
            }
        }
    }

    public void onException(RMException exception) {
        if (exception.isFatal()) {
            this.died = true;
        }
        if (exception.isFatalOnLayer()) {
            this.renderLayers.remove(exception.fatalLayer());
        }

        exception.compileMessage();

        for (String message : exception.messageForPlayer()) {
            this.mc.player.sendMessage(new TextComponentTranslation(message).setStyle(new Style().setColor(TextFormatting.RED)));
        }

        exception.printStackTrace(System.err);
    }

    public void renderArms() {
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        this.transAndRenderArm(EnumHandSide.RIGHT);
        this.transAndRenderArm(EnumHandSide.LEFT);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }

    public float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
        return f;
    }

    public void transAndRenderArm(EnumHandSide enumHandSide) {
        GlStateManager.pushMatrix();
        float f = enumHandSide == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);

        this.renderArm(this.mc.player, enumHandSide);

        GlStateManager.popMatrix();
    }

    public void renderMapFirstPerson(float interPitch, float equipProgress, float swingProgress, ItemStack stack) {
        float f = MathHelper.sqrt(swingProgress);
        float f1 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
        GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = this.getMapAngleFromPitch(interPitch);
        GlStateManager.translate(0.0F, 0.04F + equipProgress * -1.2F + f3 * -0.5F, -0.72F);
        GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
        this.renderArms();
        float f4 = MathHelper.sin(f * (float)Math.PI);
        GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.renderMapFirstPerson(stack);
    }

    public void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack) {
        float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
        this.renderArmFirstPerson(p_187465_1_, p_187465_3_, hand);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
        float f1 = MathHelper.sqrt(p_187465_3_);
        float f2 = MathHelper.sin(f1 * (float)Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f5 = -0.3F * MathHelper.sin(p_187465_3_ * (float)Math.PI);
        GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
        GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
        this.renderMapFirstPerson(stack);
        GlStateManager.popMatrix();
    }

    public void renderMapFirstPerson(ItemStack stack) {
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.disableLighting();
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = ((ItemMap) stack.getItem()).getMapData(stack, this.mc.world);

        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }

        GlStateManager.enableLighting();
    }

    public void renderArmFirstPerson(float equipProgress, float swingProgress, EnumHandSide enumhandside) {
        float f = enumhandside != EnumHandSide.LEFT ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equipProgress * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        GlStateManager.disableCull();

        this.renderArm(this.mc.player, enumhandside);

        GlStateManager.enableCull();
    }

    public void renderArm(AbstractClientPlayer player, EnumHandSide handSide) {
        for (IArmRenderLayer layer : this.renderLayers) {
            if (layer.needRender(player, this.render)) {
                layer.renderTransformed(player, handSide);
            }
        }
    }
}
