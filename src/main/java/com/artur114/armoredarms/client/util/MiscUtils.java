package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.client.core.ArmRenderLayerVanilla;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;

public class MiscUtils {
    public static int handSideDelta(HumanoidArm handSide) {
        switch (handSide) {
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                return 0;
        }
    }

    public static ModelPart handFromHumanoidModel(HumanoidModel<?> mb, HumanoidArm handSide) {
        return switch (handSide) {
            case RIGHT -> mb.rightArm;
            case LEFT -> mb.leftArm;
        };
    }

    public static ModelPart handFromModelPlayer(PlayerModel<?> mb, HumanoidArm handSide, boolean wear) {
        return switch (handSide) {
            case RIGHT -> wear ? mb.rightArm : mb.rightSleeve;
            case LEFT -> wear ? mb.leftArm : mb.leftSleeve;
        };
    }

    public static ModelPart[] playerArms() {
        PlayerModel<?> player = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(Minecraft.getInstance().player)).getModel();
        return new ModelPart[] {player.leftArm, player.rightArm};
    }

    public static void setPlayerArmDataToArm(ModelPart arm, ModelPart playerArm) {
        arm.xRot = playerArm.xRot;
        arm.yRot = playerArm.yRot;
        arm.zRot = playerArm.zRot;
        arm.x = playerArm.x;
        arm.y = playerArm.y;
        arm.z = playerArm.z;
    }
}
