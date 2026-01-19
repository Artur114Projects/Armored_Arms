package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.client.util.MappingsProcessor;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AAClientCommandsManager {
    private final Set<IAACommand> commands = new HashSet<>();
    private final Minecraft mc = Minecraft.getInstance();
    private String[] lastCommandArgs = null;
    private IAACommand lastCommand = null;

    public AAClientCommandsManager() {
        this.addCommand(new CommandPrintModelBipedState());
        this.addCommand(new CommandCopyItemName());
    }

    public void clientChatEvent(ClientChatEvent e) {
        String[] split = e.getMessage().split(" ");
        if (split.length == 0 || this.mc.player == null) {
            return;
        }

        String name = split[0];
        String[] args = new String[0];
        if (split.length > 1) args = Arrays.copyOfRange(split, 1, split.length);

        if (name.equals("&aahelp")) {
            e.setCanceled(true);
            this.mc.player.displayClientMessage(Component.literal("You can write (any command) usage to see usage"), false);
            this.mc.player.displayClientMessage(Component.literal("&aac - Executes the last executed command."), false);
            this.mc.player.displayClientMessage(Component.literal("&aahelp - Displays a list of all commands and their descriptions."), false);
            for (IAACommand command : this.commands) {
                this.mc.player.displayClientMessage(Component.literal(command.name() + " - " + command.description()), false);
            }
        }
        if (name.equals("&aac")) {
            e.setCanceled(true);
            if (this.lastCommand != null && this.lastCommandArgs != null) {
                this.lastCommand.execute(this.mc.player, this.lastCommandArgs);
            }
            return;
        }
        for (IAACommand command : this.commands) {
            if (command.name().equals(name)) {
                if (args.length > 0 && args[0].equals("usage")) {
                    for (String m : command.usage()) {
                        this.mc.player.displayClientMessage(Component.literal(m), false);
                    }
                    break;
                }
                try {
                    command.execute(this.mc.player, args);
                    this.lastCommandArgs = args;
                    this.lastCommand = command;
                } catch (Exception exp) {
                    this.mc.player.displayClientMessage(Component.literal("ERROR: " + exp.getClass() + ": " + exp.getMessage()).withStyle(ChatFormatting.RED), false);
                    exp.printStackTrace(System.err);
                }
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
        String[] usage();
        String description();
        void execute(LocalPlayer player, String[] args);
    }

    public static class CommandCopyItemName implements IAACommand {

        @Override
        public String name() {
            return "&aacopyitemname";
        }

        @Override
        public String[] usage() {
            return new String[] {"Enter the command while holding the item in main hand."};
        }

        @Override
        public String description() {
            return "Outputs to the chat and copies the indicator of the item in the main hand.";
        }

        @Override
        public void execute(LocalPlayer player, String[] args) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
            if (stack.isEmpty()) {
                return;
            }
            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (registryName == null) {
                return;
            }
            String itemName = registryName.toString();
            player.displayClientMessage(Component.literal("Id: " + itemName + " is copied to clipboard"), false);
            this.copyToClipboard(itemName);
        }

        private void copyToClipboard(String text) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(text);
            cb.setContents(data, null);
        }
    }

    public static class CommandPrintModelBipedState implements IAACommand {

        @Override
        public String name() {
            return "&aaresearchmodel";
        }

        @Override
        public String[] usage() {
            return new String[] {
                    "&aaresearchmodel target(might be (player/armor)) args",
                    "required arguments:[-bipall/-fields#/-refall] (at least one of them is required)",
                    "arg -bipall it designates the scope of information collection: all fields of the ModelRender type of the ModelBiped class.",
                    "arg -fields# it designates the scope of information collection: field names entered after the separator (!) -fields# example -fields#bipedLeftArm!bipedRightArm",
                    "arg -refall it designates the scope of information collection: all fields in the model class, incompatible with the preceding two arguments",
                    "optional arguments[-copy/-chat/-console/-mr/-deep]",
                    "arg -copy copies the summary data to the clipboard",
                    "arg -chat displays summary data in chat",
                    "arg -copy outputs summary data to the console",
                    "arg -mr for fields of type ModelRender more detailed information is collected",
                    "arg -deep the model class is checked recursively including parents",
                    "example usage: &aaresearchmodel armor -refall -copy -console -mr -deep"
            };
        }

        @Override
        public String description() {
            return "Outputs to the chat or copies model data";
        }

        @Override
        public void execute(LocalPlayer player, String[] arg) {
            ArmRenderLayerVanilla layerVanilla = ArmoredArms.RENDER_ARM_MANAGER.getLayer(ArmRenderLayerVanilla.class);
            ArmRenderLayerArmor layerArmor = ArmoredArms.RENDER_ARM_MANAGER.getLayer(ArmRenderLayerArmor.class);
            Model mb = null;

            if (arg.length == 0) {
                throw new RuntimeException("Illegal args");
            }

            if (arg[0].equals("player")) {
                mb = layerVanilla.renderPlayer.getModel();
            } else if (arg[0].equals("armor")) {
                mb = layerArmor.currentArmorModel.original();
            }

            if (mb == null) {
                throw new RuntimeException("Illegal arg 0");
            }

            if (arg.length < 2) {
                throw new RuntimeException("Illegal args");
            }

            Set<String> args = new HashSet<>(Arrays.asList(Arrays.copyOfRange(arg, 1, arg.length)));

            Set<String> fields = null;

            if (args.contains("-bipall")) {
                fields = new HashSet<>(Arrays.asList("bipedHead", "bipedHeadwear", "bipedBody", "bipedRightArm", "bipedLeftArm", "bipedRightLeg", "bipedLeftArmwear", "bipedRightArmwear", "bipedLeftLegwear", "bipedRightLegwear", "bipedBodyWear", "bipedLeftLeg"));
            }
            if (args.stream().anyMatch(s -> s.startsWith("-fields#"))) {
                if (fields == null) fields = new HashSet<>();
                fields.addAll(Arrays.asList(args.stream().filter(s -> s.startsWith("-fields#")).collect(Collectors.joining()).replaceAll("-fields#", "!").split("!")));
            }
            if (args.contains("-refall")) {
                if (fields != null) throw new RuntimeException("Illegal search mode");
                fields = new HashSet<>();
            }

            if (fields == null) {
                throw new RuntimeException("Illegal args non method(bipall|refall|-fields#field!field!...)");
            }

            StringBuilder res = new StringBuilder();
            this.loadFields(mb, mb.getClass(), res, fields, new HashSet<>(), args.contains("-deep"), args.contains("-mr"));

            if (args.contains("-copy")) {
                this.copyToClipboard(res.toString());
                player.displayClientMessage(Component.literal("Data is successfully copped to clipboard").withStyle(ChatFormatting.GREEN), false);
            }
            if (args.contains("-chat")) {
                for (String string : res.toString().split("\n")) {
                    player.displayClientMessage(Component.literal(string), false);
                }
            }
            if (args.contains("-console")) {
                for (String string : res.toString().split("\n")) {
                    System.out.println(string);
                }
            }
        }

        private void loadFields(Object mb, Class<?> mbc, StringBuilder res, Set<String> fields, Set<String> loaded, boolean deep, boolean mr) {
            res.append("#-------------------[Class - ").append(mbc.getName()).append("]-------------------#").append('\n');
            Field[] fields1 = mbc.getFields();
            for (Field field : fields1) {
                if (!loaded.contains(field.getName()) && (fields.isEmpty() ||  fields.contains(field.getName()))) {
                    try {
                        boolean isAcc = field.canAccess(Modifier.isStatic(field.getModifiers()) ? null : mb);
                        field.setAccessible(true);
                        Object obj = field.get(Modifier.isStatic(field.getModifiers()) ? null : mb);
                        field.setAccessible(isAcc);

                        String fieldName = MappingsProcessor.getDeObfuscatedFieldName(field.getName());

                        if (mr && obj instanceof ModelPart mp) {
                            res.append(fieldName).append(" - ").append("xRot:").append(mp.xRot).append("\n");
                            res.append(fieldName).append(" - ").append("yRot:").append(mp.yRot).append("\n");
                            res.append(fieldName).append(" - ").append("zRot:").append(mp.zRot).append("\n");

                            res.append(fieldName).append(" - ").append("x:").append(mp.x).append("\n");
                            res.append(fieldName).append(" - ").append("y:").append(mp.y).append("\n");
                            res.append(fieldName).append(" - ").append("z:").append(mp.z).append("\n");

                            res.append(fieldName).append(" - ").append("children:").append(mp.getAllParts().collect(Collectors.toList())).append("\n");
                            res.append(fieldName).append(" - ").append("skipDraw:").append(mp.skipDraw).append("\n");
                            res.append(fieldName).append(" - ").append("visible:").append(mp.visible).append("\n");
                        } else {
                            res.append(fieldName).append(" - [").append(obj.getClass()).append(", obj:").append(obj).append(']').append("\n");
                        }

                        loaded.add(field.getName());
                    } catch (IllegalAccessException ignored) {}
                }
            }

            if (!deep) {
                return;
            }

            Class<?> superC = mbc.getSuperclass();
            if (superC != Object.class && superC != null) {
                this.loadFields(mb, superC, res, fields, loaded, true, mr);
            }
        }

        private void copyToClipboard(String text) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(text);
            cb.setContents(data, null);
        }
    }
}

/*



 */
