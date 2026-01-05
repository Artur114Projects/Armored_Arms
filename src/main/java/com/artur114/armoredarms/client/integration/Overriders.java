package com.artur114.armoredarms.client.integration;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import c4.conarm.client.models.ModelConstructsArmor;
import com.artur114.armoredarms.api.*;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import com.gildedgames.the_aether.api.AetherAPI;
import com.gildedgames.the_aether.api.player.IPlayerAether;
import com.gildedgames.the_aether.api.player.util.IAccessoryInventory;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.accessories.ItemAccessory;
import com.gildedgames.the_aether.items.accessories.ItemAccessoryDyable;
import com.gildedgames.the_aether.player.PlayerAether;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.render.model.ModelT45Chest;
import epicsquid.mysticallib.client.model.ModelArmorBase;
import galaxyspace.core.GSItems;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;
import micdoodle8.mods.galacticraft.api.item.IItemThermal;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC;
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerThermalPadding;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.venus.VenusItems;
import net.machinemuse.powersuits.client.model.item.armor.IArmorModel;
import net.machinemuse.powersuits.common.utils.nbt.MPSNBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.pabilo8.immersiveintelligence.client.model.armor.ModelLightEngineerArmor;
import pl.pabilo8.immersiveintelligence.client.util.tmt.ModelRendererTurbo;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.util.IIColor;
import pl.pabilo8.immersiveintelligence.common.util.IISkinHandler;

import java.util.List;

/**
 * Here i ultrashitcoding
 */
@Mod.EventBusSubscriber
public class Overriders { }
