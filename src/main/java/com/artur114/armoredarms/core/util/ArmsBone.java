package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.EnumHandSideAA;

public class ArmsBone {
    private final Bone[] bones;

    public ArmsBone(IAAModContainer mod) {
        this.bones = new Bone[] {new Bone(mod, "leftArm"), new Bone(mod, "rightArm")};
    }

    public Bone bySide(EnumHandSideAA side) {
        return this.bones[side.ordinal()];
    }

    public void updateBones(Object right, Object left) {
        this.bySide(EnumHandSideAA.RIGHT).setFrom(right);
        this.bySide(EnumHandSideAA.LEFT).setFrom(left);
    }

    public void registerAdapter(IBoneAdapter<?> adapter) {
        this.bones[0].registerAdapter(adapter);
        this.bones[1].registerAdapter(adapter);
    }

    public boolean hasAdapterFor(Class<?> clazz) {
        return this.bones[0].hasAdapterFor(clazz) || this.bones[1].hasAdapterFor(clazz);
    }
}
