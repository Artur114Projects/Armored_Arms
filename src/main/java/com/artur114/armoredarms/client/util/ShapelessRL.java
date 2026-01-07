package com.artur114.armoredarms.client.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ShapelessRL extends ResourceLocation {
    public ShapelessRL(String resourceName) {
        super(resourceName);
    }

    public ShapelessRL(String resourceDomainIn, String resourcePathIn) {
        super(resourceDomainIn == null ? "" : resourceDomainIn, resourcePathIn == null ? "" : resourcePathIn);
    }

    public ShapelessRL(@Nullable ResourceLocation rl) {
        this(rl == null ? "" : rl.getResourceDomain(), rl == null ? "" : rl.getResourcePath());
    }

    public boolean isShapeless() {
        return this.getResourceDomain().equals("*") || this.getResourcePath().equals("*");
    }

    public boolean isAbsoluteShapeless() {
        return this.getResourceDomain().equals("*") && this.getResourcePath().equals("*");
    }

    public boolean isEmpty() {
        return this.getResourceDomain().isEmpty() || this.getResourcePath().isEmpty();
    }

    @Override
    public boolean equals(Object rl) {
        boolean flag = false;
        if (rl instanceof ShapelessRL) {
            if ((this.getResourcePath().equals("*") && this.getResourceDomain().equals("*")) || (((ShapelessRL) rl).getResourcePath().equals("*") && ((ShapelessRL) rl).getResourceDomain().equals("*"))) {
                flag = true;
            }
            if ((this.getResourceDomain().equals("*") || ((ShapelessRL) rl).getResourceDomain().equals("*"))) {
                flag |= this.getResourcePath().equals(((ResourceLocation) rl).getResourcePath());
            }
            if ((this.getResourcePath().equals("*") || ((ShapelessRL) rl).getResourcePath().equals("*"))) {
                flag |= this.getResourceDomain().equals(((ResourceLocation) rl).getResourceDomain());
            }
        }
        return flag || super.equals(rl);
    }
}
