package com.artur114.armoredarms.client.integration.playeranimator.modelrender;

import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IPlayerModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;

public class ArmModelManagerPlayerAnim extends ArmModelManagerPlayer {
    @Override
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
        if (model instanceof IPlayerModel playerModel) {
            if (!((IAnimatedPlayer) this.mc.player).playerAnimator_getAnimation().getFirstPersonMode().isEnabled()) {
                playerModel.playerAnimator_prepForFirstPersonRender();
            }
        }
        model.setupAnim(this.mc.player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.layer.engine().mainBones().updateBones(model.rightArm, model.leftArm);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
