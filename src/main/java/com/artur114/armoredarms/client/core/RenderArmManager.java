package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.api.events.AARenderLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.util.RMException;
import com.artur114.armoredarms.client.util.Reflector;
import com.google.common.base.MoreObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderArmManager {
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers = null;
    public final Minecraft mc = Minecraft.getMinecraft();
    public ItemRenderer itemRenderer = null;
    public boolean initTick = true;
    public boolean render = false;
    public boolean died = false;


    public void renderHandEvent(RenderHandEvent e) {
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

    public void tryRender(RenderHandEvent e) {
        boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
        boolean flag1 = this.itemRenderer.itemStackMainHand.isEmpty() || this.itemRenderer.itemStackMainHand.getItem() instanceof ItemMap || this.itemRenderer.itemStackOffHand.getItem() instanceof ItemMap;
        if (flag1 && this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator()) {
            this.mc.entityRenderer.enableLightmap();
            this.renderItemInFirstPerson(e.getPartialTicks());
            this.mc.entityRenderer.disableLightmap();
            e.setCanceled(true);
        }
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

    public void init(AbstractClientPlayer player) {
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

        this.itemRenderer = Reflector.getItemRenderer(this.mc);
    }

    public void onException(RMException exception) {
        if (exception.isFatal()) {
            this.died = true;
        }
        if (exception.isFatalOnLayer()) {
            this.renderLayers.remove(exception.fatalLayer().getClass());
        }

        for (ITextComponent message : exception.messageForPlayer()) {
            this.mc.player.sendMessage(message.setStyle(new Style().setColor(TextFormatting.RED)));
        }

        exception.printStackTrace(System.err);
    }

    public void renderItemInFirstPerson(float partialTicks) {
        AbstractClientPlayer abstractclientplayer = this.mc.player;
        float f = abstractclientplayer.getSwingProgress(partialTicks);
        EnumHand enumhand = MoreObjects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
        float interpPitch = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float interpYaw = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        boolean flag = true;
        boolean flag1 = true;

        if (abstractclientplayer.isHandActive()) {
            ItemStack itemstack = abstractclientplayer.getActiveItemStack();

            if (itemstack.getItem() instanceof net.minecraft.item.ItemBow) {
                flag = abstractclientplayer.getActiveHand() == EnumHand.MAIN_HAND;
                flag1 = !flag;
            }
        }

        this.itemRenderer.rotateArroundXAndY(interpPitch, interpYaw);
        this.itemRenderer.setLightmap();
        this.itemRenderer.rotateArm(partialTicks);
        GlStateManager.enableRescaleNormal();

        if (flag) {
            float swingProgress = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
            float equipProgress = 1.0F - (this.itemRenderer.prevEquippedProgressMainHand + (this.itemRenderer.equippedProgressMainHand - this.itemRenderer.prevEquippedProgressMainHand) * partialTicks);
            this.callRenderNotTransformed(abstractclientplayer, partialTicks, interpPitch, EnumHand.MAIN_HAND, swingProgress, this.itemRenderer.itemStackMainHand, equipProgress);
            this.renderItemInFirstPerson(abstractclientplayer, partialTicks, interpPitch, EnumHand.MAIN_HAND, swingProgress, this.itemRenderer.itemStackMainHand, equipProgress);
        }

        if (flag1) {
            float swingProgress = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
            float equipProgress = 1.0F - (this.itemRenderer.prevEquippedProgressOffHand + (this.itemRenderer.equippedProgressOffHand - this.itemRenderer.prevEquippedProgressOffHand) * partialTicks);
            this.callRenderNotTransformed(abstractclientplayer, partialTicks, interpPitch, EnumHand.OFF_HAND, swingProgress, this.itemRenderer.itemStackOffHand, equipProgress);
            this.renderItemInFirstPerson(abstractclientplayer, partialTicks, interpPitch, EnumHand.OFF_HAND, swingProgress, this.itemRenderer.itemStackOffHand, equipProgress);
        }

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    public void callRenderNotTransformed(AbstractClientPlayer player, float partialTicks, float interpPitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {
        for (IArmRenderLayer renderLayer : this.renderLayers.values()) {
            renderLayer.renderNotTransformed(player, partialTicks, interpPitch, hand, swingProgress, stack, equipProgress);
        }
    }

    public void renderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float interpPitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {
        EnumHandSide enumhandside = hand == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();

        GlStateManager.pushMatrix();

        if (stack.isEmpty()) {
            if (hand == EnumHand.MAIN_HAND) {
                this.renderArmFirstPerson(equipProgress, swingProgress, enumhandside);
            }
        } else if (stack.getItem() instanceof ItemMap) {
            if (hand == EnumHand.MAIN_HAND && player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
                this.renderMapFirstPerson(interpPitch, equipProgress, swingProgress, stack);
            } else {
                this.renderMapFirstPersonSide(equipProgress, enumhandside, swingProgress, stack);
            }

        }

        GlStateManager.popMatrix();
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
        IArmRenderLayer vanilla = this.getLayer(ArmRenderLayerVanilla.class); // spaghetti!

        if (vanilla.needRender(player, this.render)) {
            if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, handSide, this.render))) {
                vanilla.renderTransformed(player, handSide);
            }
        }

        for (IArmRenderLayer layer : this.renderLayers.values()) {
            if (layer == vanilla) {
                continue;
            }
            if (layer.needRender(player, this.render)) {
                try {
                    if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, handSide, this.render))) {
                        layer.renderTransformed(player, handSide);
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
