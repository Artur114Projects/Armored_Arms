package com.artur114.armoredarms.client.integration;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import c4.conarm.client.models.ModelConstructsArmor;
import com.artur114.armoredarms.api.*;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import com.gildedgames.the_aether.api.AetherAPI;
import com.gildedgames.the_aether.api.player.IPlayerAether;
import com.gildedgames.the_aether.api.player.util.IAccessoryInventory;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.accessories.ItemAccessory;
import com.gildedgames.the_aether.items.accessories.ItemAccessoryDyable;
import com.gildedgames.the_aether.player.PlayerAether;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.render.model.ModelT45Chest;
import epicsquid.mysticallib.client.model.ModelArmorBase;
import galaxyspace.core.GSItems;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;
import micdoodle8.mods.galacticraft.api.item.IItemThermal;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC;
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerThermalPadding;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.venus.VenusItems;
import net.machinemuse.powersuits.client.model.item.armor.IArmorModel;
import net.machinemuse.powersuits.common.utils.nbt.MPSNBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.pabilo8.immersiveintelligence.client.model.armor.ModelLightEngineerArmor;
import pl.pabilo8.immersiveintelligence.client.util.tmt.ModelRendererTurbo;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.util.IIColor;
import pl.pabilo8.immersiveintelligence.common.util.IISkinHandler;

import java.util.List;

/**
 * Here i ultrashitcoding
 */
@Mod.EventBusSubscriber
public class Overriders {
    @SubscribeEvent
    public static void initArmorRenderLayer(InitArmorRenderLayerEvent e) {
        e.registerOverrider("immersiveintelligence", "light_engineer_armor_chestplate", new LightEngineerArmorOverrider(), false);
        e.registerOverrider("galaxyspace", "space_suit_chest", new SpaceSuitTier1Overrider(), false);
        e.registerOverrider("hbm", "t45_plate", new HBMOverrider("rightarm", "leftarm", "item"), false);
        e.registerOverrider("hbm", "ajr_plate", new HBMOverrider("rightArm", "leftArm", "ajr_arm"), false);
        e.registerOverrider("hbm", "ajro_plate", new HBMOverrider("rightArm", "leftArm", "ajro_arm"), false);
        e.registerOverrider("hbm", "hev_plate", new HBMOverrider("rightArm", "leftArm", "hev_arm"), false);
        e.registerOverrider("hbm", "bj_plate", new HBMOverrider("rightArm", "leftArm", "bj_arm"), false);
        e.registerOverrider("hbm", "bj_plate_jetpack", new HBMOverrider("rightArm", "leftArm", "bj_arm"), false);
        e.registerOverrider("hbm", "rpa_plate", new HBMOverrider("rightArm", "leftArm", "rpa_arm"), false);
        e.registerOverrider("hbm", "fau_plate", new HBMOverrider("rightArm", "leftArm", "fau_arm"), false);
        e.registerOverrider("hbm", "dns_plate", new HBMOverrider("rightArm", "leftArm", "dnt_arm"), false);
        e.registerOverrider("hbm", "steamsuit_plate", new HBMOverrider("rightArm", "leftArm", "steamsuit_arm"), false);
        e.registerOverrider("hbm", "trenchmaster_plate", new HBMOverrider("rightArm", "leftArm", "trenchmaster_arm"), false);
        e.registerOverrider("hbm", "taurun_plate", new HBMOverrider("rightArm", "leftArm", "taurun_arm"), false);
        e.registerOverrider("hbm", "dieselsuit_plate", new HBMOverrider("rightArm", "leftArm", "dieselsuit_arm"), false);
        e.registerOverrider("hbm", "envsuit_plate", new HBMOverrider("rightArm", "leftArm", "envsuit_arm"), false);
        e.registerOverrider("hbm", "bismuth_plate", new HBMOverrider("rightArm", "leftArm", "bismuth_arm"), false);
        e.registerOverrider("hbm", "t51_plate", new HBMOverrider("rightArm", "leftArm", "t51_arm"), false);
        e.registerOverrider("powersuits", "powerarmor_torso", new PowerArmorOverrider(), false);
        e.registerOverrider("cqrepoured", "chestplate_slime", new SlimeArmorOverrider(), false);
        e.registerOverrider("conarm", "*", new ConstructedArmorOverrider(), false);
        e.registerOverrider("roots", "*", new RootsOverrider(), false);

        e.addArmorToBlackList(AAConfig.renderBlackList);
    }

