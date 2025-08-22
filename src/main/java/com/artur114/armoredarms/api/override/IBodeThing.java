package com.artur114.armoredarms.api.override;

/**
 * @see com.artur114.armoredarms.client.RenderArmManager.BoneThingModelRender
 */
public interface IBodeThing {
    void setRotation(float x, float y, float z);
    void render(float scale);
}
