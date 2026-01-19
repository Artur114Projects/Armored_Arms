package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.*;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.client.util.*;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class ArmRenderLayerArmor implements IArmRenderLayer {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    public final DefaultTextureGetter defaultTextureGetter = new DefaultTextureGetter();
    public final DefaultModelGetter defaultModelGetter = new DefaultModelGetter();
    public Map<ShapelessRL, IOverriderGetModel> modelOverriders = null;
    public Map<ShapelessRL, IOverriderGetTex> textureOverriders = null;
    public Map<ShapelessRL, IOverriderRender> renderOverriders = null;
    public final DefaultRender defaultRender = new DefaultRender();
    public final Minecraft mc = Minecraft.getInstance();
    public List<ShapelessRL> renderBlackList = null;
    public Set<Item> killingArmor = new HashSet<>();
    public PlayerRenderer renderPlayer = null;
    public boolean render = false;


    public ItemStack chestPlate = null;
    public ArmorItem chestPlateItem = null;
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
    public void renderTransformed(PoseStack poseStack, MultiBufferSource buffer, AbstractClientPlayer player, HumanoidArm side, int combinedLight) {
        try {
            this.tryRender(poseStack, buffer, player, side, combinedLight);
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

        this.initEvent();
    }

    public void tryTick(AbstractClientPlayer player) {
        ItemStack chestPlate = this.itemStackArmor(player);

        if (this.killingArmor.contains(chestPlate.getItem()) || !(chestPlate.getItem() instanceof ArmorItem)) {
            this.chestPlate = ItemStack.EMPTY;
            this.chestPlateItem = null;
            this.render = false;
            return;
        }

        if (this.checkIsNotNew(chestPlate)) {
            return;
        }

        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(chestPlate.getItem());

        if (rl == null || this.renderBlackList.contains(new ShapelessRL(rl))) {
            this.chestPlateItem = (ArmorItem) chestPlate.getItem();
            this.chestPlate = chestPlate;
            this.render = false;
            return;
        }

        this.render = true;
        this.chestPlate = chestPlate;
        this.chestPlateItem = (ArmorItem) chestPlate.getItem();

        this.updateOverriders();

        this.currentArmorModel = this.getArmorModel(player);
        this.currentArmorTexOv = this.getArmorTex(player, IOverriderGetTex.EnumTexType.OVERLAY);
        this.currentArmorTex = this.getArmorTex(player, IOverriderGetTex.EnumTexType.NULL);
    }

    public void tryRender(PoseStack poseStack, MultiBufferSource buffer, AbstractClientPlayer player, HumanoidArm side, int combinedLight) {
        ResourceLocation armorTexOv = this.currentArmorTexOv;
        IModelOnlyArms armorModel = this.currentArmorModel;
        ResourceLocation armorTex = this.currentArmorTex;

        this.render(armorModel, armorTexOv, poseStack, buffer, side, this.chestPlate, this.chestPlateItem, IOverriderRender.EnumRenderType.ARMOR_OVERLAY, combinedLight);
        this.render(armorModel, armorTex, poseStack, buffer, side, this.chestPlate, this.chestPlateItem, IOverriderRender.EnumRenderType.ARMOR, combinedLight);

        this.render(armorModel, null, poseStack, buffer, side, this.chestPlate, this.chestPlateItem, IOverriderRender.EnumRenderType.ARMOR_ENCHANT, combinedLight);
        this.render(armorModel, null, poseStack, buffer, side, this.chestPlate, this.chestPlateItem, IOverriderRender.EnumRenderType.ARMOR_TRIM, combinedLight);
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

        return player.getItemBySlot(EquipmentSlot.CHEST);
    }

    private void initEvent() {
        InitArmorRenderLayerEvent event = new InitArmorRenderLayerEvent();
        MinecraftForge.EVENT_BUS.post(event);

        this.textureOverriders = event.textureOverriders();
        this.renderOverriders = event.renderOverriders();
        this.modelOverriders = event.modelOverriders();

        this.renderBlackList = event.renderBlackList();
    }

    public PlayerRenderer initRenderPlayer(AbstractClientPlayer player) {
        return this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
    }

    public void updateOverriders() {
        ResourceLocation rli = ForgeRegistries.ITEMS.getKey(this.chestPlate.getItem());

        if (rli == null) {
            return;
        }

        ShapelessRL rl = new ShapelessRL(rli);

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

    public void render(IModelOnlyArms arms, ResourceLocation tex, PoseStack pPoseStack, MultiBufferSource pBuffer, HumanoidArm handSide, ItemStack stackArmor, ArmorItem itemArmor, IOverriderRender.EnumRenderType type, int packedLight) {
        this.currentRenderOverrider.render(arms, tex, pPoseStack, pBuffer, handSide, stackArmor, itemArmor, type, packedLight);
    }

    public IModelOnlyArms getArmorModel(AbstractClientPlayer player) {
        return this.currentGetModelOverrider.getModel(player, this.chestPlateItem, this.chestPlate);
    }

    public ResourceLocation getArmorTex(AbstractClientPlayer player, IOverriderGetTex.EnumTexType type) {
        return this.currentGetTexOverrider.getTexture(player, this.chestPlate, this.chestPlateItem, type);
    }

    public static class DefaultRender implements IOverriderRender {
        private final TextureAtlas armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        private final Minecraft mc = Minecraft.getInstance();

        @Override
        public void render(@Nullable IModelOnlyArms arms, @Nullable ResourceLocation tex, PoseStack pPoseStack, MultiBufferSource pBuffer, HumanoidArm handSide, ItemStack stackArmor, ArmorItem itemArmor, EnumRenderType type, int packedLight) {
            if (arms == null || this.mc.player == null) {
                return;
            }
            switch (type) {
                case ARMOR_ENCHANT:
                    this.renderGlint(pPoseStack, pBuffer, this.mc.player, itemArmor, stackArmor, handSide, packedLight, arms);
                    break;
                case ARMOR:
                    this.renderBase(pPoseStack, pBuffer, this.mc.player, itemArmor, stackArmor, handSide, packedLight, arms, tex);
                    break;
                case ARMOR_OVERLAY:
                    this.renderOverlay(pPoseStack, pBuffer, this.mc.player, itemArmor, stackArmor, handSide, packedLight, arms, tex);
                    break;
                case ARMOR_TRIM:
                    this.renderTrim(pPoseStack, pBuffer, this.mc.player, itemArmor, stackArmor, handSide, packedLight, arms);
                    break;
            }
        }

        protected void renderBase(PoseStack pPoseStack, MultiBufferSource pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, IModelOnlyArms pModel, ResourceLocation armorResource) {
            if (armorResource == null) {
                return;
            }
            float r = 1.0F, g = 1.0F, b = 1.0F;
            if (itemArmor instanceof DyeableLeatherItem dye) {
                int i = dye.getColor(stackArmor);
                r = (float)(i >> 16 & 255) / 255.0F;
                g = (float)(i >> 8 & 255) / 255.0F;
                b = (float)(i & 255) / 255.0F;
            }
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
            pModel.renderArm(pPoseStack, vertexconsumer, player, itemArmor, stackArmor, side, pPackedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        }

        protected void renderOverlay(PoseStack pPoseStack, MultiBufferSource pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, IModelOnlyArms pModel, ResourceLocation armorResource) {
            if (armorResource == null || !(itemArmor instanceof DyeableLeatherItem)) {
                return;
            }
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
            pModel.renderArm(pPoseStack, vertexconsumer, player, itemArmor, stackArmor, side, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        protected void renderTrim(PoseStack pPoseStack, MultiBufferSource pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, IModelOnlyArms pModel) {
            ArmorTrim.getTrim(player.level().registryAccess(), stackArmor).ifPresent((pTrim) -> {
                TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(pTrim.outerTexture(itemArmor.getMaterial()));
                VertexConsumer vertexconsumer = textureatlassprite.wrap(pBuffer.getBuffer(Sheets.armorTrimsSheet()));
                pModel.renderArm(pPoseStack, vertexconsumer, player, itemArmor, stackArmor, side, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            });
        }

        protected void renderGlint(PoseStack pPoseStack, MultiBufferSource pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, IModelOnlyArms pModel) {
            if (stackArmor.hasFoil()) {
                pModel.renderArm(pPoseStack, pBuffer.getBuffer(RenderType.armorEntityGlint()), player, itemArmor, stackArmor, side, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public static class DefaultModelGetter implements IOverriderGetModel {

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stack) {
            Model model = ForgeHooksClient.getArmorModel(player, stack, EquipmentSlot.CHEST, ArmoredArms.RENDER_ARM_MANAGER.actualHumanoidModel);
            return this.createModelOnlyArms(model);
        }

        protected IModelOnlyArms createModelOnlyArms(Model model) {
            ModelPart[] arms = this.extractArms(model);
            if (arms == null) return null;
            return new DefaultModelOnlyArms(model, arms);
        }

        protected ModelPart[] extractArms(Model model) {
            System.out.println(model);
            if (model instanceof HumanoidModel<?> hm) {
                return new ModelPart[] {hm.leftArm, hm.rightArm};
            }
            return null;
        }
    }

    public static class DefaultTextureGetter implements IOverriderGetTex {

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, ItemStack chestPlate, ArmorItem itemArmor, EnumTexType type) {
            switch (type) {
                case NULL:
                    return fmlGetArmorResource(player, chestPlate, EquipmentSlot.CHEST, null);
                case OVERLAY:
                    if (itemArmor instanceof DyeableLeatherItem) fmlGetArmorResource(player, chestPlate, EquipmentSlot.CHEST, "overlay");
            }
            return null;
        }
    }

    public static class DefaultModelOnlyArms implements IModelOnlyArms {
        public final ModelPart[] playerArms = MiscUtils.playerArms();
        public final ModelPart[] arms;
        public final Model mb;

        public DefaultModelOnlyArms(HumanoidModel<?> mb) {
            this.arms = new ModelPart[] {mb.leftArm, mb.rightArm};
            this.mb = mb;
        }

        public DefaultModelOnlyArms(Model mb, Function2<Model, HumanoidArm, ModelPart> armsExtractor) {
            this.arms = new ModelPart[] {armsExtractor.apply(mb, HumanoidArm.LEFT), armsExtractor.apply(mb, HumanoidArm.RIGHT)};
            this.mb = mb;
        }

        public DefaultModelOnlyArms(Model mb, ModelPart[] arms) {
            this.arms = arms;
            this.mb = mb;
        }


        public DefaultModelOnlyArms(Model mb, ModelPart right, ModelPart left) {
            this.arms = new ModelPart[] {left, right};
            this.mb = mb;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void renderArm(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            ModelPart arm = this.arms[side.ordinal()];
            arm.copyFrom(this.playerArms[side.ordinal()]);
            if (this.mb instanceof HumanoidModel<?>) {
                HumanoidModel<LivingEntity> model = (HumanoidModel<LivingEntity>) this.mb;
                model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                model.attackTime = 0.0F;
                model.crouching = false;
                model.swimAmount = 0.0F;
            }
            arm.xRot = 0.0F;
            boolean s = arm.skipDraw;
            boolean v = arm.visible;
            arm.skipDraw = false;
            arm.visible = true;
            arm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            arm.skipDraw = s;
            arm.visible = v;
        }

        @Override
        public Model original() {
            return mb;
        }
    }

    public static ResourceLocation fmlGetArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1, type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }
}
