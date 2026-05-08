package com.artur114.armoredarms.client.integration.punchy.modelrender;

import com.artur114.armoredarms.client.integration.azurelib.modelrender.BoneAdapterAzBone;
import com.artur114.armoredarms.client.integration.azurelib.modelrender.PSAzBone;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.PSGeoBone;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BoneAdapterAzBonePunchy extends BoneAdapterAzBone {
    @Override
    public void inject(Bone bone, ObjectBuff data, PSAzBone to) {
        super.inject(bone, data, to);
        data.jump(3);
        Matrix4f explicit = new Matrix4f(data.readObject(Matrix4f.class));
        Vector3f translation = explicit.getTranslation(new Vector3f());
        translation.div(16.0F);
        explicit.setTranslation(translation);
        Matrix4f vanilla = (new Matrix4f()).translate(to.arm.getPosX() / 16.0F, to.arm.getPosY() / 16.0F, to.arm.getPosZ() / 16.0F).rotateZ(to.arm.getRotZ()).rotateY(to.arm.getRotY()).rotateX(to.arm.getRotX()).scale(to.arm.getScaleX(), to.arm.getScaleY(), to.arm.getScaleZ());
        Matrix4f correction = vanilla.invert(new Matrix4f()).mul(explicit);
        to.stack.mulPoseMatrix(correction);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
