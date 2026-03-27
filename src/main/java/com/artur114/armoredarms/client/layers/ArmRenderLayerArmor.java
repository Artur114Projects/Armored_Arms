package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractArmorRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.SLContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;


import java.util.List;

public class ArmRenderLayerArmor extends AbstractArmorRenderLayer<ArmRenderLayerArmor, ItemStackAA, AbstractRenderEngineForge<?, ?>> {
    public final Minecraft mc = Minecraft.getInstance();
    public ArmRenderContext context = null;

    @Override
    public void tryRender(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        this.context = engine.renderContext;
        super.tryRender(engine, handSide);
    }

    @Override
    public ItemStackAA emptyStack() {
        return ItemStackAA.EMPTY;
    }

    @Override
    public ItemStackAA currentChestPlate() {
        if (this.mc.player == null) return ItemStackAA.EMPTY;
        return ItemStackAA.chestPlate(this.mc.player);
    }

    @Override
    public String messageForPlayer(String type) {
        return "armoredarms.error.layer.armor." + type;
    }

    @Override
    public List<ShapelessLocation> initBlackList() {
        return AAConfig.Backed.renderBlackList;
    }

    @Override
    public List<SLContainer<IArmModelManager<?, ?>>> initModelManagers() {
        InitModelManagersEvent event = new InitModelManagersEvent(ArmRenderLayerArmor.class, this.mod, true);
        event.registerManager(new ArmModelManagerArmor(), ShapelessLocation.ABSOLUTE);
        this.mod.post(event);
        return event.managersSL();
    }

    @Override
    public List<SLContainer<IArmModelRenderContainer<?, ?>>> initRenderContainers() {
        InitRenderContainersEvent event = new InitRenderContainersEvent(ArmRenderLayerArmor.class, this.mod, true);
        event.registerContainer(new ArmModelContainerArmor(ArmModelRendererArmor.class), ShapelessLocation.ABSOLUTE);
        this.mod.post(event);
        return event.containersSL();
    }

    @Override
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return AbstractRenderEngineForge.clazz();
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
