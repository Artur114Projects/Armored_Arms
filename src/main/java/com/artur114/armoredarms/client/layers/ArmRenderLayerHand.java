package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.modelrender.player.ArmModelContainerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelRendererPlayer;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractHandRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;

import java.util.List;

public class ArmRenderLayerHand extends AbstractHandRenderLayer<ArmRenderLayerHand, ItemStackAA, AbstractRenderEngineForge<?, ?>> {
    public List<ShapelessLocation> noRenderArmWearList;
    public final Minecraft mc = Minecraft.getInstance();
    public ArmRenderContext context = null;

    @Override
    public void tryRender(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        if (this.mc.player != null && this.mc.player.isInvisible()) return;
        this.context = engine.renderContext;
        super.tryRender(engine, handSide);
    }

    @Override
    public ItemStackAA currentChestPlate() {
        if (this.mc.player == null) return ItemStackAA.EMPTY;
        return ItemStackAA.chestPlate(this.mc.player);
    }

    @Override
    public ItemStackAA emptyStack() {
        return ItemStackAA.EMPTY;
    }

    @Override
    public List<ShapelessLocation> initRenderWearList() {
        this.noRenderArmWearList = AAConfig.Backed.noRenderArmWearList;
        return AAConfig.Backed.renderArmWearList;
    }

    @Override
    public List<IArmModelManager<?, ?>> initModelManager() {
        InitModelManagersEvent event = new InitModelManagersEvent(ArmRenderLayerHand.class, this.mod, false);
        event.registerManager(new ArmModelManagerPlayer());
        this.mod.post(event);
        return event.managers();
    }

    @Override
    public List<IArmModelRenderContainer<?, ?>> initRenderContainers() {
        InitRenderContainersEvent event = new InitRenderContainersEvent(ArmRenderLayerHand.class, this.mod, false);
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
