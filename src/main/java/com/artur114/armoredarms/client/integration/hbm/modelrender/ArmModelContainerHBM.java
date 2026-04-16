package com.artur114.armoredarms.client.integration.hbm.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.TextureRL;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import com.artur114.armoredarms.core.util.Reflector;
import com.artur114.armoredarms.main.ArmoredArms;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.render.model.ModelT45Chest;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.client.ForgeHooksClient;

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

    public ArmModelContainerHBM(String rightArm, String leftArm) {
        this.rightArm = rightArm;
        this.leftArm = leftArm;
        this.texture = "item";
    }

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        ModelBiped mb = manager.model;
        Object r = Reflector.getPrivateField(mb, this.rightArm);
        Object l = Reflector.getPrivateField(mb, this.leftArm);

        if (Reflector.isClassExists("com.hbm.render.model.ModelT45Chest") && mb instanceof ModelT45Chest) {
            return new ArmModelRendererT45(mb, this.getTexture(manager), (ModelRenderer) r, (ModelRenderer) l);
        } else if (r instanceof ModelRenderer) {
            return new ArmModelRendererArmor(mb, this.getTexture(manager), (ModelRenderer) r, (ModelRenderer) l);
        } else if (r instanceof ModelRendererObj) {
            return new ArmModelRendererHBM(this.getTexture(manager), (ModelRendererObj) r, (ModelRendererObj) l);
        }

        return null;
    }

    private IMultiTexture getTexture(ArmModelManagerArmor manager) {
        if (this.texture.equals("item")) {
            return manager.texture;
        } else {
            return this.createTexture(manager);
        }
    }

    private IMultiTexture createTexture(ArmModelManagerArmor manager) {
        List<ITexture> texture = manager.newTextureList();
        texture.add(new TextureRL(Reflector.getPrivateField(ResourceManager.class, null, this.texture)));
        return new MultiTexture(texture);
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return false;
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