    @SubscribeEvent
    public static void initRenderLayers(InitRenderLayersEvent e) {
        e.addLayerIfModLoad(ThermalPaddingRenderLayer.class, "galacticraftcore");
        e.addLayerIfModLoad(AetherGlovesRenderLayer.class, "aether_legacy");
    }

    /**
     * for immersiveintelligence:light_engineer_armor_chestplate
      */
    public static class LightEngineerArmorOverrider implements IOverriderGetModel, IOverriderGetTex {
        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBase mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb instanceof ModelLightEngineerArmor) {
                return new ModelLightEngineerArmorOnlyArms((ModelLightEngineerArmor) mb);
            }
            return null;
        }

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type) {
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

        public static class ModelLightEngineerArmorOnlyArms implements IModelOnlyArms {
            public final ModelRenderer[] playerArms = MiscUtils.playerArms();
            private final ModelLightEngineerArmor model;
            private final ModelRendererTurbo[][] plates;
            private final ModelRendererTurbo[][] hand;
            private final ResourceLocation plateTex;
            private final ModelRenderer[] biped;

            public ModelLightEngineerArmorOnlyArms(ModelLightEngineerArmor model) {
                this.plates = new ModelRendererTurbo[][] {Reflector.getPrivateField(model, "platesLeftArmModel"), Reflector.getPrivateField(model, "platesRightArmModel")};
                this.plateTex = new ResourceLocation(Reflector.getPrivateField(model, "TEXTURE_PLATES"));
                this.hand = new ModelRendererTurbo[][] {model.leftArmModel, model.rightArmModel};
                this.biped = new ModelRenderer[] {model.bipedLeftArm, model.bipedRightArm};
                this.model = model;
            }

            @Override
            public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
                ModelRenderer biped = this.biped[side.ordinal()];
                float scale = 1.0F / 16.0F;
                biped.rotationPointX = -5.0F * MiscUtils.handSideDelta(side);
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
                this.render(this.hand[side.ordinal()], this.plates[side.ordinal()], stackArmor);
                GlStateManager.popMatrix();
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

            @Override
            public ModelBiped original() {
                return model;
            }
        }
    }

    /**
     *  for galaxyspace:space_suit_chest
     */
    public static class SpaceSuitTier1Overrider implements IOverriderGetTex, IOverriderGetModel {

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type) {
            return TextureMap.LOCATION_BLOCKS_TEXTURE;
        }

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb instanceof ItemSpaceSuitModel) {
                return new ModelOnlyArmsOBJArmor((ItemSpaceSuitModel) mb);
            }
            return null;
        }

        public static class ModelOnlyArmsOBJArmor implements IModelOnlyArms {
            public final ModelRenderer[] playerArms = MiscUtils.playerArms();
            public final float[] colors;
            public float x;
            public float y;
            public float z;

            public ModelOnlyArmsOBJArmor(ItemSpaceSuitModel model) {
                this.colors = model.color;
            }

            @Override
            public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
                GlStateManager.pushMatrix();
                int glList = side == EnumHandSide.RIGHT ? ItemSpaceSuitModel.rightArmList : ItemSpaceSuitModel.leftArmList;
                ModelRenderer pArm = this.playerArms[side.ordinal()];
                this.x = pArm.rotateAngleX;
                this.y = pArm.rotateAngleY;
                this.z = pArm.rotateAngleZ;
                GlStateManager.rotate(this.z * 57.295776F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(this.y * 57.295776F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(this.x * 57.295776F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(side == EnumHandSide.RIGHT ? (1.0F / 16.0F) : -(1.0F / 16.0F), -1.7F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.color(this.colors[0], this.colors[1], this.colors[2]);
                GlStateManager.callList(glList);
                GlStateManager.popMatrix();
            }

            @Override
            public ModelBiped original() {
                return null;
            }
        }
    }

    /**
     *  for hbm
     */
    public static class HBMOverrider implements IOverriderGetModel, IOverriderGetTex {
        private final String rightArm;
        private final String leftArm;
        private final String texture;

        public HBMOverrider(String rightArm, String leftArm, String texture) {
            this.rightArm = rightArm;
            this.leftArm = leftArm;
            this.texture = texture;
        }

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            Object r = Reflector.getPrivateField(mb, this.rightArm);
            Object l = Reflector.getPrivateField(mb, this.leftArm);
            if (Reflector.isClassExists("com.hbm.render.model.ModelT45Chest") && mb instanceof ModelT45Chest) {
                return new ModelOnlyArmsT45(mb, (ModelRenderer) r, (ModelRenderer) l);
            } else if (r instanceof ModelRenderer) {
                return new ArmRenderLayerArmor.DefaultModelOnlyArms(mb, (ModelRenderer) r, (ModelRenderer) l);
            } else if (r instanceof ModelRendererObj) {
                return new ModelOnlyArmsOBJ(mb, (ModelRendererObj) r, (ModelRendererObj) l);
            }
            return null;
        }

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type) {
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

        public static class ModelOnlyArmsOBJ implements IModelOnlyArms {
            public final ModelRenderer[] playerArms = MiscUtils.playerArms();
            public final ModelRendererObj[] arms;
            public final ModelBiped mb;

            public ModelOnlyArmsOBJ(ModelBiped mb, ModelRendererObj right, ModelRendererObj left) {
                this.arms = new ModelRendererObj[] {left, right};
                this.mb = mb;
            }

            @Override
            public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
                ModelRendererObj arm = this.arms[side.ordinal()];
                ModelRenderer pArm = this.playerArms[side.ordinal()];
                arm.rotationPointX = -5.0F * MiscUtils.handSideDelta(side);
                arm.rotationPointY = 2.0F;
                arm.rotationPointZ = 0.0F;
                arm.rotateAngleX = pArm.rotateAngleX;
                arm.rotateAngleY = pArm.rotateAngleY;
                arm.rotateAngleZ = pArm.rotateAngleZ;
                arm.offsetX = pArm.offsetX;
                arm.offsetY = pArm.offsetY;
                arm.offsetZ = pArm.offsetZ;
                arm.render(1.0F / 16.0F);
            }

            @Override
            public ModelBiped original() {
                return this.mb;
            }
        }

        public static class ModelOnlyArmsT45 extends ArmRenderLayerArmor.DefaultModelOnlyArms {
            public ModelOnlyArmsT45(ModelBiped mb, ModelRenderer right, ModelRenderer left) {
                super(mb, right, left);
            }

            @Override
            public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.13F, 1.13F, 1.13F);
                super.renderArm(player, itemArmor, stackArmor, side);
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * for powersuits:powerarmor_torso
     */
    public static class PowerArmorOverrider implements IOverriderRender {
        private final Minecraft mc = Minecraft.getMinecraft();

        @Override
        public void render(IModelOnlyArms arms, ResourceLocation tex, EnumHandSide handSide, ItemStack stackArmor, ItemArmor itemArmor, EnumRenderType type) {
            if (arms == null || tex == null || stackArmor == null || itemArmor == null || stackArmor.isEmpty() || arms.original() == null) {
                return;
            }
            ModelBiped model = arms.original();
            if (type == EnumRenderType.ARMOR || type == EnumRenderType.ARMOR_OVERLAY) {
                ((IArmorModel) model).setRenderSpec(MPSNBTUtils.getMuseRenderTag(stackArmor, EntityEquipmentSlot.CHEST));
                ((IArmorModel) model).setVisibleSection(EntityEquipmentSlot.CHEST);
            }
            switch (type) {
                case ARMOR_ENCHANT:
                    this.renderEnchant(arms, tex, stackArmor, itemArmor, handSide);
                    break;
                case ARMOR:
                    this.renderArmor(arms, tex, stackArmor, itemArmor, handSide);
                    break;
                case ARMOR_OVERLAY:
                    this.render(arms, tex, stackArmor, itemArmor, handSide);
                    break;
            }
        }

        private void renderArmor(IModelOnlyArms arms, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor, EnumHandSide handSide) {
            if (itemArmor.hasOverlay(stackArmor)) {
                int i = itemArmor.getColor(stackArmor);
                float r = (float) (i >> 16 & 255) / 255.0F;
                float g = (float) (i >> 8 & 255) / 255.0F;
                float b = (float) (i & 255) / 255.0F;
                GlStateManager.color(r, g, b, 1.0F);
                this.render(arms, tex, stackArmor, itemArmor, handSide);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.render(arms, tex, stackArmor, itemArmor, handSide);
            }
        }

        private void render(IModelOnlyArms arms, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor, EnumHandSide handSide) {
            this.mc.getTextureManager().bindTexture(tex);
            arms.renderArm(this.mc.player, itemArmor, stackArmor, handSide);
        }

        private void renderEnchant(IModelOnlyArms arms, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor, EnumHandSide handSide) {
            if (!stackArmor.hasEffect()) {
                return;
            }
            float f = (float) this.mc.player.ticksExisted;
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            this.mc.getTextureManager().bindTexture(tex);
            this.mc.entityRenderer.setupFogColor(true);
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

            for (int i = 0; i < 2; ++i) {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
                arms.renderArm(this.mc.player, itemArmor, stackArmor, handSide);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            this.mc.entityRenderer.setupFogColor(false);
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        }
    }

    /**
     * for conarm:*
     */
    public static class ConstructedArmorOverrider implements IOverriderGetModel {

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, null);
            if (mb == null) {
                return null;
            }
            if (mb instanceof ModelConstructsArmor && mb.bipedRightArm != ((ModelConstructsArmor) mb).armRightAnchor) {
                mb.bipedRightArm = ((ModelConstructsArmor) mb).armRightAnchor;
                mb.bipedLeftArm = ((ModelConstructsArmor) mb).armLeftAnchor;
                mb.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F, player);
            }
            return new ArmRenderLayerArmor.DefaultModelOnlyArms(mb);
        }
    }

    /**
     * for roots:*
     */
    public static class RootsOverrider extends ArmRenderLayerArmor.DefaultModelGetter {
        public RootsOverrider() {
            this.setFactory((modelBiped -> {
                if (modelBiped instanceof ModelArmorBase) {
                    return new RootsModelOnlyArms((ModelArmorBase) modelBiped);
                }
                return null;
            }));
        }

        public static class RootsModelOnlyArms implements IModelOnlyArms {
            public final ModelRenderer[] playerArms = MiscUtils.playerArms();
            public final ModelRenderer[] armsB;
            public final ModelRenderer[] arms;
            public final ModelArmorBase mb;

            public RootsModelOnlyArms(ModelArmorBase mb) {
                this.arms = new ModelRenderer[] {Reflector.getPrivateField(ModelArmorBase.class, mb, "armL"), Reflector.getPrivateField(ModelArmorBase.class, mb, "armR")};
                this.armsB = new ModelRenderer[] {mb.bipedLeftArm, mb.bipedRightArm};
                this.mb = mb;
            }

            @Override
            public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
                ModelRenderer armB = this.armsB[side.ordinal()];
                ModelRenderer arm = this.arms[side.ordinal()];
                armB.rotationPointX = -5.0F * MiscUtils.handSideDelta(side);
                armB.rotationPointY = 2.0F;
                armB.rotationPointZ = 0.0F;
                MiscUtils.setPlayerArmDataToArm(armB, this.playerArms[side.ordinal()]);
                this.mb.setChestRotation(player);
                boolean h = arm.isHidden;
                boolean s = arm.showModel;
                arm.isHidden = false;
                arm.showModel = true;
                arm.render((1.0F / 16.0F) * 1.05F);
                arm.isHidden = h;
                arm.showModel = s;
            }

            @Override
            public ModelBiped original() {
                return mb;
            }
        }
    }

    public static class SlimeArmorOverrider extends ArmRenderLayerArmor.DefaultModelGetter {
        public SlimeArmorOverrider() {
            this.setFactory((modelBiped -> new SlimeModelOnlyArms(modelBiped, modelBiped.bipedRightArm, modelBiped.bipedLeftArm)));
        }

        public static class SlimeModelOnlyArms extends ArmRenderLayerArmor.DefaultModelOnlyArms {
            public SlimeModelOnlyArms(ModelBiped mb, ModelRenderer right, ModelRenderer left) {
                super(mb, right, left);
            }

            @Override
            public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                super.renderArm(player, itemArmor, stackArmor, side);
                GlStateManager.disableBlend();
            }
        }
    }

    /**
     * for thermal padding from galactic craft
     */
    public static class ThermalPaddingRenderLayer implements IArmRenderLayer {
        private final ResourceLocation texture_t3 = new ResourceLocation("galaxyspace", "textures/model/armor/thermal_padding_t3_1.png");
        private final ResourceLocation texture_t4 = new ResourceLocation("galaxyspace", "textures/model/armor/thermal_padding_t4_1.png");
        private LayerThermalPadding renderTermal;
        private RenderPlayer renderPlayer;
        private boolean render = false;

        @Override
        public void update(AbstractClientPlayer player) {
            PlayerGearData gearData = GalacticraftCore.proxy.getGearData(player);
            if (gearData != null) {
                this.render = gearData.getThermalPadding(1) != -1;
            }
        }

        @Override
        public void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide) {
            Item item = this.getItemStackFromSlot(player, EntityEquipmentSlot.CHEST);
            float scale = 1.0F / 16.0F;
            if (item != null) {
                ModelBiped model = this.renderTermal.getModelFromSlot(EntityEquipmentSlot.CHEST);
                ModelRenderer renderer = MiscUtils.handFromModelBiped(model, handSide);
                if (item instanceof IItemThermal) {
                    IItemThermal itemT = (IItemThermal) item;
                    switch (itemT.getThermalStrength()) {
                        case 1:
                            this.renderPlayer.bindTexture(RenderPlayerGC.thermalPaddingTexture1);
                            break;
                        case 2:
                            this.renderPlayer.bindTexture(RenderPlayerGC.thermalPaddingTexture1_T2);
                            break;
                        case 3:
                            this.renderPlayer.bindTexture(this.texture_t3);
                            break;
                        case 4:
                            this.renderPlayer.bindTexture(this.texture_t4);
                            break;
                    }
                }

                renderer.rotationPointX = -5.0F * MiscUtils.handSideDelta(handSide);
                renderer.rotationPointY = 2.0F;
                renderer.rotationPointZ = 0.0F;
                MiscUtils.setPlayerArmDataToArm(renderer, MiscUtils.handFromModelBiped(this.renderPlayer.getMainModel(), handSide));

                boolean h = renderer.isHidden;
                boolean s = renderer.showModel;
                renderer.isHidden = false;
                renderer.showModel = true;
                renderer.render(scale);
                renderer.isHidden = h;
                renderer.showModel = s;
                GlStateManager.disableLighting();
                Minecraft.getMinecraft().renderEngine.bindTexture(RenderPlayerGC.thermalPaddingTexture0);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                float time = (float)player.ticksExisted / 10.0F;
                float sTime = (float)Math.sin(time) * 0.5F + 0.5F;
                float r = 0.2F * sTime;
                float g = 1.0F * sTime;
                float b = 0.2F * sTime;
                if (player.world.provider instanceof IGalacticraftWorldProvider) {
                    float modifier = ((IGalacticraftWorldProvider) player.world.provider).getThermalLevelModifier();
                    if (modifier > 0.0F) {
                        b = g;
                        g = r;
                    } else if (modifier < 0.0F) {
                        r = g;
                        g = b;
                    }
                }
                GlStateManager.color(r, g, b, 0.4F * sTime);
                h = renderer.isHidden;
                s = renderer.showModel;
                renderer.isHidden = false;
                renderer.showModel = true;
                renderer.render(scale);
                renderer.isHidden = h;
                renderer.showModel = s;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableLighting();
            }
        }

        @Override
        public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
            return this.render;
        }

        @Override
        public void init(AbstractClientPlayer player) {
            this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
            this.renderTermal = new LayerThermalPadding(this.renderPlayer);
        }

        public Item getItemStackFromSlot(EntityLivingBase living, EntityEquipmentSlot slotIn) {
            PlayerGearData gearData = GalacticraftCore.proxy.getGearData((EntityPlayer)living);
            if (gearData != null) {
                int padding = gearData.getThermalPadding(1);
                if (padding != -1) {
                    switch (padding) {
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            return AsteroidsItems.thermalPadding;
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                            return VenusItems.thermalPaddingTier2;
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                            return GSItems.THERMAL_PADDING_3;
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                            return GSItems.THERMAL_PADDING_4;
                    }
                }
            }

            return null;
        }
    }

    /**
     * for all gloves from the aether
     */
    public static class AetherGlovesRenderLayer implements IArmRenderLayer {
        private RenderPlayer renderPlayer;
        public ModelBiped modelMisc = null;
        private double modelSize = -1.0D;
        private boolean render = false;

        @Override
        public void update(AbstractClientPlayer player) {
            IPlayerAether playerAether = AetherAPI.getInstance().get(player);
            IAccessoryInventory accessories = playerAether.getAccessoryInventory();
            boolean flag = !accessories.getStackInSlot(6).isEmpty() && ((PlayerAether)playerAether).shouldRenderGloves;
            if (flag && this.modelSize != AAConfig.vanillaArmorModelSize) {
                this.modelMisc = new ModelBiped(((float) AAConfig.vanillaArmorModelSize + 0.01F));
                this.modelSize = AAConfig.vanillaArmorModelSize;
            }
            this.render = flag;
        }

        @Override
        public void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide) {
            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            IPlayerAether playerAether = AetherAPI.getInstance().get(player);
            IAccessoryInventory accessories = playerAether.getAccessoryInventory();
            ModelRenderer renderer = MiscUtils.handFromModelBiped(this.modelMisc, handSide);

            float scale = 1.0F / 16.0F;

            GlStateManager.pushMatrix();

            if (accessories.getStackInSlot(6).getItem().getClass() == ItemAccessory.class && ((PlayerAether)playerAether).shouldRenderGloves) {
                ItemAccessory shield = (ItemAccessory)accessories.getStackInSlot(6).getItem();
                manager.renderEngine.bindTexture(shield.texture);
                int j = shield.getColorFromItemStack(accessories.getStackInSlot(6), 0);
                float red = (float)(j >> 16 & 255) / 255.0F;
                float green = (float)(j >> 8 & 255) / 255.0F;
                red = (float)(j & 255) / 255.0F;
                if (shield != ItemsAether.phoenix_gloves) {
                    GlStateManager.color(red, green, red);
                }

                renderer.rotationPointX = -5.0F * MiscUtils.handSideDelta(handSide);
                renderer.rotationPointY = 2.0F;
                renderer.rotationPointZ = 0.0F;
                MiscUtils.setPlayerArmDataToArm(renderer, MiscUtils.handFromModelBiped(this.renderPlayer.getMainModel(), handSide));

                boolean h = renderer.isHidden;
                boolean s = renderer.showModel;
                renderer.isHidden = false;
                renderer.showModel = true;
                renderer.render(scale);
                renderer.isHidden = h;
                renderer.showModel = s;

                GlStateManager.color(1.0F, 1.0F, 1.0F);
            } else if (accessories.getStackInSlot(6).getItem().getClass() == ItemAccessoryDyable.class && ((PlayerAether)playerAether).shouldRenderGloves) {
                ItemAccessoryDyable gloves = (ItemAccessoryDyable)accessories.getStackInSlot(6).getItem();
                manager.renderEngine.bindTexture(gloves.texture);
                int j = gloves.getColor(accessories.getStackInSlot(6));
                float red = (float)(j >> 16 & 255) / 255.0F;
                float green = (float)(j >> 8 & 255) / 255.0F;
                red = (float)(j & 255) / 255.0F;
                GlStateManager.color(red, green, red);

                renderer.rotationPointX = -5.0F * MiscUtils.handSideDelta(handSide);
                renderer.rotationPointY = 2.0F;
                renderer.rotationPointZ = 0.0F;
                MiscUtils.setPlayerArmDataToArm(renderer, MiscUtils.handFromModelBiped(this.renderPlayer.getMainModel(), handSide));

                boolean h = renderer.isHidden;
                boolean s = renderer.showModel;
                renderer.isHidden = false;
                renderer.showModel = true;
                renderer.render(scale);
                renderer.isHidden = h;
                renderer.showModel = s;

                GlStateManager.color(1.0F, 1.0F, 1.0F);
            }

            GlStateManager.popMatrix();
        }

        @Override
        public void init(AbstractClientPlayer player) {
            this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
        }

        @Override
        public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
            return this.render;
        }
    }
}
