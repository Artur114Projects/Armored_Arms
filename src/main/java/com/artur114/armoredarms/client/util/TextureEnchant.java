package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.util.ITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class TextureEnchant implements ITexture {
    public static final ITexture FIRST = new TextureEnchant(0);
    public static final ITexture SECOND = new TextureEnchant(1);
    public static final ITexture[] ALL = new ITexture[] {FIRST, SECOND};

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final int i;

    private TextureEnchant(int i) {
        this.i = i;
    }

    @Override
    public void bind() {
        float f = (float) this.mc.player.ticksExisted;
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        this.mc.getTextureManager().bindTexture(ENCHANTED_ITEM_GLINT_RES);
        this.mc.entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
        GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
        GlStateManager.matrixMode(5888);
    }

    @Override
    public void postBind() {
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        this.mc.entityRenderer.setupFogColor(false);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    @Override
    public IPriority priority() {
        return () -> -10001 - i;
    }
}
