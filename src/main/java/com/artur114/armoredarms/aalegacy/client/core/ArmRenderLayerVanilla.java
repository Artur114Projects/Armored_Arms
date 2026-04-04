package com.artur114.armoredarms.aalegacy.client.core;

import com.artur114.armoredarms.aalegacy.api.IArmRenderLayer;
import com.artur114.armoredarms.aalegacy.api.IVanillaHandRenderer;
import com.artur114.armoredarms.aalegacy.api.events.InitVanillaHandRendererEvent;
import com.artur114.armoredarms.aalegacy.client.util.EnumHandSide;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ArmRenderLayerVanilla implements IArmRenderLayer {
    private IVanillaHandRenderer renderer = null;
    public RenderPlayer renderPlayer = null;

    @Override
    public void update(AbstractClientPlayer player) {
        this.renderer.update(player, this.renderPlayer);
    }

    @Override
    public void renderTransformed(AbstractClientPlayer player, EnumHandSide side) {
        if (player.isInvisible()) {
            return;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
        this.renderArm(player, side);
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return renderManagerState;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
        InitVanillaHandRendererEvent event = new InitVanillaHandRendererEvent();
        MinecraftForge.EVENT_BUS.post(event);
        this.renderer = event.renderer();

        if (this.renderer == null) {
            this.renderer = new DefaultHandRender();
        }
    }

    public void renderArm(AbstractClientPlayer player, EnumHandSide side) {
        this.renderer.renderHand(player, this.renderPlayer, side);
    }

    public static class DefaultHandRender implements IVanillaHandRenderer {
        @Override
        public void update(AbstractClientPlayer player, RenderPlayer renderPlayer) {}

        @Override
        public void renderHand(AbstractClientPlayer player, RenderPlayer renderPlayer, EnumHandSide side) {
            ModelBiped mb = renderPlayer.modelBipedMain;
            float f = 1.0F;
            GL11.glColor3f(f, f, f);
            mb.swingProgress = 0.0F;
            mb.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
            side.handFromModelBiped(mb).render(0.0625F);
        }

        @Override
        public boolean isCombinable() {
            return false;
        }
    }
}