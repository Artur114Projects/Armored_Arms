package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.modelrender.ArmModelContainerPlayer;
import com.artur114.armoredarms.client.modelrender.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.modelrender.ArmModelRendererPlayer;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractHandRenderLayer;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArmRenderLayerHand extends AbstractHandRenderLayer<ArmRenderLayerHand, AAItemStack, AbstractRenderEngineForge<?, ?>> {
    public final Minecraft mc = Minecraft.getMinecraft();
    public RenderPlayer renderPlayer = null;

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        super.init(engine, mod);
        this.renderPlayer = (RenderPlayer) engine.mc.getRenderManager().<AbstractClientPlayer>getEntityRenderObject(engine.mc.player);
    }

    @Override
    public AAItemStack currentChestPlate() {
        return AAItemStack.chestPlate(this.mc.player);
    }

    @Override
    public AAItemStack emptyStack() {
        return AAItemStack.EMPTY;
    }

    @Override
    public List<ShapelessLocation> initRenderWearList() {
        return AAConfig.Baked.renderWearList;
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
    @SuppressWarnings("unchecked")
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return (Class<AbstractRenderEngineForge<?,?>>) (Class<?>) AbstractRenderEngineForge.class;
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
