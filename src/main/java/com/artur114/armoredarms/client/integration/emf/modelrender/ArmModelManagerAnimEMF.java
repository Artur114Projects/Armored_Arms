package com.artur114.armoredarms.client.integration.emf.modelrender;

import com.artur114.armoredarms.client.integration.playeranimator.modelrender.ArmModelManagerPlayerAnim;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ArmModelManagerAnimEMF extends ArmModelManagerPlayerAnim {
    @Override
    public void render(ArmRenderLayerHand layer, IArmModelRenderer<ArmModelManagerPlayer> renderer, EnumHandSideAA side) {
        this.handler$zbf000$emf$setHand(null);

        super.render(layer, renderer, side);

        this.handler$zbf000$emf$unsetHand(null);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGHEST;
    }

    private void handler$zbf000$emf$unsetHand(CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

    private void handler$zbf000$emf$setHand(CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player));
        EMFAnimationEntityContext.isFirstPersonHand = true;
    }
}
