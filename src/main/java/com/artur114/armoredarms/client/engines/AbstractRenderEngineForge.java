package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.ArmsBone;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngine<?, ?>, P extends IArmRenderPipeline<?>> extends AbstractRenderEngine<E, P> {
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public final RenderBlocks renderBlocksIr = new RenderBlocks();
    public final Minecraft mc = Minecraft.getMinecraft();
    public EntityRenderer entityRenderer = null;
    public ItemRenderer itemRenderer = null;

    @Override
    public void tryTick(P context) {
        super.tryTick(context);
    }

    @Override
    public void init(P context, IAAModContainer mod) {
        this.entityRenderer = this.mc.entityRenderer;
        this.itemRenderer = this.entityRenderer.itemRenderer;
        super.init(context, mod);
    }

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderEngineForge<?, ?>> clazz() {
        return (Class<AbstractRenderEngineForge<?, ?>>) (Class<?>) AbstractRenderEngineForge.class;
    }
}
