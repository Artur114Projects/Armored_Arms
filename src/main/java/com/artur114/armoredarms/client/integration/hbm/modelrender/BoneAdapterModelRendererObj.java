package com.artur114.armoredarms.client.integration.hbm.modelrender;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;
import com.hbm.render.loader.ModelRendererObj;

public class BoneAdapterModelRendererObj implements IBoneAdapter<ModelRendererObj> {
    @Override
    public void inject(Bone bone, ObjectBuff data, ModelRendererObj to) {
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
    public void set(Bone bone, ObjectBuff data, ModelRendererObj from) {}

    @Override
    public Class<ModelRendererObj> targetObjectClass() {
        return ModelRendererObj.class;
    }

    @Override
    public boolean canWork(ModelRendererObj obj) {
        return true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
