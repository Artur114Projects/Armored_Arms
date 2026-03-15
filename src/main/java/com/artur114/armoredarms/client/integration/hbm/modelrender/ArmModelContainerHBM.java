package com.artur114.armoredarms.client.integration.hbm.modelrender;

import com.artur114.armoredarms.aalegacy.util.Reflector;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.client.util.TextureEnchant;
import com.artur114.armoredarms.client.util.TextureRL;
import com.artur114.armoredarms.client.util.TextureRLRGB;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.render.model.ModelT45Chest;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ArmModelContainerHBM implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    private final String rightArm;
    private final String leftArm;
    private final String texture;

    public ArmModelContainerHBM(String rightArm, String leftArm, String texture) {
        this.rightArm = rightArm;
        this.leftArm = leftArm;
        this.texture = texture;
    }

    public ArmModelContainerHBM(String texture) {
        this.rightArm = "rightArm";
        this.leftArm = "leftArm";
        this.texture = texture;
    }

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        ModelBiped mb = manager.model;
        Object right = Reflector.getPrivateField(mb, this.rightArm);
        Object left = Reflector.getPrivateField(mb, this.leftArm);
        IMultiTexture texture = this.textures(manager.player, manager.armorLayer, manager.stack);

        if (Reflector.isClassExists("com.hbm.render.model.ModelT45Chest") && mb instanceof ModelT45Chest) {
            return new ArmModelRendererT45(mb, texture, (ModelRenderer) right, (ModelRenderer) left);
        } else if (right instanceof ModelRenderer) {
            return new ArmModelRendererArmor(mb, texture, (ModelRenderer) right, (ModelRenderer) left);
        } else if (right instanceof ModelRendererObj) {
            return new ArmModelRendererHBM(mb, texture, (ModelRendererObj) right, (ModelRendererObj) left);
        }

        return null;
    }

    public IMultiTexture textures(AbstractClientPlayer player, LayerBipedArmor armorLayer, AAItemStack stack) {
        ResourceLocation overlay = null;
        ResourceLocation armor;
        if (this.texture.equals("item")) {
            if (stack.item().hasOverlay(stack.stack())) overlay = armorLayer.getArmorResource(player, stack.stack(), EntityEquipmentSlot.CHEST, "overlay");
            armor = armorLayer.getArmorResource(player, stack.stack(), EntityEquipmentSlot.CHEST, null);
        } else {
            armor = Reflector.getPrivateField(ResourceManager.class, null, this.texture);
        }

        List<ITexture> textures = new ArrayList<>(2);
        if (overlay != null) {
            textures.add(new TextureRL(overlay, Priority.HIGH));
            textures.add(new TextureRLRGB(armor, stack.item().getColor(stack.stack())));
        } else {
            textures.add(new TextureRL(armor));
        }

        if (stack.item().hasEffect(stack.stack())) {
            textures.add(TextureEnchant.FIRST);
            textures.add(TextureEnchant.SECOND);
        }

        return new MultiTexture(textures);
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return true;
    }

    @Override
    public Class<ArmModelManagerArmor> targetManager() {
        return ArmModelManagerArmor.class;
    }

    @Override
    public Class<ArmRenderLayerArmor> targetLayer() {
        return ArmRenderLayerArmor.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
