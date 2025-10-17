package com.artur114.armoredarms.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.HashSet;
import java.util.Set;

public class AAClientCommandsManager {
    private final Set<IAACommand> commands = new HashSet<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private IAACommand lastCommand = null;

    public AAClientCommandsManager() {
        this.addCommand(new CommandCopyItemName());
    }

    public void clientChatEvent(ClientChatEvent e) {
        if (e.getMessage().equals("&aahelp")) {
            e.setCanceled(true);
            this.mc.player.sendMessage(new TextComponentString("&aac - Executes the last executed command."));
            this.mc.player.sendMessage(new TextComponentString("&aahelp - Displays a list of all commands and their descriptions."));
            for (IAACommand command : this.commands) {
                this.mc.player.sendMessage(new TextComponentString(command.name() + " - " + command.description()));
            }
        }
        if (e.getMessage().equals("&aac")) {
            e.setCanceled(true);
            if (this.lastCommand != null) {
                this.lastCommand.execute(this.mc.player);
            }
            return;
        }
        for (IAACommand command : this.commands) {
            if (command.name().equals(e.getMessage())) {
                command.execute(this.mc.player);
                this.lastCommand = command;
                e.setCanceled(true);
                break;
            }
        }
    }

    public void addCommand(IAACommand command) {
        this.commands.add(command);
    }

    public interface IAACommand {
        String name();
        String description();
        void execute(EntityPlayerSP player);
    }
    
    public static class CommandCopyItemName implements IAACommand {

        @Override
        public String name() {
            return "&aacopyitemname";
        }

        @Override
        public String description() {
            return "Outputs to the console and copies the indicator of the item in the main hand.";
        }

        @Override
        public void execute(EntityPlayerSP player) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (stack.isEmpty()) {
                return;
            }
            ResourceLocation registryName = stack.getItem().getRegistryName();
            if (registryName == null) {
                return;
            }
            String itemName = registryName.toString();
            player.sendMessage(new TextComponentString("Id: " + itemName));
            this.copyToClipboard(itemName);
        }

        private void copyToClipboard(String text) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(text);
            cb.setContents(data, null);
        }
    }
}
