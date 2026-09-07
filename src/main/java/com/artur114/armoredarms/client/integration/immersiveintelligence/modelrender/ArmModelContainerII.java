package com.artur114.armoredarms.client.integration.immersiveintelligence.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.TextureRL;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import com.artur114.armoredarms.core.util.Reflector;
import net.minecraft.util.ResourceLocation;
import pl.pabilo8.immersiveintelligence.client.model.armor.ModelLightEngineerArmor;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.items.armor.ItemIILightEngineerChestplate;
import pl.pabilo8.immersiveintelligence.common.util.IISkinHandler;

import java.util.List;

public class ArmModelContainerII implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {

        if (manager.model instanceof ModelLightEngineerArmor) {
            if (this.isNewMod()) {
                return new ArmModelRendererTurboNew((ModelLightEngineerArmor) manager.model, this.textures(manager, manager.stack));
            } else {
                return new ArmModelRendererTurboOld((ModelLightEngineerArmor) manager.model, this.textures(manager, manager.stack));
            }
        }

        return new ArmModelRendererArmor(manager.model, manager.texture);
    }

    public IMultiTexture textures(ArmModelManagerArmor manager, ItemStackAA stack) {
        List<ITexture> textures = manager.newTextureList();

        if (this.isNewMod()) {
            this.computeTexturesNew(textures, stack);
        } else {
            this.computeTexturesOld(textures, stack);
        }

        return new MultiTexture(textures);
    }

    private void computeTexturesOld(List<ITexture> textures, ItemStackAA stack) {
        textures.add(new TextureRL(new ResourceLocation("immersiveintelligence:textures/armor/engineer_light.png")));
    }

    private void computeTexturesNew(List<ITexture> textures, ItemStackAA stack) {
        boolean flag = false;
        String s = IISkinHandler.getCurrentSkin(stack.stack());
        if (IISkinHandler.isValidSkin(s)) {
            IISkinHandler.IISpecialSkin skin = IISkinHandler.getSkin(s);
            if (skin.doesApply(Reflector.invokeMethod(ItemIILightEngineerChestplate.class, IIContent.itemLightEngineerChestplate, "getSkinnableName", new Class<?>[0], new Object[0]))) {
                textures.add(this.getSkin(s));
                flag = true;
            }
        }

        if (!flag) {
            textures.add(this.getSkin(""));
        }
    }

    private boolean isNewMod() {
        return Reflector.isClassExists("pl.pabilo8.immersiveintelligence.common.util.IISkinHandler");
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
