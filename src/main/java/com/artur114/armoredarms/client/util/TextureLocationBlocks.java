package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.ITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;

public class TextureLocationBlocks implements ITexture {
    private static final TextureLocationBlocks[] INSTANCES = new TextureLocationBlocks[] {new TextureLocationBlocks(Priority.LOWEST), new TextureLocationBlocks(Priority.LOW), new TextureLocationBlocks(Priority.NORMAL), new TextureLocationBlocks(Priority.HIGH), new TextureLocationBlocks(Priority.HIGHEST)};
    public static final TextureLocationBlocks INSTANCE = new TextureLocationBlocks(Priority.NORMAL);
    public static TextureLocationBlocks fromPriority(Priority priority) {
        if (priority.ordinal() > INSTANCES.length) return null;
        return INSTANCES[priority.ordinal()];
    }

    private final Priority priority;
    private TextureLocationBlocks(Priority priority) {
        this.priority = priority;
    }

    @Override
    public void bind() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    @Override
    public void postBind() {}

    @Override
    public IPriority priority() {
        return this.priority;
    }
}
