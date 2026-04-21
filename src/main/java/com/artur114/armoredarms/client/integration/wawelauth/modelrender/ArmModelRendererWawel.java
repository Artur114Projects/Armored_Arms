package com.artur114.armoredarms.client.integration.wawelauth.modelrender;

import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelRendererPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import org.fentanylsolutions.wawelauth.client.render.IModelBipedModernExt;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.fentanylsolutions.wawelauth.client.render.SkinModelHelper;
import org.fentanylsolutions.wawelauth.client.render.skinlayers.SkinLayers3DConfig;
import org.fentanylsolutions.wawelauth.client.render.skinlayers.SkinLayers3DSetup;
import org.fentanylsolutions.wawelauth.client.render.skinlayers.SkinLayers3DState;
import org.fentanylsolutions.wawelauth.common.ISkinLayerExtender;
import org.fentanylsolutions.wawelauth.wawelcore.data.SkinModel;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class ArmModelRendererWawel extends ArmModelRendererPlayer {
    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        AbstractClientPlayer player = context.mc.thePlayer;
        IModelBipedModernExt ext = (IModelBipedModernExt) context.renderPlayer.modelBipedMain;
        UUID uuid = player.getUniqueID();
        ext.setCurrentPlayerUuid(uuid);

        if (!SkinLayers3DConfig.modernSkinSupport) {
            ext.setSlim(false);
            SkinLayers3DSetup.updateState(uuid, null);
        } else {
            if (!ext.isModern()) {
                ext.initModern();
            }

            SkinModel model = SkinModelHelper.getSkinModel(player);
            boolean slim = model == SkinModel.SLIM;
            ext.setSlim(slim);
            if (SkinLayers3DConfig.enabled3D) {
                SkinLayers3DState existing = SkinLayers3DSetup.getState(uuid);
                SkinLayers3DState state = SkinLayers3DSetup.createOrUpdate(player, existing, slim);
                SkinLayers3DSetup.updateState(uuid, state);
            } else {
                SkinLayers3DSetup.updateState(uuid, null);
            }

            if (((ISkinLayerExtender)player).wawelAuth$getHideRightSleeve()) {
                ext.getRightArmWear().showModel = false;
            }
        }

        super.renderArm(context, side);

        if (context.shouldRenderWear) {
            ext.render3DRightArmWear(0.0625F);
            ext.getRightArmWear().showModel = true;
        }
    }
}
