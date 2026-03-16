package com.artur114.armoredarms.client.integration.immersiveintelligence.modelrender;

import com.artur114.armoredarms.aalegacy.api.IOverriderGetTex;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.client.util.TextureEnchant;
import com.artur114.armoredarms.client.util.TextureRL;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pl.pabilo8.immersiveintelligence.client.model.armor.ModelLightEngineerArmor;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.util.IISkinHandler;

import java.util.List;

public class ArmModelContainerII implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {

        if (manager.model instanceof ModelLightEngineerArmor) {
            return new ArmModelRendererTurbo((ModelLightEngineerArmor) manager.model, this.textures(manager, manager.stack));
        }

        return new ArmModelRendererArmor(manager.model, manager.texture);
    }

    public IMultiTexture textures(ArmModelManagerArmor manager, AAItemStack stack) {
        String s = IISkinHandler.getCurrentSkin(stack.stack());
        List<ITexture> textures = manager.newTextureList();
        boolean flag = false;

        if (IISkinHandler.isValidSkin(s)) {
            IISkinHandler.IISpecialSkin skin = IISkinHandler.getSkin(s);
            if (skin.doesApply(IIContent.itemLightEngineerChestplate.getSkinnableName())) {
                textures.add(this.getSkin(s));
                flag = true;
            }
        }

        if (!flag) {
            textures.add(this.getSkin(""));
        }

        return new MultiTexture(textures);
    }

    private ITexture getSkin(String skin) {
        String baseName = skin.isEmpty() ? "immersiveintelligence:textures/armor/engineer_light" : "immersiveintelligence:textures/skins/" + skin + "/engineer_light";
        return new TextureRL(new ResourceLocation(baseName + ".png"));
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
