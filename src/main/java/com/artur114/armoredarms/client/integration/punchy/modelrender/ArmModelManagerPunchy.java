package com.artur114.armoredarms.client.integration.punchy.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.mixin.IPunchyArmRenderProvider;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class ArmModelManagerPunchy extends ArmModelManagerPlayer {

    @Override
    @SuppressWarnings("unchecked")
    public void render(ArmRenderLayerHand layer, IArmModelRenderer<ArmModelManagerPlayer> renderer, EnumHandSideAA side) {
        if (this.mc.player == null) return;

        this.model = (PlayerModel<AbstractClientPlayer>) IPunchyArmRenderProvider.INSTANCE.armoredarms$punchyFirstPersonModel(this.mc.player);
        this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.mc.player);
        this.context.prepare(layer.context.multiBufferSource, layer.context.poseStack, layer.context.packedLight);
        this.chestPlate = layer.currentChestPlate;
        this.rawContext = layer.context;
        this.prepareModel(this.model);

        renderer.renderArm(this, side);

        this.renderPlayer = null;
        this.rawContext = null;
        this.chestPlate = null;
        this.model = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IArmModelRenderer<ArmModelManagerPlayer> cacheRenderer(ArmRenderLayerHand layer, IArmModelRenderContainer<ArmRenderLayerHand, ArmModelManagerPlayer> container) {
        if (this.mc.player == null) return null;

        this.model = (PlayerModel<AbstractClientPlayer>) IPunchyArmRenderProvider.INSTANCE.armoredarms$punchyFirstPersonModel(this.mc.player);
        this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.mc.player);
        this.chestPlate = layer.currentChestPlate;

        IArmModelRenderer<ArmModelManagerPlayer> model = container.create(this);

        this.renderPlayer = null;
        this.chestPlate = null;
        this.model = null;

        return model;
    }

    @Override
    public void prepareModel(PlayerModel<AbstractClientPlayer> model) {
        this.layer.engine().mainBones().updateBones(model.rightArm, model.leftArm);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGHEST;
    }
}
