package com.artur114.armoredarms.client.integration;


import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.api.IOverriderGetModel;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.EnumHandSide;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.accessories.ItemAccessory;
import com.gildedgames.the_aether.player.PlayerAether;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
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
    
    public static class AetherGlovesRenderLayer implements IArmRenderLayer {
        private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
        public final ModelRenderer[] playerArms = MiscUtils.playerArms();
        private double modelSize = AAConfig.vanillaArmorModelSize - 1;
        public final Minecraft mc = Minecraft.getMinecraft();
        public RenderPlayer renderPlayer = null;
        public ItemStack gloves = null;

        @Override
        public void update(AbstractClientPlayer player) {
            PlayerAether playerAether = PlayerAether.get(player);
            this.gloves = playerAether.getAccessoryInventory().getStackInSlot(AccessoryType.GLOVES);
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
