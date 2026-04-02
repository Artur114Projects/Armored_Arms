package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLoggingManager {
    public final Logger AA_LOG = LogManager.getLogger("ARMOREDARMS");
    protected static final Map<String, Logger> loggers = new HashMap<>();
    protected final ShapelessLocationMap<String> localiseMap;

    protected AbstractLoggingManager() {
        this.localiseMap = this.initLocaliseMap();
    }

    public Logger namedLogger(String name) {
        return loggers.computeIfAbsent(name, LogManager::getLogger);
    }

    public void processException(RenderException exp) {
        try {
            if (exp instanceof RenderExceptionMulti) {
                for (RenderException exception : ((RenderExceptionMulti) exp).causes()) {
                    this.processSingleException(exception);
                }
            } else {
                this.processSingleException(exp);
            }
        } catch (Exception e) {
            AA_LOG.fatal("An error occurred during error handling (￣﹃￣) ", e);
        }
    }

    protected void processSingleException(RenderException exp) {
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
            this.pipeline().deactivate();
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

        ITextMessage[] messageForPlayer = this.compileMessageForPlayer(exp);

        for (ITextMessage comp : messageForPlayer) {
            comp.sendMessage();
        }
    }

    public ITextMessage[] compileMessageForPlayer(RenderException exp) {
        MessageFormating color = MessageFormating.RED;
        if (exp.type() == EnumExceptionType.FATAL) color = MessageFormating.DARK_RED;


        List<ITextMessage> list = new ArrayList<>();
        if (exp.messageForPlayer() != null) {
            list.add(this.createMessageTranslate(exp.messageForPlayer()).withFormating(color));
            list.add(this.createMessageLiteral(exp.getLocalizedMessage()).withFormating(color));
            return list.toArray(new ITextMessage[0]);
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

        String local = this.localise(localisationKey, (this.formatingToString(MessageFormating.YELLOW) + "[" + this.formatingToString(MessageFormating.UNDERLINE) + this.localisedComponentName(exp.brokenComponent()) + this.formatingToString(MessageFormating.RESET) + this.formatingToString(MessageFormating.YELLOW) +  "]" + this.formatingToString(color)));

        String[] strings = local.split(this.splitter());

        for (String m : strings) {
            list.add(this.createMessageLiteral(m.replaceAll(this.expMessageKey(), exp.getLocalizedMessage())).withFormating(color));
        }

        return list.toArray(new ITextMessage[0]);
    }

    private String localisedComponentName(IArmRenderComponent component) {
        String localisationKey = "armoredarms.unknown-component";
        Object[] args = new Object[] {"null", "unknown"};
        String type = "";
        if (component != null) type = component.type();
        if (component != null) args = new Object[] {CoreUtils.compressClassName(component.getClass()), type};
        if (component == null) localisationKey = "armoredarms.null-component";
        if (component == null) args = new Object[] {};

        if (component != null) {
            String rawKey = this.localiseMap.get(ShapelessLocation.location(type, component.getClass().getName()));

            if (rawKey != null && !rawKey.isEmpty()) {
                localisationKey = rawKey;
            }
        }

        return this.localise(localisationKey, args);
    }


    protected abstract String splitter();
    protected abstract String expMessageKey();
    protected abstract IArmRenderPipeline<?> pipeline();
    protected abstract String formatingToString(MessageFormating formating);
    protected abstract String localise(String translateKey, Object... parameters);
    protected abstract ITextMessage createMessageLiteral(String message);
    protected abstract ITextMessage createMessageTranslate(String key);
    protected abstract ShapelessLocationMap<String> initLocaliseMap();
}
