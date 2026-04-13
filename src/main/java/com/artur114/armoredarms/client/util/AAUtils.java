package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.ShapelessLocation;
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
            return null;
        }
        return ShapelessLocation.location(location.toLowerCase());
    }
}
