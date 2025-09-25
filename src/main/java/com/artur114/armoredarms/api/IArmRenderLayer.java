package com.artur114.armoredarms.api;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.EnumHandSide;

public interface IArmRenderLayer {
    void update(AbstractClientPlayer player);
    void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide);
    boolean needRender(AbstractClientPlayer player, boolean renderManagerState);
    void init(AbstractClientPlayer player);
}
