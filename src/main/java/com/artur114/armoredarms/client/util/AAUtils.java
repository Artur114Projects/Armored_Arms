package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.ShapelessLocation;
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
