package com.artur114.armoredarms.client.integration;


import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.api.IOverriderGetModel;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.EnumHandSide;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.model.armor.ModelArmorTerrasteel;

/**
 * Here i ultrashitcoding
 */
public class Overriders {
    @SubscribeEvent
    public void initOverriders(InitArmorRenderLayerEvent e) {
        e.registerOverrider("Botania", "terrasteelChest", new OverriderBotania("armr", "armL"), false);
        e.registerOverrider("Botania", "elementiumChest", new OverriderBotania("armR", "armL"), false);
        e.registerOverrider("Botania", "manasteelChest", new OverriderBotania("armR", "armL"), false);
        e.registerOverrider("Botania", "manaweaveChest", new OverriderBotania("armR", "armL"), false);

        e.registerOverrider("alfheim", "ElementalEarthChest", new OverriderBotania("armR", "armL"), false);
        e.registerOverrider("alfheim", "ElvoriumChestplate", new OverriderBotania("armR", "armL"), false);
        e.registerOverrider("alfheim", "FenrirChestplate", new OverriderBotania("armR", "armL"), false);
    }

    public static class OverriderBotania extends ArmRenderLayerArmor.DefaultModelGetter {
        private final String rightArm;
        private final String leftArm;
        public OverriderBotania(String rightArm, String leftArm) {
            this.rightArm = rightArm;
            this.leftArm = leftArm;
        }

        @Override
        protected IModelOnlyArms createModel(ModelBiped mb) {
            if (mb.getClass().getName().contains("ModelElvoriumArmor")) {
                return new ModelOnlyArmsIModelCustom(mb, Reflector.getPrivateField(mb, "model"));
            } else {
                return new ArmRenderLayerArmor.DefaultModelOnlyArms(mb, Reflector.getPrivateField(mb, this.rightArm), Reflector.getPrivateField(mb, this.leftArm));
            }
        }

        public static class ModelOnlyArmsIModelCustom implements IModelOnlyArms {
            public final ModelRenderer[] playerArms = MiscUtils.playerArms();
            private final IModelCustom modelCustom;
            private final ModelBiped mb;

            public ModelOnlyArmsIModelCustom(ModelBiped mb, IModelCustom modelCustom) {
                this.modelCustom = modelCustom;
                this.mb = mb;
            }

            @Override
            public void renderArm(AbstractClientPlayer player, EnumHandSide side, ItemArmor itemArmor, ItemStack stackArmor) {
                float parTicks = 1.0F / 16.0F;
                ModelRenderer arm = this.playerArms[side.ordinal()];
                GL11.glPushMatrix();
                GL11.glTranslatef(arm.rotationPointX * parTicks, arm.rotationPointY * parTicks, arm.rotationPointZ * parTicks);
                GL11.glRotatef((float) (arm.rotateAngleZ * (180.0F / Math.PI)), 0.0F, 0.0F, 1.0F);
                GL11.glRotatef((float) (arm.rotateAngleY * (180.0F / Math.PI)), 0.0F, 1.0F, 0.0F);
                GL11.glRotatef((float) (arm.rotateAngleX * (180.0F / Math.PI)), 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                double s = 0.01;
                if (side == EnumHandSide.RIGHT) {
                    GL11.glTranslated(0.31, -0.55, 0.0);
                    GL11.glScaled(s, s, s);
                    this.modelCustom.renderPart("ArmO");
                } else {
                    GL11.glTranslated(-0.31, -0.55, 0.0);
                    GL11.glScaled(s, s, s);
                    this.modelCustom.renderPart("ArmT");
                }
                GL11.glPopMatrix();
            }

            @Override
            public ModelBiped original() {
                return this.mb;
            }
        }
     }
}
