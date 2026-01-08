package com.artur114.armoredarms.client.util;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public enum EnumHandSide {
    RIGHT, LEFT;

    public int delta() {
        switch (this) {
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                throw new IllegalStateException();
        }
    }

    public ModelRenderer handFromModelBiped(ModelBiped mb) {
        switch (this) {
            case RIGHT:
                return mb.bipedRightArm;
            case LEFT:
                return mb.bipedLeftArm;
            default:
                throw new NullPointerException();
        }
    }
}
