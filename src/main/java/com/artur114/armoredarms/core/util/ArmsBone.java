package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.EnumHandSideAA;

public class ArmsBone {
    private final Bone[] bones = new Bone[] {new Bone(), new Bone()};

    public Bone bySide(EnumHandSideAA side) {
        return this.bones[side.ordinal()];
    }

    public void updateBones(Object right, Object left) {
        this.bySide(EnumHandSideAA.RIGHT).setFrom(right);
        this.bySide(EnumHandSideAA.LEFT).setFrom(left);
    }
}
