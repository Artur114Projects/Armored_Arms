package com.artur114.armoredarms.client.util.event;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class AAClientCommandsManager {
    public static final AAClientCommandsManager INSTANCE = new AAClientCommandsManager();
    private final Set<IAACommand> commands = new HashSet<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private String[] lastCommandArgs = null;
    private IAACommand lastCommand = null;

    public AAClientCommandsManager() {
        this.addCommand(new CommandPrintModelBipedState());
        this.addCommand(new CommandCopyItemName());
    }

    @SubscribeEvent
    public static void chatEvent(ClientChatEvent e) {
        INSTANCE.clientChatEvent(e);
    }

    public void clientChatEvent(ClientChatEvent e) {
        String[] split = e.getMessage().split(" ");
        if (split.length == 0) {
            return;
        }

        String name = split[0];
        String[] args = new String[0];
        if (split.length > 1) args = Arrays.copyOfRange(split, 1, split.length);

        if (name.equals("&aahelp")) {
            e.setCanceled(true);
            this.mc.player.sendMessage(new TextComponentString("You can write (any command) usage to see usage"));
            this.mc.player.sendMessage(new TextComponentString("&aac - Executes the last executed command."));
            this.mc.player.sendMessage(new TextComponentString("&aahelp - Displays a list of all commands and their descriptions."));
            for (IAACommand command : this.commands) {
                this.mc.player.sendMessage(new TextComponentString(command.name() + " - " + command.description()));
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
                        this.mc.player.sendMessage(new TextComponentString(m));
                    }
                    break;
                }
                try {
                    command.execute(this.mc.player, args);
                    this.lastCommandArgs = args;
                    this.lastCommand = command;
                } catch (Exception exp) {
                    this.mc.player.sendMessage(new TextComponentString("ERROR: " + exp.getClass() + ": " + exp.getMessage()).setStyle(new Style().setColor(TextFormatting.RED)));
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
        void execute(EntityPlayerSP player, String[] args);
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
        public void execute(EntityPlayerSP player, String[] args) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (stack.isEmpty()) {
                return;
            }
            ResourceLocation registryName = stack.getItem().getRegistryName();
            if (registryName == null) {
                return;
            }
            String itemName = registryName.toString();
            player.sendMessage(new TextComponentString("Id: " + itemName + " is copied to clipboard"));
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
        public void execute(EntityPlayerSP player, String[] arg) {
            ArmRenderLayerHand layerVanilla = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerHand.class);
            ArmRenderLayerArmor layerArmor = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerArmor.class);
            ModelBiped mb = null;

            if (arg.length == 0) {
                throw new RuntimeException("Illegal args");
            }

            if (arg[0].equals("player")) {
                mb = layerVanilla.renderPlayer.getMainModel();
            } else if (arg[0].equals("armor")) {
                IArmModelRenderer<?> model = layerArmor.model;
                if (model instanceof ArmModelRendererArmor) {
                    mb = ((ArmModelRendererArmor) model).mb;
                }
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
                player.sendMessage(new TextComponentString("Data is successfully copped to clipboard").setStyle(new Style().setColor(TextFormatting.GREEN)));
            }
            if (args.contains("-chat")) {
                player.sendMessage(new TextComponentString(res.toString()));
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
                if (!loaded.contains(field.getName()) && (fields.isEmpty() ||  fields.contains(Mappings.getDeObfuscatedFieldName(field.getName())))) {
                    try {
                        boolean isAcc = field.isAccessible();
                        field.setAccessible(true);
                        Object obj = field.get(mb);
                        field.setAccessible(isAcc);

                        String fieldName = Mappings.getDeObfuscatedFieldName(field.getName());

                        if (obj instanceof ModelRenderer && mr) {
                            res.append(fieldName).append(" - ").append("rotateAngleX:").append(((ModelRenderer) obj).rotateAngleX).append("\n");
                            res.append(fieldName).append(" - ").append("rotateAngleY:").append(((ModelRenderer) obj).rotateAngleY).append("\n");
                            res.append(fieldName).append(" - ").append("rotateAngleZ:").append(((ModelRenderer) obj).rotateAngleZ).append("\n");

                            res.append(fieldName).append(" - ").append("rotationPointX:").append(((ModelRenderer) obj).rotationPointX).append("\n");
                            res.append(fieldName).append(" - ").append("rotationPointY:").append(((ModelRenderer) obj).rotationPointY).append("\n");
                            res.append(fieldName).append(" - ").append("rotationPointZ:").append(((ModelRenderer) obj).rotationPointZ).append("\n");

                            res.append(fieldName).append(" - ").append("offsetX:").append(((ModelRenderer) obj).offsetX).append("\n");
                            res.append(fieldName).append(" - ").append("offsetY:").append(((ModelRenderer) obj).offsetY).append("\n");
                            res.append(fieldName).append(" - ").append("offsetZ:").append(((ModelRenderer) obj).offsetZ).append("\n");

                            res.append(fieldName).append(" - ").append("children:").append(((ModelRenderer) obj).childModels != null ? ((ModelRenderer) obj).childModels.size() : 0).append(", cubes:").append(((ModelRenderer) obj).cubeList != null ? ((ModelRenderer) obj).cubeList.size() : 0).append("\n");
                            res.append(fieldName).append(" - ").append("showModel:").append(((ModelRenderer) obj).showModel).append("\n");
                            res.append(fieldName).append(" - ").append("isHidden:").append(((ModelRenderer) obj).isHidden).append("\n");
                            res.append(fieldName).append(" - ").append("mirror:").append(((ModelRenderer) obj).mirror).append("\n");
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

    private static class Mappings {
        private static final String[] mappings = new String[] {
                "field_78089_u,textureHeight",
                "field_78090_t,textureWidth",
                "field_78116_c,bipedHead",
                "field_178720_f,bipedHeadwear",
                "field_78115_e,bipedBody",
                "field_178723_h,bipedRightArm",
                "field_178724_i,bipedLeftArm",
                "field_178721_j,bipedRightLeg",
                "field_178722_k,bipedLeftLeg",
                "field_187075_l,leftArmPose",
                "field_187076_m,rightArmPose",
                "field_78117_n,isSneak",
                "field_78095_p,swingProgress",
                "field_78093_q,isRiding",
                "field_78091_s,isChild",
                "field_78092_r,boxList",
                "field_78801_a,textureWidth",
                "field_78799_b,textureHeight",
                "field_178734_a,bipedLeftArmwear",
                "field_178732_b,bipedRightArmwear",
                "field_178733_c,bipedLeftLegwear",
                "field_178731_d,bipedRightLegwear",
                "field_178730_v,bipedBodyWear"
        };
        private static final Map<String, String> map;

        public static String getDeObfuscatedFieldName(String obf) {
            String ret = map.get(obf);

            if (ret == null) {
                ret = obf;
            }

            return ret;
        }

        static {
            map = new HashMap<>();

            for (String s : mappings) {
                String[] sa = s.split(",");

                map.put(sa[0], sa[1]);
            }
        }
    }
}
