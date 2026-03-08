package com.artur114.armoredarms.core.api;

import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public interface IArmRenderLayer<E extends IArmRenderEngine<?>> {
    void update(E engine, AbstractClientPlayer player);
    void render(E engine, AbstractClientPlayer player, EnumHandSide handSide);
    void init(E engine, IAAModContainer mod, AbstractClientPlayer player);
    boolean needRender(E engine, AbstractClientPlayer player, boolean renderEngineState);
    Class<E> targetEngine();
}
