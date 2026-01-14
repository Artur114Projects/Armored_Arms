package com.artur114.armoredarms.client.integration;


import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.api.IOverriderGetModel;
import com.artur114.armoredarms.api.IOverriderGetTex;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.EnumHandSide;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.api.player.IPlayerAether;
import com.gildedgames.the_aether.api.player.util.IAccessoryInventory;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.accessories.ItemAccessory;
import com.gildedgames.the_aether.player.PlayerAether;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.render.model.ModelT45Chest;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

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

        e.registerOverrider("hbm", "item.t45_plate", new HBMOverrider("rightarm", "leftarm", "item"), false);
        e.registerOverrider("hbm", "item.ajr_plate", new HBMOverrider("rightAr m", "leftArm", "ajr_arm"), false);
        e.registerOverrider("hbm", "item.ajro_plate", new HBMOverrider("rightArm", "leftArm", "ajro_arm"), false);
        e.registerOverrider("hbm", "item.hev_plate", new HBMOverrider("rightArm", "leftArm", "hev_arm"), false);
        e.registerOverrider("hbm", "item.bj_plate", new HBMOverrider("rightArm", "leftArm", "bj_arm"), false);
        e.registerOverrider("hbm", "item.bj_plate_jetpack", new HBMOverrider("rightArm", "leftArm", "bj_arm"), false);
        e.registerOverrider("hbm", "item.rpa_plate", new HBMOverrider("rightArm", "leftArm", "rpa_arm"), false);
        e.registerOverrider("hbm", "item.fau_plate", new HBMOverrider("rightArm", "leftArm", "fau_arm"), false);
        e.registerOverrider("hbm", "item.dns_plate", new HBMOverrider("rightArm", "leftArm", "dnt_arm"), false);
        e.registerOverrider("hbm", "item.steamsuit_plate", new HBMOverrider("rightArm", "leftArm", "steamsuit_arm"), false);
        e.registerOverrider("hbm", "item.trenchmaster_plate", new HBMOverrider("rightArm", "leftArm", "trenchmaster_arm"), false);
        e.registerOverrider("hbm", "item.taurun_plate", new HBMOverrider("rightArm", "leftArm", "taurun_arm"), false);
        e.registerOverrider("hbm", "item.dieselsuit_plate", new HBMOverrider("rightArm", "leftArm", "dieselsuit_arm"), false);
        e.registerOverrider("hbm", "item.envsuit_plate", new HBMOverrider("rightArm", "leftArm", "envsuit_arm"), false);
        e.registerOverrider("hbm", "item.bismuth_plate", new HBMOverrider("rightArm", "leftArm", "armor_bismuth_tex"), false);
        e.registerOverrider("hbm", "item.t51_plate", new HBMOverrider("rightArm", "leftArm", "t51_arm"), false);

        e.addArmorToBlackList(AAConfig.renderBlackList);
    }

    @SubscribeEvent
    public void initRenderLayers(InitRenderLayersEvent e) {
        e.addLayerIfModLoad(AetherGlovesRenderLayer.class, "aether_legacy");
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
            ModelBiped mb = ForgeHooksClient.getArmorModel(player, stack, ArmoredArms.CHEST_PLATE_ID, null);
            if (mb == null) {
                return null;
            }
            Object r = Reflector.getPrivateField(mb, this.rightArm);
            Object l = Reflector.getPrivateField(mb, this.leftArm);
            if (mb instanceof ModelT45Chest) {
                return new ModelOnlyArmsT45(mb, (ModelRenderer) r, (ModelRenderer) l);
            } else if (r instanceof ModelRenderer) {
                return new ArmRenderLayerArmor.DefaultModelOnlyArms(mb, (ModelRenderer) r, (ModelRenderer) l);
            } else if (r instanceof ModelRendererObj) {
                return new ModelOnlyArmsOBJ(mb, (ModelRendererObj) r, (ModelRendererObj) l);
            }
            return null;
        }

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type) {
            if (this.texture.equals("item")) {
                switch (type) {
                    case NULL:
                        return RenderBiped.getArmorResource(player, chestPlate, ArmoredArms.CHEST_PLATE_ID, null);
                    case OVERLAY:
                        if (itemArmor.getColor(chestPlate) != -1) return RenderBiped.getArmorResource(player, chestPlate, ArmoredArms.CHEST_PLATE_ID, "overlay");
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
                this.arms = new ModelRendererObj[] {right, left};
                this.mb = mb;
            }

            @Override
            public void renderArm(AbstractClientPlayer player, EnumHandSide side, ItemArmor itemArmor, ItemStack stackArmor) {
                ModelRendererObj arm = this.arms[side.ordinal()];
                ModelRenderer pArm = this.playerArms[side.ordinal()];
                arm.rotationPointX = -5.0F * side.delta();
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
            public void renderArm(AbstractClientPlayer player, EnumHandSide side, ItemArmor itemArmor, ItemStack stackArmor) {
                GL11.glPushMatrix();
                GL11.glScalef(1.125F, 1.125F, 1.125F);
                super.renderArm(player, side, itemArmor, stackArmor);
                GL11.glPopMatrix();
            }
        }
    }

    public static class AetherGlovesRenderLayer implements IArmRenderLayer {
        private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
        public final ModelRenderer[] playerArms = MiscUtils.playerArms();
        private double modelSize = AAConfig.vanillaArmorModelSize - 1;
        public final Minecraft mc = Minecraft.getMinecraft();
        public RenderPlayer renderPlayer = null;
        public ItemStack gloves = null;

        @Override
        public void update(AbstractClientPlayer player) {
            String method = "getStackInSlot";
            try {
                Class.forName("com.gildedgames.the_aether.api.accessories.DegradationRate");
                method = "getFirstStackIfWearing";
            } catch (ClassNotFoundException ignored) {}

            PlayerAether playerAether = PlayerAether.get(player);
            this.gloves = Reflector.invokeMethod(IAccessoryInventory.class, playerAether.getAccessoryInventory(), method, new Class[] {AccessoryType.class}, new Object[] {AccessoryType.GLOVES});

            if (this.modelSize != AAConfig.vanillaArmorModelSize) {
                this.defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
                this.modelSize = AAConfig.vanillaArmorModelSize;
                this.defaultModel.swingProgress = 0.0F;
                this.defaultModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
            }
        }

        @Override
        public void renderTransformed(AbstractClientPlayer player, EnumHandSide side) {
            this.mc.getTextureManager().bindTexture(player.getLocationSkin());
            if (this.gloves != null) {
                if (this.gloves.getItem() instanceof ItemAccessory) {
                    this.mc.getTextureManager().bindTexture(((ItemAccessory)this.gloves.getItem()).texture);
                    int colour = gloves.getItem().getColorFromItemStack(this.gloves, 0);
                    float red = (float)(colour >> 16 & 255) / 255.0F;
                    float green = (float)(colour >> 8 & 255) / 255.0F;
                    float blue = (float)(colour & 255) / 255.0F;
                    if (this.gloves.getItem() != ItemsAether.phoenix_gloves) {
                        GL11.glColor3f(red, green, blue);
                    }

                    GL11.glEnable(3042);
                    ModelRenderer arm = side.handFromModelBiped(this.defaultModel);
                    arm.rotationPointX = -5.0F * side.delta();
                    arm.rotationPointY = 2.0F;
                    arm.rotationPointZ = 0.0F;
                    MiscUtils.setPlayerArmDataToArm(arm, this.playerArms[side.ordinal()]);
                    arm.render(0.0625F);
                    GL11.glDisable(3042);
                    GL11.glColor3f(1.0F, 1.0F, 1.0F);
                }
            }
        }

        @Override
        public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
            return this.gloves != null;
        }

        @Override
        public void init(AbstractClientPlayer player) {
            this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
        }
    }
}
