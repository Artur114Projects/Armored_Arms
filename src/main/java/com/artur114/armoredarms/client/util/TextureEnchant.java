package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.util.ITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureEnchant implements ITexture {
    public static final ITexture FIRST = new TextureEnchant(0);
    public static final ITexture SECOND = new TextureEnchant(1);
    public static final ITexture[] ALL = new ITexture[] {FIRST, SECOND};

    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final int i;

    private TextureEnchant(int i) {
        this.i = i;
    }

    @Override
    public void bind() {
        GL11.glPushMatrix();
        float f8 = (float) this.mc.thePlayer.ticksExisted;
        this.mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
        GL11.glEnable(3042);
        float f9 = 0.5F;
        GL11.glColor4f(f9, f9, f9, 1.0F);
        GL11.glDepthFunc(514);
        GL11.glDepthMask(false);

        GL11.glDisable(2896);
        float f10 = 0.76F;
        GL11.glColor4f(0.5F * f10, 0.25F * f10, 0.8F * f10, 1.0F);
        GL11.glBlendFunc(768, 1);
        GL11.glMatrixMode(5890);
        GL11.glLoadIdentity();
        float f11 = f8 * (0.001F + (float) i * 0.003F) * 20.0F;
        float f12 = 0.33333334F;
        GL11.glScalef(f12, f12, f12);
        GL11.glRotatef(30.0F - (float) i * 60.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, f11, 0.0F);
        GL11.glMatrixMode(5888);
    }

    @Override
    public void postBind() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glMatrixMode(5890);
        GL11.glDepthMask(true);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glEnable(2896);
        GL11.glDisable(3042);
        GL11.glDepthFunc(515);
        GL11.glPopMatrix();
    }

    @Override
    public IPriority priority() {
        return () -> -10001 - i;
    }
}