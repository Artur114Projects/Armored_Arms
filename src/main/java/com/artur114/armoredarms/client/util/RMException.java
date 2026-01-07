package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.api.IArmRenderLayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;


public class RMException extends RuntimeException {
    private IArmRenderLayer fatalOnLayer = null;
    private String messageForPlayer = null;
    private boolean isFatal = false;
    private Method method = null;

    public RMException() {}

    public RMException(String message) {
        super(message);
    }

    public RMException(String message, Throwable cause) {
        super(message, cause);

        this.processCause(cause);
    }

    public RMException(Throwable cause) {
        super(cause);

        this.processCause(cause);
    }

    public RMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

        this.processCause(cause);
    }

    private void processCause(Throwable cause) {
        if (cause instanceof RMException) {
            this.isFatal |= ((RMException) cause).isFatal;
            this.messageForPlayer = this.nonNull(((RMException) cause).messageForPlayer, this.messageForPlayer);
            this.fatalOnLayer = this.nonNull(((RMException) cause).fatalOnLayer, this.fatalOnLayer);
            this.method = this.nonNull(((RMException) cause).method, this.method);
        }
    }

    private <T> T nonNull(T t1, T t2) {
        if (t1 == null) {
            return t2;
        }
        return t1;
    }

    public RMException setFatal() {
        this.isFatal = true; return this;
    }

    public RMException setFatalLayer(IArmRenderLayer layer) {
        this.fatalOnLayer = layer; return this;
    }

    public RMException setMethod(Method method) {
        this.method = method; return this;
    }

    public RMException setMessage(String message) {
        this.messageForPlayer = message; return this;
    }

    public boolean isFatal() {
        return this.isFatal;
    }

    public boolean isFatalOnLayer() {
        return this.fatalOnLayer != null;
    }

    public IArmRenderLayer fatalLayer() {
        return this.fatalOnLayer;
    }

    public Method method() {
        return this.method;
    }

    public IChatComponent[] messageForPlayer() {
        ArrayList<IChatComponent> list = new ArrayList<>(4);
        if (this.messageForPlayer != null) {
            list.add(0, new ChatComponentTranslation(this.messageForPlayer + "." + this.method.nameString()));
        }
        if (this.isFatal) {
            list.add(0, new ChatComponentTranslation("armoredarms.error.fatal." + this.method.nameString()));
        }
        if (this.isFatalOnLayer()) {
            list.add(0, new ChatComponentTranslation("armoredarms.error.layer.fatal." + this.method.nameString()));

            list.add(new ChatComponentText("Layer: " + this.fatalOnLayer.getClass().getName()));
        }
        if (list.isEmpty()) {
            list.add(new ChatComponentTranslation("armoredarms.error.unknown." + this.method.nameString()));
        }
        list.add(new ChatComponentText("Message: " + this.getMessage()));
        return list.toArray(new IChatComponent[0]);
    }

    public enum Method {
        TICK, RENDER;

        public String nameString() {
            switch (this) {
                case TICK:
                    return "tryTick";
                case RENDER:
                    return "tryRender";
                default:
                    return "wtf?";
            }
        }
    }
}
