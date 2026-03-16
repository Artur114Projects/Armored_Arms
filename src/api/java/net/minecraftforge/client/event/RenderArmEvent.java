package net.minecraftforge.client.event;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Event;

// Dark magic
public class RenderArmEvent extends Event {
    public EnumHandSide getArm() {return null;}

    public RenderPlayer getRenderer() {return null;}

    public AbstractClientPlayer getPlayer() {return null;}
}
