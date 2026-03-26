package com.artur114.armoredarms.client.modelrender.player;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextPlayer;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class ArmModelManagerPlayer implements IArmModelManager<ArmModelManagerPlayer, ArmRenderLayerHand> {
    protected boolean deactivated = false;
    public final Minecraft mc = Minecraft.getInstance();
    public ItemStackAA chestPlate = ItemStackAA.EMPTY;
    public PlayerModel<AbstractClientPlayer> model;
    public MultiModelRenderContext context;
    public PlayerRenderer renderPlayer;
    public boolean shouldRenderWear;

    @Override
    public void update(ArmRenderLayerHand layer) {
        ItemStackAA stack = layer.currentChestPlate;
        this.shouldRenderWear = !layer.noRenderArmWearList.contains(stack.location()) && (!AAConfig.disableArmWear || layer.renderArmWearList.contains(stack.location()) || (AAConfig.enableArmWearWithVanillaM && stack.isHumanoid(layer.engine)));
    }

    @Override
    public void render(ArmRenderLayerHand layer, IArmModelRenderer<ArmModelManagerPlayer> renderer, EnumHandSideAA side) {
        if (this.mc.player == null) return;
        this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.mc.player);
        this.context.prepare(layer.context.multiBufferSource, layer.context.poseStack, layer.context.packedLight);
        this.chestPlate = layer.currentChestPlate;
        this.model = this.renderPlayer.getModel();

        renderer.renderArm(this, side);

        this.renderPlayer = null;
        this.chestPlate = null;
        this.model = null;
    }

    @Override
    public void load(ArmRenderLayerHand layer) {
        this.context = new MultiModelRenderContext(new ModelRenderContextPlayer());
    }

    @Override
    public void unload(ArmRenderLayerHand layer) {}

    @Override
    public IArmModelRenderer<ArmModelManagerPlayer> cacheRenderer(ArmRenderLayerHand layer, IArmModelRenderContainer<ArmRenderLayerHand, ArmModelManagerPlayer> container) {
        return null;
    }

    @Override
    public Class<ArmRenderLayerHand> targetLayer() {
        return ArmRenderLayerHand.class;
    }

    @Override
    public Class<ArmModelManagerPlayer> clazz() {
        return ArmModelManagerPlayer.class;
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
