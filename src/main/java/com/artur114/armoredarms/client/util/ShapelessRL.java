package com.artur114.armoredarms.client.util;

import net.minecraft.util.ResourceLocation;

public class ShapelessRL extends ResourceLocation {
    public ShapelessRL(String resourceName) {
        super(resourceName);
    }

    public ShapelessRL(String resourceDomainIn, String resourcePathIn) {
        super(resourceDomainIn, resourcePathIn);
    }

    public ShapelessRL(ResourceLocation rl) {
        this(rl.getResourceDomain(), rl.getResourcePath());
    }

    @Override
    public boolean equals(Object rl) {
        boolean flag = false;
        if (rl instanceof ShapelessRL && (this.resourcePath.equals("*") || ((ShapelessRL) rl).resourcePath.equals("*"))) {
            flag = this.resourceDomain.equals(((ResourceLocation) rl).getResourceDomain());
        }
        return flag || super.equals(rl);
    }
}
