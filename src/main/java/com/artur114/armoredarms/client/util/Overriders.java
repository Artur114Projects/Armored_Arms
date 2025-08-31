package com.artur114.armoredarms.client.util;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.override.IBoneThing;
import com.artur114.armoredarms.api.override.IOverriderGetModel;
import com.artur114.armoredarms.api.override.IOverriderGetTex;
import com.artur114.armoredarms.client.RenderArmManager;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.render.model.ModelT45Chest;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;
import net.minecraft.client.Minecraft;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import pl.pabilo8.immersiveintelligence.client.model.armor.ModelLightEngineerArmor;
import pl.pabilo8.immersiveintelligence.client.util.tmt.ModelRendererTurbo;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.util.IIColor;
import pl.pabilo8.immersiveintelligence.common.util.IISkinHandler;

import java.lang.reflect.Field;
import java.util.List;

public class Overriders {
    public static void init() {
        ArmoredArmsApi.registerOverrider("immersiveintelligence", "light_engineer_armor_chestplate", new LightEngineerArmorOverrider(), false);
        ArmoredArmsApi.registerOverrider("galaxyspace", "space_suit_chest", new SpaceSuitTier1Overrider(), false);
        ArmoredArmsApi.registerOverrider("hbm", "t45_plate", new HBMOverrider("rightarm", "leftarm", "item"), false);
        ArmoredArmsApi.registerOverrider("hbm", "ajr_plate", new HBMOverrider("rightArm", "leftArm", "ajr_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "ajro_plate", new HBMOverrider("rightArm", "leftArm", "ajro_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "hev_plate", new HBMOverrider("rightArm", "leftArm", "hev_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "bj_plate", new HBMOverrider("rightArm", "leftArm", "bj_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "bj_plate_jetpack", new HBMOverrider("rightArm", "leftArm", "bj_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "rpa_plate", new HBMOverrider("rightArm", "leftArm", "rpa_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "fau_plate", new HBMOverrider("rightArm", "leftArm", "fau_arm"), false);
        ArmoredArmsApi.registerOverrider("hbm", "dns_plate", new HBMOverrider("rightArm", "leftArm", "dnt_arm"), false);
    }

    /**
     * for immersiveintelligence:light_engineer_armor_chestplate
      */
    public static class LightEngineerArmorOverrider implements IOverriderGetModel, IOverriderGetTex {
        private IBoneThing[] hands = null;

        @Override
        public ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBase mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb instanceof ModelLightEngineerArmor) {
                this.hands = new IBoneThing[] {
                    new BoneThingModelRenderersTurbo(((ModelLightEngineerArmor) mb).bipedRightArm, ((ModelLightEngineerArmor) mb).rightArmModel, (ModelRendererTurbo[]) this.getField("platesRightArmModel", mb), new ResourceLocation((String) this.getField("TEXTURE_PLATES", mb)), stack),
                    new BoneThingModelRenderersTurbo(((ModelLightEngineerArmor) mb).bipedLeftArm, ((ModelLightEngineerArmor) mb).leftArmModel, (ModelRendererTurbo[]) this.getField("platesLeftArmModel", mb), new ResourceLocation((String) this.getField("TEXTURE_PLATES", mb)), stack)
                };
            } else {
                this.hands = null;
            }
            return mb;
        }

        @Override
        public IBoneThing getArm(ModelBase mb, ItemArmor itemArmor, ItemStack stack, EnumHandSide handSide) {
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

        private Object getField(String field, Object obj) {
            try {
                Field field1 = obj.getClass().getDeclaredField(field);
                field1.setAccessible(true);
                Object ret = field1.get(obj);
                field1.setAccessible(false);
                return ret;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public static class BoneThingModelRenderersTurbo implements IBoneThing {
            private final ModelRendererTurbo[] plates;
            private final ModelRendererTurbo[] hand;
            private final ResourceLocation plateTex;
            private final ModelRenderer biped;
            private final ItemStack stack;

            public BoneThingModelRenderersTurbo(ModelRenderer biped, ModelRendererTurbo[] hand, ModelRendererTurbo[] plates, ResourceLocation plateTex, ItemStack stack) {
                this.plateTex = plateTex;
                this.plates = plates;
                this.stack = stack;
                this.biped = biped;
                this.hand = hand;
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
                this.render();
                GlStateManager.popMatrix();
            }

            private void render() {
                for (int i = 0; i != this.hand.length; i++) {
                    this.hand[i].render();
                }

                NBTTagCompound upgrades = IIContent.itemLightEngineerHelmet.getUpgrades(this.stack);
                if (this.plateTex != null && this.plates != null && this.hasPlates(upgrades)) {
                    int armorIncrease = upgrades.getInteger("armor_increase");
                    this.setColorForPlates(this.stack, upgrades);
                    if (armorIncrease > 1) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(this.plateTex);
                        for (int i = 0; i != this.plates.length; i++) {
                            boolean h = this.plates[i].isHidden;
                            boolean s = this.plates[i].showModel;
                            this.plates[i].isHidden = false;
                            this.plates[i].showModel = true;
                            this.plates[i].render();
                            this.plates[i].isHidden = h;
                            this.plates[i].showModel = s;
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
    }

    /**
     *  for galaxyspace:space_suit_chest
     */
    public static class SpaceSuitTier1Overrider implements IOverriderGetTex, IOverriderGetModel {
        private IBoneThing[] hands = null;

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumModelTexType type) {
            return TextureMap.LOCATION_BLOCKS_TEXTURE;
        }

        @Override
        public ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb instanceof ItemSpaceSuitModel) {
                this.hands = new IBoneThing[] {
                    new BoneThingModelOBJArmor((ItemSpaceSuitModel) mb, EnumHandSide.RIGHT),
                    new BoneThingModelOBJArmor((ItemSpaceSuitModel) mb, EnumHandSide.LEFT)
                };
            } else {
                this.hands = null;
            }
            return mb;
        }

        @Override
        public IBoneThing getArm(ModelBase mb, ItemArmor itemArmor, ItemStack stack, EnumHandSide handSide) {
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

        public static class BoneThingModelOBJArmor implements IBoneThing {
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
                GlStateManager.translate(this.side == EnumHandSide.RIGHT ? (1.0F / 16.0F) : -(1.0F / 16.0F), -1.7F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.color(this.colors[0], this.colors[1], this.colors[2]);
                GlStateManager.callList(this.glList);
                GlStateManager.popMatrix();
            }
        }
    }

    public static class HBMOverrider implements IOverriderGetModel, IOverriderGetTex {
        private IBoneThing[] hands = null;
        private final String rightArm;
        private final String leftArm;
        private final String texture;

        public HBMOverrider(String rightArm, String leftArm, String texture) {
            this.rightArm = rightArm;
            this.leftArm = leftArm;
            this.texture = texture;
        }

        @Override
        public ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            Object r = Reflector.getPrivateField(mb, this.rightArm);
            Object l = Reflector.getPrivateField(mb, this.leftArm);
            if (mb instanceof ModelT45Chest) {
                this.hands = new IBoneThing[] {
                    new BoneThingT45((ModelRenderer) r),
                    new BoneThingT45((ModelRenderer) l)
                };
            } else if (r instanceof ModelRenderer) {
                this.hands = new IBoneThing[] {
                    new RenderArmManager.BoneThingModelRender((ModelRenderer) r),
                    new RenderArmManager.BoneThingModelRender((ModelRenderer) l)
                };
            } else if (r instanceof ModelRendererObj) {
                this.hands = new IBoneThing[] {
                    new BoneThingOBJ((ModelRendererObj) r),
                    new BoneThingOBJ((ModelRendererObj) l)
                };
            }
            return mb;
        }

        @Override
        public IBoneThing getArm(ModelBase mb, ItemArmor itemArmor, ItemStack stack, EnumHandSide handSide) {
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
            if (this.texture.equals("item")) {
                switch (type) {
                    case NULL:
                        return armorLayer.getArmorResource(player, chestPlate, EntityEquipmentSlot.CHEST, null);
                    case OVERLAY:
                        if (itemArmor.hasOverlay(chestPlate)) return armorLayer.getArmorResource(player, chestPlate, EntityEquipmentSlot.CHEST, "overlay");
                }
                return null;
            }
            return Reflector.getPrivateField(ResourceManager.class, null, this.texture);
        }

        public static class BoneThingOBJ implements IBoneThing {
            public final ModelRendererObj obj;

            public BoneThingOBJ(ModelRendererObj obj) {
                this.obj = obj;
            }

            @Override
            public void setRotation(float x, float y, float z) {
                this.obj.rotateAngleX = x;
                this.obj.rotateAngleY = y;
                this.obj.rotateAngleZ = z;
            }

            @Override
            public void render(float scale) {
                this.obj.render(scale);
            }
        }

        public static class BoneThingT45 implements IBoneThing {
            private ModelRenderer mr;

            public BoneThingT45(ModelRenderer mr) {
                this.mr = mr;
            }

            @Override
            public void setRotation(float x, float y, float z) {
                this.mr.rotateAngleX = x;
                this.mr.rotateAngleY = y;
                this.mr.rotateAngleZ = z;
            }

            @Override
            public void render(float scale) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.13F, 1.13F, 1.13F);
                boolean h = this.mr.isHidden;
                boolean s = this.mr.showModel;
                this.mr.isHidden = false;
                this.mr.showModel = true;
                this.mr.render(scale);
                this.mr.isHidden = h;
                this.mr.showModel = s;
                GlStateManager.popMatrix();
            }
        }
    }
}
