package com.artur114.armoredarms.main;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class LoggingManager extends AbstractLoggingManager {
    @Override
    protected String splitter() {
        return "//////";
    }

    @Override
    protected String expMessageKey() {
        return "/exp/";
    }

    @Override
    protected IArmRenderPipeline<?> pipeline() {
        return ArmoredArmsApi.currentPipeline();
    }

    @Override
    protected String formatingToString(MessageFormating formating) {
        return EnumChatFormatting.valueOf(formating.name()).toString();
    }

    @Override
    protected String localise(String translateKey, Object... parameters) {
        if (translateKey.equals("%1$s")) {
            return parameters[0].toString();
        }
        return I18n.format(translateKey, parameters);
    }

    @Override
    protected ITextMessage createMessageLiteral(String message) {
        return new TextMessage(new ChatComponentText(message));
    }

    @Override
    protected ITextMessage createMessageTranslate(String key) {
        return new TextMessage(new ChatComponentTranslation(key));
    }

    @Override
    protected ShapelessLocationMap<String> initLocaliseMap() {
        ShapelessLocationMap<String> map = new ShapelessLocationMap<>();
        map.put(new ShapelessLocation("layer:*"), "armoredarms.layer");
        map.put(new ShapelessLocation("engine:*"), "armoredarms.engine");
        map.put(new ShapelessLocation("pipeline:*"), "armoredarms.pipeline");
        map.put(new ShapelessLocation("model-manager:*"), "armoredarms.model-manager");
        return map;
    }

    private static class TextMessage implements ITextMessage {
        private final IChatComponent component;

        private TextMessage(IChatComponent component) {
            this.component = component;
        }

        @Override
        public ITextMessage withFormating(MessageFormating formating) {
            EnumChatFormatting format = EnumChatFormatting.valueOf(formating.name());
            this.component.getChatStyle().setColor(format);
            return this;
        }

        @Override
        public void sendMessage() {
            Minecraft.getMinecraft().thePlayer.addChatMessage(this.component);
        }

        @Override
        public String string() {
            return this.component.getFormattedText();
        }
    }
}
