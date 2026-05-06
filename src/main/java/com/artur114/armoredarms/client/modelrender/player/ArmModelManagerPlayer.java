package com.artur114.armoredarms.client.modelrender.player;

import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextPlayer;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.PlayerModelPart;

public class ArmModelManagerPlayer implements IArmModelManager<ArmModelManagerPlayer, ArmRenderLayerHand> {
    protected boolean deactivated = false;
    public final Minecraft mc = Minecraft.getInstance();
    public ItemStackAA chestPlate = ItemStackAA.EMPTY;
    public PlayerModel<AbstractClientPlayer> model;
    public MultiModelRenderContext context;
    public ArmRenderContext rawContext;
    public PlayerRenderer renderPlayer;
    public ArmRenderLayerHand layer;
    public boolean shouldRenderWear;

    @Override
    public void update(ArmRenderLayerHand layer) {
        ItemStackAA stack = layer.currentChestPlate;
        this.shouldRenderWear = !layer.noRenderArmWearList.contains(stack.location()) && (stack.isEmpty() || !AAConfig.disableArmWear || layer.renderArmWearList.contains(stack.location()) || (AAConfig.enableArmWearWithVanillaM && stack.isHumanoid(layer.engine)));
    }

    @Override
    public void render(ArmRenderLayerHand layer, IArmModelRenderer<ArmModelManagerPlayer> renderer, EnumHandSideAA side) {
        if (this.mc.player == null) return;

        this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.mc.player);
        this.context.prepare(layer.context.multiBufferSource, layer.context.poseStack, layer.context.packedLight);
        this.chestPlate = layer.currentChestPlate;
        this.model = this.renderPlayer.getModel();
        this.rawContext = layer.context;
        this.prepareModel(this.model);

        renderer.renderArm(this, side);

        this.renderPlayer = null;
        this.rawContext = null;
        this.chestPlate = null;
        this.model = null;
    }

    @Override
    public void load(ArmRenderLayerHand layer) {
        this.context = new MultiModelRenderContext(new ModelRenderContextPlayer(false), new ModelRenderContextPlayer(true, Priority.LOW));
        this.layer = layer;
    }

    @Override
    public void unload(ArmRenderLayerHand layer) {}

    @Override
    public ArmRenderLayerHand layer() {
        return this.layer;
    }

    @Override
    public Bone bone(EnumHandSideAA side) {
        return this.layer.engine.mainBones().bySide(side);
    }

    @Override
    public IArmModelRenderer<ArmModelManagerPlayer> cacheRenderer(ArmRenderLayerHand layer, IArmModelRenderContainer<ArmRenderLayerHand, ArmModelManagerPlayer> container) {
        if (this.mc.player == null) return null;

        this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.mc.player);
        this.chestPlate = layer.currentChestPlate;
        this.model = this.renderPlayer.getModel();

        IArmModelRenderer<ArmModelManagerPlayer> model = container.create(this);

        this.renderPlayer = null;
        this.chestPlate = null;
        this.model = null;

        return model;
    }

    public ModelPart arm(EnumHandSideAA side) {
        return switch (side) {
            case RIGHT -> this.model.rightArm;
            case LEFT -> this.model.leftArm;
        };
    }

    public ModelPart armWear(EnumHandSideAA side) {
        return switch (side) {
            case RIGHT -> this.model.rightSleeve;
            case LEFT -> this.model.leftSleeve;
        };
    }

    public void prepareModel(PlayerModel<AbstractClientPlayer> model) {
        if (this.mc.player == null) return;
        if (this.mc.player.isSpectator()) {
            model.setAllVisible(false);
            model.head.visible = true;
            model.hat.visible = true;
        } else {
            model.setAllVisible(true);
            model.hat.visible = this.mc.player.isModelPartShown(PlayerModelPart.HAT);
            model.jacket.visible = this.mc.player.isModelPartShown(PlayerModelPart.JACKET);
            model.leftPants.visible = this.mc.player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            model.rightPants.visible = this.mc.player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            model.leftSleeve.visible = this.mc.player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            model.rightSleeve.visible = this.mc.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            model.crouching = this.mc.player.isCrouching();
        }
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(this.mc.player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.layer.engine().mainBones().updateBones(model.rightArm, model.leftArm);
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
