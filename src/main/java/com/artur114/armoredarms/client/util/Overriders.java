package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.override.IBodeThing;
import com.artur114.armoredarms.api.override.IOverriderGetModel;
import com.artur114.armoredarms.api.override.IOverriderGetTex;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import pl.pabilo8.immersiveintelligence.client.model.TMTArmorModel;
import pl.pabilo8.immersiveintelligence.client.util.tmt.ModelRendererTurbo;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.util.IISkinHandler;

import java.util.List;

public class Overriders {
    public static void init() {
        ArmoredArmsApi.registerOverrider("immersiveintelligence", "light_engineer_armor_chestplate", new LightEngineerArmorOverrider(), false);
        ArmoredArmsApi.registerOverrider("galaxyspace", "space_suit_chest", new SpaceSuitTier1Overrider(), false);
    }

    /**
     * for immersiveintelligence:light_engineer_armor_chestplate
      */
    public static class LightEngineerArmorOverrider implements IOverriderGetModel, IOverriderGetTex {
        private IBodeThing[] hands = null;

        @Override
        public ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBase mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb instanceof TMTArmorModel) {
                this.hands = new IBodeThing[] {
                    new BoneThingModelRenderersTurbo(((TMTArmorModel) mb).bipedRightArm, ((TMTArmorModel) mb).rightArmModel, stack),
                    new BoneThingModelRenderersTurbo(((TMTArmorModel) mb).bipedLeftArm, ((TMTArmorModel) mb).leftArmModel, stack)
                };
            } else {
                this.hands = null;
            }
            return mb;
        }

        @Override
        public IBodeThing getArm(ModelBase mb, ItemArmor itemArmor, ItemStack stack, EnumHandSide handSide) {
            if (this.hands == null) {
                return null;
            }
            switch (handSide) {
                case RIGHT:
                    return this.hands[0];
                case LEFT:
                    return this.hands[1];
                default:
                    return null;
            }
        }

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumModelTexType type) {
            String s = IISkinHandler.getCurrentSkin(chestPlate);
            if (IISkinHandler.isValidSkin(s)) {
                IISkinHandler.IISpecialSkin skin = IISkinHandler.getSkin(s);
                if (skin.doesApply(IIContent.itemLightEngineerChestplate.getSkinnableName())) {
                    return this.getSkin(s);
                }
            } else {
                return this.getSkin("");
            }
            return this.getSkin("");
        }

        private ResourceLocation getSkin(String skin) {
            String baseName = skin.isEmpty() ? "immersiveintelligence:textures/armor/engineer_light" : "immersiveintelligence:textures/skins/" + skin + "/engineer_light";
            return new ResourceLocation(baseName + ".png");
        }

        public static class BoneThingModelRenderersTurbo implements IBodeThing {
            private final ModelRendererTurbo[] mr;
            private final ModelRenderer biped;
            private final ItemStack stack;

            public BoneThingModelRenderersTurbo(ModelRenderer biped, ModelRendererTurbo[] mr, ItemStack stack) {
                this.stack = stack;
                this.biped = biped;
                this.mr = mr;
            }

            @Override
            public void setRotation(float x, float y, float z) {
                this.biped.rotateAngleX = x;
                this.biped.rotateAngleY = y;
                this.biped.rotateAngleZ = z;
            }

            @Override
            public void render(float scale) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.biped.rotationPointX * scale, this.biped.rotationPointY * scale, this.biped.rotationPointZ * scale);
                if (this.biped.rotateAngleY != 0.0F) {
                    GlStateManager.rotate(this.biped.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (this.biped.rotateAngleX != 0.0F) {
                    GlStateManager.rotate(this.biped.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (this.biped.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate(this.biped.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(1.06, 1.06, 1.06);
                for (int i = 0; i != this.mr.length; i++) {
                    this.mr[i].render();
                }
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     *  for galaxyspace:space_suit_chest
     */
    public static class SpaceSuitTier1Overrider implements IOverriderGetTex, IOverriderGetModel {
        private IBodeThing[] hands = null;

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumModelTexType type) {
            return TextureMap.LOCATION_BLOCKS_TEXTURE;
        }

        @Override
        public ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb instanceof ItemSpaceSuitModel) {
                this.hands = new IBodeThing[] {
                    new BoneThingModelOBJArmor((ItemSpaceSuitModel) mb, EnumHandSide.RIGHT),
                    new BoneThingModelOBJArmor((ItemSpaceSuitModel) mb, EnumHandSide.LEFT)
                };
            } else {
                this.hands = null;
            }
            return mb;
        }

        @Override
        public IBodeThing getArm(ModelBase mb, ItemArmor itemArmor, ItemStack stack, EnumHandSide handSide) {
            if (this.hands == null) {
                return null;
            }
            switch (handSide) {
                case RIGHT:
                    return this.hands[0];
                case LEFT:
                    return this.hands[1];
                default:
                    return null;
            }
        }

        public static class BoneThingModelOBJArmor implements IBodeThing {
            public final EnumHandSide side;
            public final float[] colors;
            public final int glList;
            public float x;
            public float y;
            public float z;

            public BoneThingModelOBJArmor(ItemSpaceSuitModel model, EnumHandSide side) {
                this.side = side;

                switch (side) {
                    case RIGHT:
                        this.glList = ItemSpaceSuitModel.rightArmList;
                        this.colors = model.color;
                        break;
                    case LEFT:
                        this.glList = ItemSpaceSuitModel.leftArmList;
                        this.colors = model.color;
                        break;
                    default:
                        throw new NullPointerException();
                }
            }

            @Override
            public void setRotation(float x, float y, float z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            @Override
            public void render(float scale) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(this.z * 57.295776F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(this.y * 57.295776F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(this.x * 57.295776F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(this.side == EnumHandSide.RIGHT ? (1.0F / 16.0F) : -(1.0F / 16.0F), -1.8F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.color(this.colors[0], this.colors[1], this.colors[2]);
                GlStateManager.callList(this.glList);
                GlStateManager.popMatrix();
            }
        }
    }
}
