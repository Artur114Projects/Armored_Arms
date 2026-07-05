package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;
import net.minecraft.client.model.ModelRenderer;

public class BoneAdapterModelRenderer implements IBoneAdapter<ModelRenderer> {
    @Override
    public void inject(Bone bone, ObjectBuff data, ModelRenderer to) {
        to.offsetX = data.readFloat();
        to.offsetY = data.readFloat();
        to.offsetZ = data.readFloat();
        to.rotateAngleX = data.readFloat();
        to.rotateAngleY = data.readFloat();
        to.rotateAngleZ = data.readFloat();
        to.rotationPointX = data.readFloat();
        to.rotationPointY = data.readFloat();
        to.rotationPointZ = data.readFloat();
    }

    @Override
    public void set(Bone bone, ObjectBuff data, ModelRenderer from) {
        data.writeFloat("offsetX", from.offsetX);
        data.writeFloat("offsetY", from.offsetY);
        data.writeFloat("offsetZ", from.offsetZ);
        data.writeFloat("rotateAngleX", from.rotateAngleX);
        data.writeFloat("rotateAngleY", from.rotateAngleY);
        data.writeFloat("rotateAngleZ", from.rotateAngleZ);
        data.writeFloat("rotationPointX", from.rotationPointX);
        data.writeFloat("rotationPointY", from.rotationPointY);
        data.writeFloat("rotationPointZ", from.rotationPointZ);
    }

    @Override
    public Class<ModelRenderer> targetObjectClass() {
        return ModelRenderer.class;
    }

    @Override
    public boolean canWork(ModelRenderer obj) {
        return true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
