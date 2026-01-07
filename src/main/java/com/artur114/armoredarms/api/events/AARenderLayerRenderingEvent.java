package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.api.IArmRenderLayer;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Cancelable
@SideOnly(Side.CLIENT)
public class AARenderLayerRenderingEvent extends Event {
    private final boolean renderManagerState;
    private final IArmRenderLayer layer;

    public AARenderLayerRenderingEvent(IArmRenderLayer layer, boolean renderManagerState) {
        this.renderManagerState = renderManagerState;
        this.layer = layer;
    }

    public boolean getRenderManagerState() {
        return this.renderManagerState;
    }

    public IArmRenderLayer getRenderLayer() {
        return this.layer;
    }
}
