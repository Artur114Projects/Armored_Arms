package com.artur114.armoredarms.client.integration.geckolib.modelrender;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;

public class BoneAdapterGeoBone implements IBoneAdapter<PSGeoBone>  {
    @Override
    public void inject(Bone bone, ObjectBuff data, PSGeoBone to) {
        to.arm.setRotX(data.readFloat());
        to.arm.setRotY(data.readFloat());

        float rotZ = data.readFloat();
        int delta = rotZ < 0.0F ? -1 : 1;
        to.arm.setRotZ((float) (Math.PI * delta) + rotZ);

        to.arm.setPosX(to.arm.getPivotX() * 2);
        to.arm.setPosY(2.0F * -1 * 10);
        to.arm.setPosZ(0.0F);

        to.arm.setScaleX(1.0F);
        to.arm.setScaleY(1.0F);
        to.arm.setScaleZ(1.0F);
    }

    @Override
    public void set(Bone bone, ObjectBuff data, PSGeoBone from) {}

    @Override
    public Class<PSGeoBone> targetObjectClass() {
        return PSGeoBone.class;
    }

    @Override
    public boolean canWork(PSGeoBone obj) {
        return true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
