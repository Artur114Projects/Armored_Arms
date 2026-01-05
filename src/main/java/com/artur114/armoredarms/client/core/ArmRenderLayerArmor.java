package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.*;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.client.util.Function2;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.RMException;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ArmRenderLayerArmor implements IArmRenderLayer {
    public static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public final DefaultTextureGetter defaultTextureGetter = new DefaultTextureGetter();
    public final DefaultModelGetter defaultModelGetter = new DefaultModelGetter();
    public List<LayerRenderer<AbstractClientPlayer>> layerRenderers = null;
    public Map<ShapelessRL, IOverriderGetModel> modelOverriders = null;
    public Map<ShapelessRL, IOverriderGetTex> textureOverriders = null;
    public Map<ShapelessRL, IOverriderRender> renderOverriders = null;
    public final DefaultRender defaultRender = new DefaultRender();
    public final Minecraft mc = Minecraft.getMinecraft();
    public List<ShapelessRL> renderBlackList = null;
    public Set<Item> killingArmor = new HashSet<>();
    public LayerBipedArmor armorLayer = null;
    public RenderPlayer renderPlayer = null;
    public boolean render = false;


    public ItemStack chestPlate = null;
    public ItemArmor chestPlateItem = null;
    public IModelOnlyArms currentArmorModel = null;
    public ResourceLocation currentArmorTex = null;
    public ResourceLocation currentArmorTexOv = null;
    public IOverriderGetTex currentGetTexOverrider = null;
    public IOverriderRender currentRenderOverrider = null;
    public IOverriderGetModel currentGetModelOverrider = null;

    @Override
    public void update(AbstractClientPlayer player) {
        try {
            this.tryTick(player);
        } catch (RMException rm) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw rm.setMessage("armoredarms.error.layer.armor");
        } catch (Throwable tr) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw new RMException(tr).setMessage("armoredarms.error.layer.armor");
        }
    }

    @Override
    public void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide) {
        try {
            this.tryRender(player, handSide);
        } catch (RMException rm) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw rm.setMessage("armoredarms.error.layer.armor");
        } catch (Throwable tr) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw new RMException(tr).setMessage("armoredarms.error.layer.armor");
        }
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return this.render;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderPlayer = this.initRenderPlayer(player);
        this.layerRenderers = this.initLayerRenderers(player);
        this.armorLayer = this.findArmorLayer();

        this.initEvent();
    }

    public void tryTick(AbstractClientPlayer player) {
        ItemStack chestPlate = this.itemStackArmor(player);

        if (this.killingArmor.contains(chestPlate.getItem()) || !(chestPlate.getItem() instanceof ItemArmor)) {
            this.chestPlate = ItemStack.EMPTY;
            this.chestPlateItem = null;
            this.render = false;
            return;
        }

        if (this.checkIsNotNew(chestPlate)) {
            return;
        }

        if (this.renderBlackList.contains(new ShapelessRL(chestPlate.getItem().getRegistryName()))) {
            this.chestPlateItem = (ItemArmor) chestPlate.getItem();
            this.chestPlate = chestPlate;
            this.render = false;
            return;
        }

        this.render = true;
        this.chestPlate = chestPlate;
        this.chestPlateItem = (ItemArmor) chestPlate.getItem();

        this.updateOverriders();

        this.currentArmorModel = this.getArmorModel(player);
        this.currentArmorTexOv = this.getArmorTex(player, IOverriderGetTex.EnumTexType.OVERLAY);
        this.currentArmorTex = this.getArmorTex(player, IOverriderGetTex.EnumTexType.NULL);
    }

    public void tryRender(AbstractClientPlayer player, EnumHandSide handSide) {
        ResourceLocation armorTexOv = this.currentArmorTexOv;
        IModelOnlyArms armorModel = this.currentArmorModel;
        ResourceLocation armorTex = this.currentArmorTex;

        this.render(armorModel, armorTexOv, handSide, IOverriderRender.EnumRenderType.ARMOR_OVERLAY);
        this.render(armorModel, armorTex, handSide, IOverriderRender.EnumRenderType.ARMOR);

        this.render(armorModel, ENCHANTED_ITEM_GLINT_RES, handSide, IOverriderRender.EnumRenderType.ARMOR_ENCHANT);
    }

    public ItemStack itemStackArmor(AbstractClientPlayer player) {
//        if (Loader.isModLoaded("cosmeticarmorreworked")) {
//            CAStacksBase stacks = CosArmorAPI.getCAStacksClient(player.getUniqueID());
//            int chestId = EntityEquipmentSlot.CHEST.getIndex();
//
//            if (stacks.isSkinArmor(chestId)) {
//                return ItemStack.EMPTY;
//            }
//
//            ItemStack stack = stacks.getStackInSlot(chestId);
//
//            if (!stack.isEmpty()) {
//                return stack;
//            }
//        }

        return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    }

    private void initEvent() {
        InitArmorRenderLayerEvent event = new InitArmorRenderLayerEvent();
        MinecraftForge.EVENT_BUS.post(event);

        this.textureOverriders = event.textureOverriders();
        this.renderOverriders = event.renderOverriders();
        this.modelOverriders = event.modelOverriders();

        this.renderBlackList = event.renderBlackList();
    }

    @SuppressWarnings("unchecked")
    public List<LayerRenderer<AbstractClientPlayer>> initLayerRenderers(AbstractClientPlayer player) {
        try {
            Field field = null;
            Field[] fields = RenderLivingBase.class.getDeclaredFields();

            for (Field rField : fields) {
                boolean isAcc = rField.isAccessible();
                rField.setAccessible(true);
                if (rField.get(this.renderPlayer) instanceof List) {
                    field = rField;
                }
                rField.setAccessible(isAcc);
            }

            if (field == null) {
                throw new RMException("layerRenderers is not find!").setFatalLayer(this);
            }

            boolean isAcc = field.isAccessible();
            field.setAccessible(true);
            List<LayerRenderer<AbstractClientPlayer>> layers = (List<LayerRenderer<AbstractClientPlayer>>) field.get(this.renderPlayer);
            field.setAccessible(isAcc);
            return layers;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LayerBipedArmor findArmorLayer() {
        for (LayerRenderer<?> layerRenderer : this.layerRenderers) {
            if (layerRenderer instanceof LayerBipedArmor) {
                return (LayerBipedArmor) layerRenderer;
            }
        }
        throw new IllegalStateException();
    }

    public RenderPlayer initRenderPlayer(AbstractClientPlayer player) {
        return (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
    }

    public void updateOverriders() {
        ShapelessRL rl = new ShapelessRL(this.chestPlateItem.getRegistryName());

        AtomicReference<IOverriderRender> overriderRender = new AtomicReference<>(this.renderOverriders.get(rl));
        AtomicReference<IOverriderGetTex> overriderGetTex = new AtomicReference<>(this.textureOverriders.get(rl));
        AtomicReference<IOverriderGetModel> overriderGetModel = new AtomicReference<>(this.modelOverriders.get(rl));

        this.findOverrider(overriderRender, this.renderOverriders, rl);
        this.findOverrider(overriderGetTex, this.textureOverriders, rl);
        this.findOverrider(overriderGetModel, this.modelOverriders, rl);

        if (overriderRender.get() == null) overriderRender.set(this.defaultRender);
        if (overriderGetTex.get() == null) overriderGetTex.set(this.defaultTextureGetter);
        if (overriderGetModel.get() == null) overriderGetModel.set(this.defaultModelGetter);

        this.currentRenderOverrider = overriderRender.get();
        this.currentGetTexOverrider = overriderGetTex.get();
        this.currentGetModelOverrider = overriderGetModel.get();
    }

    public <T extends IOverrider> void findOverrider(AtomicReference<T> overrider, Map<ShapelessRL, T> map, ShapelessRL rl) {
        if (overrider.get() == null) {
            map.forEach((k, v) -> {
                if (k.equals(rl)) {
                    if (k.isAbsoluteShapeless() && overrider.get() != null) {
                        return;
                    }
                    overrider.set(v);
                }
            });
        }
    }

    public boolean checkIsNotNew(ItemStack stack) {
        if (AAConfig.useCheckByItem) {
            return stack.getItem() == this.chestPlateItem;
        } else {
            return stack == this.chestPlate;
        }
    }

    public void render(IModelOnlyArms arms, ResourceLocation tex, EnumHandSide handSide, IOverriderRender.EnumRenderType type) {
        this.currentRenderOverrider.render(arms, tex, handSide, this.chestPlate, this.chestPlateItem, type);
    }

    public IModelOnlyArms getArmorModel(AbstractClientPlayer player) {
        return this.currentGetModelOverrider.getModel(player, this.chestPlateItem, this.chestPlate);
    }

    public ResourceLocation getArmorTex(AbstractClientPlayer player, IOverriderGetTex.EnumTexType type) {
        return this.currentGetTexOverrider.getTexture(player, this.armorLayer, this.layerRenderers, this.chestPlate, this.chestPlateItem, type);
    }

    public static class DefaultRender implements IOverriderRender {
        private final Minecraft mc = Minecraft.getMinecraft();

        @Override
        public void render(IModelOnlyArms arms, ResourceLocation tex, EnumHandSide handSide, ItemStack stackArmor, ItemArmor itemArmor, EnumRenderType type) {
            if (arms == null || tex == null) {
                return;
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

    public static class DefaultModelGetter implements IOverriderGetModel {
        private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
        private Function2<ModelBiped, EnumHandSide, ModelRenderer> extractor;
        private double modelSize = AAConfig.vanillaArmorModelSize;
        private Function<ModelBiped, IModelOnlyArms> factory;

        public DefaultModelGetter() {
            this.extractor = ((modelBase, handSide) -> {
                switch (handSide) {
                    case RIGHT:
                        return modelBase.bipedRightArm;
                    case LEFT:
                        return modelBase.bipedLeftArm;
                    default:
                        return null;
                }
            });
            this.factory = (modelBiped -> new DefaultModelOnlyArms(modelBiped, this.extractor()));
        }

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, this.defaultModel);
            if (mb == null) {
                if (this.modelSize != AAConfig.vanillaArmorModelSize) {
                    this.defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
                    this.modelSize = AAConfig.vanillaArmorModelSize;
                }
                mb = this.defaultModel;
            }
            return this.factory.apply(mb);
        }

        private Function2<ModelBiped, EnumHandSide, ModelRenderer> extractor() {
            return this.extractor;
        }

        protected void setArmsExtractor(Function2<ModelBiped, EnumHandSide, ModelRenderer> extractor) {
            this.extractor = extractor;
        }

        protected void setFactory(Function<ModelBiped, IModelOnlyArms> factory) {
            this.factory = factory;
        }
    }

    public static class DefaultTextureGetter implements IOverriderGetTex {

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type) {
            switch (type) {
                case NULL:
                    return armorLayer.getArmorResource(player, chestPlate, EntityEquipmentSlot.CHEST, null);
                case OVERLAY:
                    if (itemArmor.hasOverlay(chestPlate)) return armorLayer.getArmorResource(player, chestPlate, EntityEquipmentSlot.CHEST, "overlay");
            }
            return null;
        }
    }

    public static class DefaultModelOnlyArms implements IModelOnlyArms {
        public final ModelRenderer[] playerArms = MiscUtils.playerArms();
        public final ModelRenderer[] arms;
        public final ModelBiped mb;

        public DefaultModelOnlyArms(ModelBiped mb) {
            this.arms = new ModelRenderer[] {mb.bipedLeftArm, mb.bipedRightArm};
            this.mb = mb;
        }

        public DefaultModelOnlyArms(ModelBiped mb, Function2<ModelBiped, EnumHandSide, ModelRenderer> armsExtractor) {
            this.arms = new ModelRenderer[] {armsExtractor.apply(mb, EnumHandSide.LEFT), armsExtractor.apply(mb, EnumHandSide.RIGHT)};
            this.mb = mb;
        }

        public DefaultModelOnlyArms(ModelBiped mb, ModelRenderer right, ModelRenderer left) {
            this.arms = new ModelRenderer[] {left, right};
            this.mb = mb;
        }

        @Override
        public void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side) {
            ModelRenderer arm = this.arms[side.ordinal()];
            arm.rotationPointX = -5.0F * MiscUtils.handSideDelta(side);
            arm.rotationPointY = 2.0F;
            arm.rotationPointZ = 0.0F;
            MiscUtils.setPlayerArmDataToArm(arm, this.playerArms[side.ordinal()]);
            boolean h = arm.isHidden;
            boolean s = arm.showModel;
            arm.isHidden = false;
            arm.showModel = true;
            arm.render(1.0F / 16.0F);
            arm.isHidden = h;
            arm.showModel = s;
        }

        @Override
        public ModelBiped original() {
            return mb;
        }
    }
}
