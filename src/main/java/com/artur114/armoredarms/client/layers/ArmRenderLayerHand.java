package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractHandRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
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
        return Collections.emptyList();
    }

    @Override
    public List<ShapelessLocation> initNoRenderWearList() {
        return Collections.emptyList();
    }

    @Override
    public List<IArmModelManager<?, ?>> initModelManager() {
        return Collections.emptyList();
    }

    @Override
    public List<IArmModelRenderContainer<?, ?>> initRenderContainers() {
        return Collections.emptyList();
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
