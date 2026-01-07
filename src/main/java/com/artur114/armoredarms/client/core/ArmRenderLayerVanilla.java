package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ArmRenderLayerVanilla implements IArmRenderLayer {
    public RenderPlayer renderPlayer = null;

    @Override
    public void update(AbstractClientPlayer player) {}

    @Override
    public void renderTransformed(AbstractClientPlayer player) {
        if (player.isInvisible()) {
            return;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
        this.renderArm(player);
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return renderManagerState;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
    }

    public void renderArm(AbstractClientPlayer player) {
        ModelBiped mb = this.renderPlayer.modelBipedMain;
        float f = 1.0F;
        GL11.glColor3f(f, f, f);
        mb.swingProgress = 0.0F;
        mb.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
        mb.bipedRightArm.render(0.0625F);
    }
}