package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.api.IArmRenderLayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Cancelable
@SideOnly(Side.CLIENT)
public class AARenderLayerRenderingEvent extends Event {
    private final boolean renderManagerState;
    private final IArmRenderLayer layer;
    private final EnumHandSide side;

    public AARenderLayerRenderingEvent(IArmRenderLayer layer, EnumHandSide side, boolean renderManagerState) {
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

    public EnumHandSide getHandSide() {
        return this.side;
    }
}
