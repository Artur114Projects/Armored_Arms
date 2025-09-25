package com.artur114.armoredarms.client.util;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumHandSide;

public class MiscUtils {
    public static int handSideDelta(EnumHandSide handSide) {
        switch (handSide) {
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                return 0;
        }
    }

    public static ModelRenderer handFromModelBiped(ModelBiped mb, EnumHandSide handSide) {
        switch (handSide) {
            case RIGHT:
                return mb.bipedRightArm;
            case LEFT:
                return mb.bipedLeftArm;
            default:
                return null;
        }
    }

    public static ModelRenderer handFromModelPlayer(ModelPlayer mb, EnumHandSide handSide, boolean wear) {
        switch (handSide) {
            case RIGHT:
                return wear ? mb.bipedRightArmwear : mb.bipedRightArm;
            case LEFT:
                return wear ? mb.bipedLeftArmwear : mb.bipedLeftArm;
            default:
                return null;
        }
    }
}
