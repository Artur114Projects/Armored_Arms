package com.artur114.armoredarms.client.integration.curios.layers;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.PSGeoBone;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class ArmRenderLayerCurio implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    public final Minecraft mc = Minecraft.getInstance();
    public AbstractRenderEngineForge<?, ?> engine;
    private boolean isDeactivated = false;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {}

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        if (this.mc.player == null || !EnumMods.GECKO_LIB.isLoaded()) return;

        ArmRenderContext renderContext = engine.renderContext;
        PoseStack poseStack = renderContext.poseStack;
        poseStack.pushPose();

        CuriosApi.getCuriosInventory(this.mc.player).ifPresent((handler) -> handler.getCurios().forEach((id, stacksHandler) -> {
            if (!stacksHandler.isVisible()) return;

            IDynamicStackHandler stackHandler = stacksHandler.getStacks();
            IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();
            NonNullList<Boolean> renderStates = stacksHandler.getRenders();

            for (int i = 0; i < stackHandler.getSlots(); ++i) {
                if (renderStates.size() > i && !renderStates.get(i)) continue;

                ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);

                if (stack.isEmpty()) {
                    stack = stackHandler.getStackInSlot(i);
                }

                if (stack.isEmpty()) continue;

                Model model = ClientHooks.getArmorModel(this.mc.player, stack, EquipmentSlot.CHEST, engine.actualHumanoidModel());

                if (model instanceof GeoArmorRenderer<?> geoModel) {
                    this.renderGeo(geoModel, renderContext, handSide, stack);
                }
            }
        }));

        poseStack.popPose();
    }

    @SuppressWarnings("unchecked")
    private <T extends Item & GeoItem> void renderGeo(GeoArmorRenderer<?> rawModel, ArmRenderContext renderContext, EnumHandSideAA side, ItemStack stack) {
        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) rawModel;
        T item = (T) stack.getItem();
        BakedGeoModel baked = model.getGeoModel().getBakedModel(model.getGeoModel().getModelResource(item, model));
        GeoBone arm = baked.getBone(side.sided("armorRightArm", "armorLeftArm")).orElse(null);

        if (arm == null) return;

        MultiBufferSource bufferSource = renderContext.multiBufferSource;
        PoseStack poseStack = renderContext.poseStack;
        float partialTick = this.mc.getTimer().getGameTimeDeltaTicks();
        RenderType renderType = model.getRenderType(item, model.getTextureLocation(item), bufferSource, partialTick);
        VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, stack.hasFoil());

        PSGeoBone bone = new PSGeoBone();
        bone.stack = poseStack;
        bone.side = side;
        bone.arm = arm;

        boolean hidden = arm.isHidden();
        arm.setHidden(false);

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.engine.mainBones().bySide(side).injectTo(bone);
        model.renderRecursively(poseStack, item, arm, renderType, bufferSource, buffer, false, partialTick, renderContext.packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        poseStack.popPose();

        arm.setHidden(hidden);
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.engine = engine;
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<?, ?> engine, boolean renderEngineState) {
        if (this.mc.player == null || !EnumMods.GECKO_LIB.isLoaded()) return false;

        return CuriosApi.getCuriosInventory(this.mc.player)
                .map((handler) -> handler.getCurios().values().stream().anyMatch(this::hasArmor))
                .orElse(false);
    }

    private boolean hasArmor(ICurioStacksHandler stacksHandler) {
        if (!stacksHandler.isVisible()) return false;

        IDynamicStackHandler stackHandler = stacksHandler.getStacks();
        IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();
        NonNullList<Boolean> renderStates = stacksHandler.getRenders();

        for (int i = 0; i < stackHandler.getSlots(); ++i) {
            if (renderStates.size() > i && !renderStates.get(i)) continue;

            ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);

            if (stack.isEmpty()) {
                stack = stackHandler.getStackInSlot(i);
            }

            if (stack.getItem() instanceof ArmorItem) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return AbstractRenderEngineForge.clazz();
    }

    @Override
    public AbstractRenderEngineForge<?, ?> engine() {
        return this.engine;
    }

    @Override
    public boolean isDeactivated() {
        return this.isDeactivated;
    }

    @Override
    public void deactivate() {
        this.isDeactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.LOW;
    }
}
