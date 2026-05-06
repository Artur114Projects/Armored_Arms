package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.ArmoredArms;
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
}
