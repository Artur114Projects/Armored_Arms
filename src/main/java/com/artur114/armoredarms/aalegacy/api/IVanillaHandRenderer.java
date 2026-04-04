package com.artur114.armoredarms.aalegacy.api;

import com.artur114.armoredarms.aalegacy.client.util.EnumHandSide;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

public interface IVanillaHandRenderer {
    void update(AbstractClientPlayer player, RenderPlayer renderPlayer);
    void renderHand(AbstractClientPlayer player, RenderPlayer renderPlayer, EnumHandSide side);
    boolean isCombinable();

    static IVanillaHandRenderer combine(IVanillaHandRenderer renderer1, IVanillaHandRenderer renderer2) {
        return new IVanillaHandRenderer() {
            private final IVanillaHandRenderer render1 = renderer1;
            private final IVanillaHandRenderer render2 = renderer2;

            @Override
            public void update(AbstractClientPlayer player, RenderPlayer renderPlayer) {
                this.render1.update(player, renderPlayer);
                this.render2.update(player, renderPlayer);
            }

            @Override
            public void renderHand(AbstractClientPlayer player, RenderPlayer renderPlayer, EnumHandSide side) {
                this.render1.renderHand(player, renderPlayer, side);
                this.render2.renderHand(player, renderPlayer, side);
            }

            @Override
            public boolean isCombinable() {
                return true;
            }

            @Override
            public String toString() {
                return "[" + this.render1 + ", " + this.render2 + "]";
            }
        };
    }
}
