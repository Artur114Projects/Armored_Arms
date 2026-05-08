package com.artur114.armoredarms.client.integration.punchy.modelrender;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import punchy.client.access.TransformablePart;

public class BoneAdapterModelPartPunchy implements IBoneAdapter<ModelPart> {

    @Override
    public void inject(Bone bone, ObjectBuff data, ModelPart to) {
        to.xRot = data.readFloat();
        to.yRot = data.readFloat();
        to.zRot = data.readFloat();
        to.x = data.readFloat();
        to.y = data.readFloat();
        to.z = data.readFloat();
        TransformablePart.class.cast(to).punchy$setExplicitTransform(data.readObject(Matrix4f.class));
    }

    @Override
    public void set(Bone bone, ObjectBuff data, ModelPart from) {
        data.writeFloat("xRot", from.xRot);
        data.writeFloat("yRot", from.yRot);
        data.writeFloat("zRot", from.zRot);
        data.writeFloat("x", from.x);
        data.writeFloat("y", from.y);
        data.writeFloat("z", from.z);
        data.writeObject("nlc|punchy$transform-matrix", TransformablePart.class.cast(from).punchy$getExplicitTransform());
    }

    @Override
    public Class<ModelPart> targetObjectClass() {
        return ModelPart.class;
    }

    @Override
    public boolean canWork(ModelPart obj) {
        return true;
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
