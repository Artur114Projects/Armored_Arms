package com.artur114.armoredarms.client.integration.azurelib.modelrender;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;

public class BoneAdapterAzBone implements IBoneAdapter<PSAzBone>  {
    @Override
    public void inject(Bone bone, ObjectBuff data, PSAzBone to) {
        float xRot = data.readFloat();
        float yRot = data.readFloat();
        float zRot = data.readFloat();
        float x = data.readFloat();
        float y = data.readFloat();
        float z = data.readFloat();

        to.arm.setRotX(-xRot);
        to.arm.setRotY(-yRot);
        to.arm.setRotZ(zRot);

        float delta = to.side == EnumHandSideAA.RIGHT ? 5.0F : -5.0F;
        to.arm.setPosX(x + delta);
        to.arm.setPosY(2.0F - y);
        to.arm.setPosZ(z);

        to.arm.setScaleX(1.0F);
        to.arm.setScaleY(1.0F);
        to.arm.setScaleZ(1.0F);
    }

    @Override
    public void set(Bone bone, ObjectBuff data, PSAzBone from) {}

    @Override
    public Class<PSAzBone> targetObjectClass() {
        return PSAzBone.class;
    }

    @Override
    public boolean canWork(PSAzBone obj) {
        return true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
