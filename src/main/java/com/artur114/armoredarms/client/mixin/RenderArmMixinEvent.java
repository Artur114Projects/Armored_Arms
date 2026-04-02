package com.artur114.armoredarms.client.mixin;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderArmMixinEvent extends Event {
    private final EnumHandSideAA side;

    public RenderArmMixinEvent(EnumHandSideAA side) {
        this.side = side;
    }

    public EnumHandSideAA armSide() {
        return this.side;
    }
}
