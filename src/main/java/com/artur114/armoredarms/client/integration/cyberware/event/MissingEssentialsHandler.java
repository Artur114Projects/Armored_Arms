package com.artur114.armoredarms.client.integration.cyberware.event;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.client.render.ModelClaws;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberlimb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class MissingEssentialsHandler {
    public final ResourceLocation robo = new ResourceLocation("cyberware", "textures/models/player_robot.png");
    public final ModelClaws claws = new ModelClaws(0.0F);
    public final Minecraft mc = Minecraft.getMinecraft();
    public boolean missingArm = false;
    public boolean missingSecondArm = false;
    public boolean hasRoboLeft = false;
    public boolean hasRoboRight = false;
    public EnumHandSide oldHand;
    public boolean died = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleMissingEssentials(LivingEvent.LivingUpdateEvent event) {
        if (this.died) {
            return;
        }

        try {
            EntityLivingBase entityLivingBase = event.getEntityLiving();
            if (entityLivingBase == Minecraft.getMinecraft().player) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(entityLivingBase);
                if (cyberwareUserData != null) {
                    boolean stillMissingArm = false;
                    boolean stillMissingSecondArm = false;
                    boolean leftUnpowered = false;
                    ItemStack armLeft = cyberwareUserData.getCyberware(CyberwareContent.cyberlimbs.getCachedStack(0));
                    if (!armLeft.isEmpty() && !ItemCyberlimb.isPowered(armLeft)) {
                        leftUnpowered = true;
                    }

                    boolean rightUnpowered = false;
                    ItemStack armRight = cyberwareUserData.getCyberware(CyberwareContent.cyberlimbs.getCachedStack(1));
                    if (!armRight.isEmpty() && !ItemCyberlimb.isPowered(armRight)) {
                        rightUnpowered = true;
                    }

                    boolean hasSkin = cyberwareUserData.isCyberwareInstalled(CyberwareContent.skinUpgrades.getCachedStack(2));
                    hasRoboLeft = !armLeft.isEmpty() && !hasSkin;
                    hasRoboRight = !armRight.isEmpty() && !hasSkin;
                    boolean hasRightArm = cyberwareUserData.hasEssential(ICyberware.EnumSlot.ARM, ICyberware.ISidedLimb.EnumSide.RIGHT) && !rightUnpowered;
                    boolean hasLeftArm = cyberwareUserData.hasEssential(ICyberware.EnumSlot.ARM, ICyberware.ISidedLimb.EnumSide.LEFT) && !leftUnpowered;
                    if (!hasRightArm) {

                        missingArm = true;
                        stillMissingArm = true;
                        if (!hasLeftArm) {
                            missingSecondArm = true;
                            stillMissingSecondArm = true;
                        }
                    } else if (!hasLeftArm) {

                        missingArm = true;
                        stillMissingArm = true;
                    }

                    if (!stillMissingArm) {
                        missingArm = false;
                    }

                    if (!stillMissingSecondArm) {
                        missingSecondArm = false;
                    }
                }

            }
        } catch (Exception exp) {
            this.died = true;
            exp.printStackTrace(System.err);
        }
    }
}
