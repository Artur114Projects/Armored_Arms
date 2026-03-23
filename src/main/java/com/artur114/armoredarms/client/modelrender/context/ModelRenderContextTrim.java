package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.List;

public class ModelRenderContextTrim extends AbsModelRenderContext {
    protected final TextureAtlas armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    protected final Minecraft mc = Minecraft.getInstance();
    protected final ItemStackAA armorItem;

    public ModelRenderContextTrim(ItemStackAA armorItem, IPriority priority) {
        super(priority);

        this.armorItem = armorItem;
    }

    public ModelRenderContextTrim(ItemStackAA armorItem) {
        this(armorItem, Priority.NORMAL);
    }

    @Override
    public VertexConsumer vertexConsumer() {
        if (this.mc.player == null) return null;

        ArmorTrim trim = ArmorTrim.getTrim(this.mc.player.level().registryAccess(), this.armorItem.stack()).orElse(null);

        if (trim != null) {
            TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(trim.outerTexture(this.armorItem.item().getMaterial()));
            return textureatlassprite.wrap(this.buffer.getBuffer(Sheets.armorTrimsSheet()));
        }

        return null;
    }

    @Override
    public float alpha() {
        return 1.0F;
    }

    @Override
    public float blue() {
        return 1.0F;
    }

    @Override
    public float green() {
        return 1.0F;
    }

    @Override
    public float red() {
        return 1.0F;
    }
}
