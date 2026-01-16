package com.artur114.armoredarms.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

/**
 * Rendering layer interface; implementation must have an empty constructor.<br>
 * You can register your layer in {@link com.artur114.armoredarms.api.events.InitRenderLayersEvent}.
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerVanilla
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
     * @param side Hand side to render.
     */
    void renderTransformed(PoseStack poseStack, MultiBufferSource buffer, AbstractClientPlayer player, HumanoidArm side, int combinedLight);

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
    default void renderNotTransformed(AbstractClientPlayer player, float partialTicks, float interpPitch, HumanoidArm hand, float swingProgress, ItemStack stack, float equipProgress) {}
}
