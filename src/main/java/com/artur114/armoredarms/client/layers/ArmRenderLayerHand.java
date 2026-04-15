package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.modelrender.player.ArmModelContainerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelRendererPlayer;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractHandRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

import java.util.Collections;
import java.util.List;

public class ArmRenderLayerHand extends AbstractHandRenderLayer<ArmRenderLayerHand, ItemStackAA, AbstractRenderEngineForge<?, ?>> {
    public final Minecraft mc = Minecraft.getMinecraft();
    public RenderPlayer renderPlayer = null;

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);

        super.init(engine, mod);
    }

    @Override
    public void tryTick(AbstractRenderEngineForge<?, ?> engine) {
        this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);

        super.tryTick(engine);
    }

    @Override
    public ItemStackAA currentChestPlate() {
        return ItemStackAA.chestPlate(this.mc.thePlayer);
    }

    @Override
    public ItemStackAA emptyStack() {
        return ItemStackAA.EMPTY;
    }

    @Override
    public List<ShapelessLocation> initRenderWearList() {
        return AAConfig.Baked.renderWearList;
    }

    @Override
    public List<ShapelessLocation> initNoRenderWearList() {
        return AAConfig.Baked.noRenderWearList;
    }

    @Override
    public List<IArmModelManager<?, ?>> initModelManager() {
        InitModelManagersEvent event = new InitModelManagersEvent(this.getClass(), this.mod, false);
        event.registerManager(new ArmModelManagerPlayer());
        this.mod.post(event);
        return event.managers();
    }

    @Override
    public List<IArmModelRenderContainer<?, ?>> initRenderContainers() {
        InitRenderContainersEvent event = new InitRenderContainersEvent(this.getClass(), this.mod, false);
        event.registerContainer(new ArmModelContainerPlayer(ArmModelRendererPlayer.class));
        this.mod.post(event);
        return event.containers();
    }

    @Override
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return AbstractRenderEngineForge.clazz();
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
