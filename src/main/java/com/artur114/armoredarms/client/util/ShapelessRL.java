package com.artur114.armoredarms.client.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ShapelessRL extends ResourceLocation {
    public ShapelessRL(String resourceName) {
        super(0, (resourceName == null ? new String[] {"", ""} : resourceName.split(":")));
    }

    public ShapelessRL(String resourceDomainIn, String resourcePathIn) {
        super(resourceDomainIn == null ? "" : resourceDomainIn, resourcePathIn == null ? "" : resourcePathIn);
    }

    public ShapelessRL(@Nullable ResourceLocation rl) {
        this(rl == null ? "" : rl.getResourceDomain(), rl == null ? "" : rl.getResourcePath());
    }

    public boolean isShapeless() {
        return this.resourceDomain.equals("*") || this.resourcePath.equals("*");
    }

    public boolean isAbsoluteShapeless() {
        return this.resourceDomain.equals("*") && this.resourcePath.equals("*");
    }

    public boolean isEmpty() {
        return this.resourceDomain.isEmpty() || this.resourcePath.isEmpty();
    }

    @Override
    public boolean equals(Object rl) {
        boolean flag = false;
        if (rl instanceof ShapelessRL) {
            if ((this.resourcePath.equals("*") && this.resourceDomain.equals("*")) || (((ShapelessRL) rl).resourcePath.equals("*") && ((ShapelessRL) rl).resourceDomain.equals("*"))) {
                flag = true;
            }
            if ((this.resourceDomain.equals("*") || ((ShapelessRL) rl).resourceDomain.equals("*"))) {
                flag |= this.resourcePath.equals(((ResourceLocation) rl).getResourcePath());
            }
            if ((this.resourcePath.equals("*") || ((ShapelessRL) rl).resourcePath.equals("*"))) {
                flag |= this.resourceDomain.equals(((ResourceLocation) rl).getResourceDomain());
            }
        }
        return flag || super.equals(rl);
    }
}
