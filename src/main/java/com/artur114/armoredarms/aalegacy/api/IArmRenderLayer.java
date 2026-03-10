package com.artur114.armoredarms.aalegacy.api;

import com.artur114.armoredarms.aalegacy.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.aalegacy.core.ArmRenderLayerVanilla;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

/**
 * Rendering layer interface; implementation must have an empty constructor.<br>
 * You can register your layer in {@link InitRenderLayersEvent}.
 * @see ArmRenderLayerVanilla
 */
public interface IArmRenderLayer {

    /**
     * The update method is called every tick.
     * @param player Main Client-side player.
     */
    void update(AbstractClientPlayer player);

    /**
     * Rendering method; all transformations are already performed before calling.
     * @param player Main Client-side player.
     * @param handSide Hand side to render.
     */
    void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide);

    /**
     * By default, the vanilla hand renderer is used,
     * but if one of the layers returns true,
     * the render manager is enabled and custom rendering is used.
     * @param player Main Client-side player.
     * @param renderManagerState Render manager state.
     * @return Whether the layer should be rendered.
     */
    boolean needRender(AbstractClientPlayer player, boolean renderManagerState);

    /**
     * Initialization method, called after the layer is created and registered.
     * @param player Main Client-side player.
     */
    void init(AbstractClientPlayer player);

    /**
     * Rendering method.
     */
    default void renderNotTransformed(AbstractClientPlayer player, float partialTicks, float interpPitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {}
}
