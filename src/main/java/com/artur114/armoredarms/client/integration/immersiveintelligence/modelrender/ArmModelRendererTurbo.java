package com.artur114.armoredarms.client.integration.immersiveintelligence.modelrender;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.artur114.armoredarms.aalegacy.util.MiscUtils;
import com.artur114.armoredarms.aalegacy.util.Reflector;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import pl.pabilo8.immersiveintelligence.client.model.armor.ModelLightEngineerArmor;
import pl.pabilo8.immersiveintelligence.client.util.tmt.ModelRendererTurbo;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.util.IIColor;

public class ArmModelRendererTurbo implements IArmModelRenderer<ArmModelManagerArmor> {
    public final ModelRenderer[] playerArms = MiscUtils.playerArms();
    private final ModelRendererTurbo[][] plates;
    private final ModelRendererTurbo[][] hand;
    private final ResourceLocation plateTex;
    private final ModelRenderer[] biped;
    private final IMultiTexture texture;

    public ArmModelRendererTurbo(ModelLightEngineerArmor model, IMultiTexture texture) {
        this.plates = new ModelRendererTurbo[][] {Reflector.getPrivateField(model, "platesLeftArmModel"), Reflector.getPrivateField(model, "platesRightArmModel")};
        this.plateTex = new ResourceLocation(Reflector.getPrivateField(model, "TEXTURE_PLATES"));
        this.hand = new ModelRendererTurbo[][] {model.leftArmModel, model.rightArmModel};
        this.biped = new ModelRenderer[] {model.bipedLeftArm, model.bipedRightArm};
        this.texture = texture;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            ModelRenderer biped = this.biped[side.ordinal()];
            float scale = 1.0F / 16.0F;
            biped.rotationPointX = -5.0F * side.delta();
            biped.rotationPointY = 2.0F;
            biped.rotationPointZ = 0.0F;
            MiscUtils.setPlayerArmDataToArm(biped, this.playerArms[side.ordinal()]);

            GlStateManager.pushMatrix();
            GlStateManager.translate(biped.rotationPointX * scale, biped.rotationPointY * scale, biped.rotationPointZ * scale);
            if (biped.rotateAngleY != 0.0F) {
                GlStateManager.rotate(biped.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (biped.rotateAngleX != 0.0F) {
                GlStateManager.rotate(biped.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (biped.rotateAngleZ != 0.0F) {
                GlStateManager.rotate(biped.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(1.06, 1.06, 1.06);
            this.render(this.hand[side.ordinal()], this.plates[side.ordinal()], context.stack.stack());
            GlStateManager.popMatrix();
            iterator.postBind();
        }
    }

    private void render(ModelRendererTurbo[] hand, ModelRendererTurbo[] plates, ItemStack stack) {
        for (int i = 0; i != hand.length; i++) {
            hand[i].render();
        }

        NBTTagCompound upgrades = IIContent.itemLightEngineerHelmet.getUpgrades(stack);
        if (this.plateTex != null && this.plates != null && this.hasPlates(upgrades)) {
            int armorIncrease = upgrades.getInteger("armor_increase");
            this.setColorForPlates(stack, upgrades);
            if (armorIncrease > 1) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.plateTex);
                for (int i = 0; i != plates.length; i++) {
                    boolean h = plates[i].isHidden;
                    boolean s = plates[i].showModel;
                    plates[i].isHidden = false;
                    plates[i].showModel = true;
                    plates[i].render();
                    plates[i].isHidden = h;
                    plates[i].showModel = s;
                }
            }
        }
    }

    private boolean hasPlates(NBTTagCompound upgrades) {
        return upgrades.hasKey("steel_plates") || upgrades.hasKey("composite_plates");
    }

    private void setColorForPlates(ItemStack stack, NBTTagCompound upgrades) {
        if (ItemNBTHelper.hasKey(stack, "colour")) {
            float[] rgb = IIColor.rgbIntToRGB(ItemNBTHelper.getInt(stack, "colour"));
            GlStateManager.color(rgb[0], rgb[1], rgb[2]);
        } else if (upgrades.hasKey("composite_plates")) {
            GlStateManager.color(0.9F, 0.9F, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
        }
    }
}
