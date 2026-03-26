package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class AAUtils {
    public static EnumHandSideAA fromMc(HumanoidArm arm) {
        return EnumHandSideAA.values()[arm.getId()];
    }

    public static ShapelessLocation fromMc(ResourceLocation location) {
        if (location == null) {
            return ShapelessLocation.EMPTY;
        }
        return ShapelessLocation.location(location.getNamespace(), location.getPath());
    }


    public static void setForcedRotations(ModelPart part, EnumHandSideAA side) {
        int delta = side.delta();
        part.xRot = 0.0F;
        part.yRot = 0.0F;
        part.zRot = 0.1F * delta;
        part.x = -5.0F * delta;
        part.y = 2.0F;
        part.z = 0.0F;
    }

    public static int handSideDelta(HumanoidArm handSide) {
        return switch (handSide) {
            case RIGHT -> 1;
            case LEFT -> -1;
        };
    }

    public static ModelPart handFromHumanoidModel(HumanoidModel<?> mb, EnumHandSideAA handSide) {
        return switch (handSide) {
            case RIGHT -> mb.rightArm;
            case LEFT -> mb.leftArm;
        };
    }

    public static ModelPart handFromModelPlayer(PlayerModel<?> mb, EnumHandSideAA handSide, boolean wear) {
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
