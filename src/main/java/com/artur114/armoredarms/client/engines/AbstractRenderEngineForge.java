package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderHandEvent;

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngineForge<?, ?>, P extends IArmRenderPipeline<?>> extends AbstractRenderEngine<E, P> {
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public final Minecraft mc = Minecraft.getMinecraft();
    public ItemRenderer itemRenderer = null;
    public RenderItem renderItem = null;
    public boolean deactivated = false;
    public boolean render = false;
    public IAAModContainer mod = null;
    public P pipeline = null;;

    @Override
    public void init(P context, IAAModContainer mod) {
        this.pipeline = context;
        this.mod = mod;
        this.renderItem = this.mc.getRenderItem();
        this.itemRenderer = this.mc.getItemRenderer();
        super.init(context, mod);
    }

    @Override
    public void tryRender(P context) {
        if (this.deactivated || !this.render) {
            return;
        }

        this.render(context);
    }

    @Override
    public void tryTick(P context) {
        if (this.deactivated) {
            return;
        }

        this.tick(context);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return true;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    public abstract void render(P context);
    public abstract void tick(P context);

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderEngineForge<?, ?>> clazz() {
        return (Class<AbstractRenderEngineForge<?, ?>>) (Class<?>) AbstractRenderEngineForge.class;
    }
}
