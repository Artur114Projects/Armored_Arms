package com.artur114.armoredarms.client.integration.alfheim.modelrender;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.ObjectBuff;
import org.lwjgl.opengl.GL11;

public class BoneAdapterAlfheim implements IBoneAdapter<AlfheimGLCont> {
    
    @Override
    public void inject(Bone bone, ObjectBuff data, AlfheimGLCont to) {
        data.jump(3);
        float rotateAngleX = data.readFloat();
        float rotateAngleY = data.readFloat();
        float rotateAngleZ = data.readFloat();
        float rotationPointX = data.readFloat();
        float rotationPointY = data.readFloat();
        float rotationPointZ = data.readFloat();

        GL11.glTranslatef(rotationPointX * (1.0F / 16.0F), rotationPointY * (1.0F / 16.0F), rotationPointZ * (1.0F / 16.0F));
        GL11.glRotatef((float) (rotateAngleZ * (180.0F / Math.PI)), 0.0F, 0.0F, 1.0F);
        GL11.glRotatef((float) (rotateAngleY * (180.0F / Math.PI)), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float) (rotateAngleX * (180.0F / Math.PI)), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
    }

    @Override
    public void set(Bone bone, ObjectBuff data, AlfheimGLCont from) {}

    @Override
    public Class<AlfheimGLCont> targetObjectClass() {
        return AlfheimGLCont.class;
    }

    @Override
    public boolean canWork(AlfheimGLCont obj) {
        return true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
