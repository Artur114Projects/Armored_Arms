package com.artur114.armoredarms.asm;

import com.artur114.armoredarms.asm.out.RenderArmEvent;
import com.artur114.armoredarms.asm.transform.RenderPlayerTransformer;
import com.artur114.armoredarms.asm.util.ITargetTransformer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import net.minecraftforge.common.MinecraftForge;

public class ASMHooksOut {
    private static EnumHandSideAA handSide = EnumHandSideAA.RIGHT;
    private static boolean renderFirstPersonArmHookState = true;

    protected static void onTransformerDown(ITargetTransformer transformer) {
        if (transformer.getClass().equals(RenderPlayerTransformer.class)) {
            renderFirstPersonArmHookState = false;
        }
    }

    public static boolean isRenderFirstPersonArmHookAvailable() {
        return renderFirstPersonArmHookState;
    }

    public static EnumHandSideAA currentHandSide() {
        return handSide;
    }

    public static boolean renderFirstPersonArmHook() {
        return !MinecraftForge.EVENT_BUS.post(new RenderArmEvent());
    }

    public static void backHandPreLeft() {
        handSide = EnumHandSideAA.LEFT;
    }

    public static void backHandPostLeft() {
        handSide = EnumHandSideAA.RIGHT;
    }
}
