package com.artur114.armoredarms.client.integration.punchy.modelrender;

import com.artur114.armoredarms.client.integration.geckolib.modelrender.BoneAdapterGeoBone;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.PSGeoBone;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BoneAdapterGeoBonePunchy extends BoneAdapterGeoBone {
    @Override
    public void inject(Bone bone, ObjectBuff data, PSGeoBone to) {
        float rotX = data.readFloat();
        float rotY = data.readFloat();
        float rotZ = data.readFloat();

        float posX = data.readFloat();
        float posY = data.readFloat();
        float posZ = data.readFloat();

        to.arm.setRotX(rotX);
        to.arm.setRotY(rotY);
        int delta = rotZ < 0.0F ? -1 : 1;
        to.arm.setRotZ((float) (Math.PI * delta) + rotZ);

        float xd = Math.abs(to.arm.getPivotX()) < 5 ? (to.side == EnumHandSideAA.LEFT ? 1 : -1) : 0;
        to.arm.setPosX(to.arm.getPivotX() + xd);
        to.arm.setPosY(-to.arm.getPivotY());
        to.arm.setPosZ(to.arm.getPivotZ());

        to.arm.setScaleX(1.0F);
        to.arm.setScaleY(1.0F);
        to.arm.setScaleZ(1.0F);

        Matrix4f explicit = new Matrix4f(data.readObject(Matrix4f.class));
        Vector3f translation = explicit.getTranslation(new Vector3f());
        translation.div(16.0F);
        explicit.setTranslation(translation);
        Matrix4f vanilla = (new Matrix4f());//.translate(posX / 16.0F, posY / 16.0F, posZ / 16.0F).rotateZ(rotZ).rotateY(rotY).rotateX(rotX).scale(1.0F, 1.0F, 1.0F);
        Matrix4f correction = vanilla.invert(new Matrix4f()).mul(explicit);
        to.stack.mulPoseMatrix(correction);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
