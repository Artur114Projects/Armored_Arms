package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class AAUtils {
    public static ShapelessLocation fromMc(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        return ShapelessLocation.location(location.getResourceDomain().toLowerCase(), location.getResourcePath().toLowerCase());
    }

    public static ShapelessLocation fromMc(String location) {
        if (location == null) {
            return ShapelessLocation.location("minecraft:air");
        }
        return ShapelessLocation.location(location.toLowerCase());
    }

    public static ModelRenderer handFromModelBiped(ModelBiped mb, EnumHandSideAA handSide) {
        switch (handSide) {
            case RIGHT:
                return mb.bipedRightArm;
            case LEFT:
                return mb.bipedLeftArm;
            default:
                throw new NullPointerException();
        }
    }
}
