package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.api.IArmRenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@OnlyIn(Dist.CLIENT)
public class AARenderLayerRenderingEvent extends Event {
    private final boolean renderManagerState;
    private final IArmRenderLayer layer;
    private final HumanoidArm side;

    public AARenderLayerRenderingEvent(IArmRenderLayer layer, HumanoidArm side, boolean renderManagerState) {
        this.renderManagerState = renderManagerState;
        this.layer = layer;
        this.side = side;
    }

    public boolean getRenderManagerState() {
        return this.renderManagerState;
    }

    public IArmRenderLayer getRenderLayer() {
        return this.layer;
    }

    public HumanoidArm getHandSide() {
        return this.side;
    }
}
