package com.artur114.armoredarms.main;

import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.*;

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
        return ArmoredArms.pipeline;
    }

    @Override
    protected String formatingToString(MessageFormating formating) {
        return TextFormatting.valueOf(formating.name()).toString();
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
        return new TextMessage(new TextComponentString(message));
    }

    @Override
    protected ITextMessage createMessageTranslate(String key) {
        return new TextMessage(new TextComponentTranslation(key));
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
        private final ITextComponent component;

         private TextMessage(ITextComponent component) {
             this.component = component;
         }

         @Override
         public ITextMessage withFormating(MessageFormating formating) {
             TextFormatting format = TextFormatting.valueOf(formating.name());
             this.component.getStyle().setColor(format);
             return this;
         }

         @Override
         public void sendMessage() {
            Minecraft.getMinecraft().player.sendMessage(this.component);
         }

         @Override
         public String string() {
             return component.getFormattedText();
         }
     }
}
