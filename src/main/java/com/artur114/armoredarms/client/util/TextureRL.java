package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.Immutable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

@Immutable
public class TextureRL implements ITexture {
    private final ResourceLocation tex;
    private final IPriority priority;

    public TextureRL(ResourceLocation tex, IPriority priority) {
        this.priority = priority;
        this.tex = tex;
    }

    public TextureRL(ResourceLocation tex) {
        this(tex, Priority.NORMAL);
    }

    @Override
    public void bind() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.tex);
    }

    @Override
    public void postBind() {}

    @Override
    public IPriority priority() {
        return this.priority;
    }
}