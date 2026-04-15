package com.artur114.armoredarms.client.integration.ears.modelrender;

import com.artur114.armoredarms.aalegacy.client.util.EnumHandSide;
import com.artur114.armoredarms.aalegacy.client.util.Reflector;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelRendererPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.unascribed.ears.Ears;
import com.unascribed.ears.LayerEars;
import com.unascribed.ears.common.legacy.PartiallyUnmanagedEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;

import static sun.audio.AudioPlayer.player;

public class ArmModelRendererEars extends ArmModelRendererPlayer {
    private PartiallyUnmanagedEarsRenderDelegate<AbstractClientPlayer, ModelRenderer> delegate = null;

    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        super.renderArm(context, side);

        if (!context.shouldRenderWear) {
            return;
        }

        if (this.delegate == null) {
            try {
                this.delegate = Reflector.getPrivateField(LayerEars.class, Reflector.getPrivateField(Ears.class, null, "layer"), "delegate");
            } catch (Exception ignored) {}
        }

        EarsRenderDelegate.BodyPart bodyPart = side == EnumHandSideAA.RIGHT ? EarsRenderDelegate.BodyPart.RIGHT_ARM : EarsRenderDelegate.BodyPart.LEFT_ARM;
        this.delegate.render(context.mc.thePlayer, bodyPart);
    }
}
