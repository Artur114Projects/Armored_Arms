package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureRLRGB extends TextureRL {
    private final int color;

    public TextureRLRGB(ResourceLocation tex, IPriority priority, int color) {
        super(tex, priority);
        this.color = color;
    }

    public TextureRLRGB(ResourceLocation tex, int color) {
        this(tex, Priority.NORMAL, color);
    }

    @Override
    public void bind() {
        float r = (float) (this.color >> 16 & 255) / 255.0F;
        float g = (float) (this.color >> 8 & 255) / 255.0F;
        float b = (float) (this.color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
        super.bind();
    }

    @Override
    public void postBind() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}