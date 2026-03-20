package com.artur114.armoredarms.main;

import com.artur114.armoredarms.client.engines.ArmRenderEngineCleanRoom;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineCleanRoom;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.util.EnumExceptionType;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingManager {
    public final Logger AA_LOG = LogManager.getLogger("ARMOREDARMS");
    protected static final Map<String, Logger> loggers = new HashMap<>();

    public Logger logger(String name) {
        return loggers.computeIfAbsent(name, LogManager::getLogger);
    }

    public void processException(RenderException exp) {
        IArmRenderComponent broken = exp.brokenComponent();
        Level level = Level.ERROR;

        if (exp.type() == EnumExceptionType.FATAL) {
            level = Level.FATAL;
        }

        if (exp.type() == EnumExceptionType.WARN) {
            level = Level.WARN;
        }

        if (exp.type() != EnumExceptionType.WARN && broken != null) {
            broken.deactivate();
        }
        if (exp.type() == EnumExceptionType.FATAL) {
            ArmoredArms.pipeline.deactivate();
        }

        String component = "unknown-component";
        String message = "an error occurred in component: ";

        if (broken != null) {
            component = broken.type();
        }

        switch (exp.type()) {
            case WARN:
                message = "Warn an error occurred in component: " + component;
                break;
            case ERROR:
                message = "An error occurred in component: " + component;
                break;
            case FATAL:
                message = "An fatal error occurred in component: " + component;
                break;
        }

        AA_LOG.log(level, message, exp);

        Minecraft mc = Minecraft.getMinecraft();

        ITextComponent[] messageForPlayer = this.compileMessageForPlayer(exp);

        for (ITextComponent comp : messageForPlayer) {
            mc.player.sendMessage(comp);
        }
    }

    public ITextComponent[] compileMessageForPlayer(RenderException exp) {
        Style red = new Style().setColor(TextFormatting.RED);
        List<ITextComponent> list = new ArrayList<>();
        if (exp.messageForPlayer() != null) {
            list.add(new TextComponentTranslation(exp.messageForPlayer()).setStyle(red));
            list.add(new TextComponentString(TextFormatting.RED + exp.getLocalizedMessage()));
            return list.toArray(new ITextComponent[0]);
        }

        String localisationKey;

        switch (exp.type()) {
            case FATAL:
                localisationKey = "armoredarms.error.fatal";
                break;
            case WARN:
                localisationKey = "armoredarms.error.warn";
                break;
            default:
                localisationKey = "armoredarms.error";
        }

        String local = I18n.format(localisationKey, (TextFormatting.YELLOW + "[" + TextFormatting.UNDERLINE + this.localisedComponentName(exp.brokenComponent()) + TextFormatting.RESET + TextFormatting.YELLOW +  "]" + TextFormatting.RED));

        String[] strings = local.split("//////");

        for (String m : strings) {
            list.add(new TextComponentString(m.replaceAll("/exp/", exp.getLocalizedMessage())).setStyle(red));
        }

        return list.toArray(new ITextComponent[0]);
    }

    public String compressClassName(Class<?> clazz) {
        return this.compressClassName(clazz, 2);
    }

    public String compressClassName(Class<?> clazz, int noCutPackagesCount) {
        String className = clazz.getName();
        int lastPoint = className.lastIndexOf(".");

        if (noCutPackagesCount <= 0) {
            return className.substring(lastPoint + 1);
        }

        int substringPoint = 0;
        int packagesCount = 0;

        for (int i = 0; i != className.length(); i++) {
            char c = className.charAt(i);

            if (c == '.') {
                substringPoint = i;
                packagesCount++;
            }

            if (packagesCount >= noCutPackagesCount) {
                break;
            }
        }

        if (substringPoint == lastPoint) {
            return className;
        }

        return className.substring(0, substringPoint) + ";" + className.substring(lastPoint + 1);
    }

    private String localisedComponentName(IArmRenderComponent component) {
        String localisationKey = "armoredarms.unknown-component";
        Object[] args = new Object[] {"null", "unknown"};
        String type = "";
        if (component != null) type = component.type();
        if (component != null) args = new Object[] {this.compressClassName(component.getClass()), type};
        if (component == null) localisationKey = "armoredarms.null-component";
        if (component == null) args = new Object[] {};


        switch (type) {
            case "layer":
                if (component.getClass() == ArmRenderLayerArmor.class) {
                    localisationKey = "armoredarms.layer.armor";
                    args = new Object[] {};
                } else if (component.getClass() == ArmRenderLayerHand.class) {
                    localisationKey = "armoredarms.layer.hand";
                    args = new Object[] {};
                } else {
                    localisationKey = "armoredarms.layer";
                    args = new Object[] {this.compressClassName(component.getClass())};
                }
            break;
            case "engine":
                if (component.getClass() == ArmRenderEngineForge.class) {
                    localisationKey = "armoredarms.engine.forge";
                    args = new Object[] {};
                } else if (component.getClass() == ArmRenderEngineCleanRoom.class) {
                    localisationKey = "armoredarms.engine.cleanroom";
                    args = new Object[] {};
                } else {
                    localisationKey = "armoredarms.engine";
                    args = new Object[] {this.compressClassName(component.getClass())};
                }
            break;
            case "pipeline":
                if (component.getClass() == ArmRenderPipelineForge.class) {
                    localisationKey = "armoredarms.pipeline.forge";
                    args = new Object[] {};
                } else if (component.getClass() == ArmRenderPipelineCleanRoom.class) {
                    localisationKey = "armoredarms.pipeline.cleanroom";
                    args = new Object[] {};
                } else {
                    localisationKey = "armoredarms.pipeline";
                    args = new Object[] {this.compressClassName(component.getClass())};
                }
            break;
            case "model-manager":
                if (component.getClass() == ArmModelManagerPlayer.class || component.getClass() == ArmModelManagerArmor.class) {
                    localisationKey = "armoredarms.model-manager.def";
                    args = new Object[] {};
                } else {
                    localisationKey = "armoredarms.model-manager";
                    args = new Object[] {this.compressClassName(component.getClass())};
                }
            break;
        }

        return I18n.format(localisationKey, args);
    }
}
