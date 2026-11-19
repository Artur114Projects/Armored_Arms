package com.artur114.armoredarms.client.integration.emf.modelrender;

import net.minecraft.client.Minecraft;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

abstract class EMFHandContext implements AutoCloseable {
    private static final String LEGACY_CONTEXT_CLASS = "traben.entity_model_features.models.animation.EMFAnimationEntityContext";
    private static final String ETF_STATE_CLASS = "traben.entity_texture_features.features.state.ETFState";
    private static final boolean LEGACY = isClassPresent(LEGACY_CONTEXT_CLASS);

    static EMFHandContext open() {
        EMFEntityRenderState state = (EMFEntityRenderState) ETFEntityRenderState
                .forEntity((ETFEntity) Minecraft.getInstance().player);
        return LEGACY ? new Legacy(state) : new Modern(state);
    }

    @Override
    public abstract void close();

    private static boolean isClassPresent(String name) {
        try {
            Class.forName(name, false, EMFHandContext.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static MethodHandle findHandle(String owner, String name, MethodType type, boolean isStatic) {
        try {
            Class<?> ownerClass = Class.forName(owner, false, EMFHandContext.class.getClassLoader());
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return isStatic ? lookup.findStatic(ownerClass, name, type) : lookup.findVirtual(ownerClass, name, type);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unsupported Entity Model Features API: " + owner + "#" + name, e);
        }
    }

    private static Object invoke(MethodHandle handle, Object... args) {
        try {
            return handle.invokeWithArguments(args);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("deprecation")
    private static final class Legacy extends EMFHandContext {
        private Legacy(EMFEntityRenderState state) {
            EMFAnimationEntityContext.setCurrentEntityIteration(state);
            EMFAnimationEntityContext.isFirstPersonHand = true;
        }

        @Override
        public void close() {
            EMFAnimationEntityContext.reset();
        }
    }

    private static final class Modern extends EMFHandContext {
        private static final String EMF_STATE_CLASS = "traben.entity_model_features.models.animation.state.EMFEntityRenderState";
        private static final MethodHandle CURRENT_STATE = findHandle(ETF_STATE_CLASS, "state",
                MethodType.methodType(ETFEntityRenderState.class), true);
        private static final MethodHandle MOUNT = findHandle(ETF_STATE_CLASS, "mount",
                MethodType.methodType(void.class, ETFEntityRenderState.class), true);
        private static final MethodHandle UNMOUNT = findHandle(ETF_STATE_CLASS, "unMount",
                MethodType.methodType(void.class), true);
        private static final MethodHandle SET_MANUAL_PLAYER_STATE = findHandle(EMF_STATE_CLASS, "setManualPlayerState",
                MethodType.methodType(void.class, boolean.class), false);
        private static final MethodHandle SET_FIRST_PERSON_HAND = findHandle(EMF_STATE_CLASS, "setIsFirstPersonHand",
                MethodType.methodType(void.class, boolean.class), false);

        private ETFEntityRenderState mountedByUs;

        private Modern(EMFEntityRenderState state) {
            invoke(SET_MANUAL_PLAYER_STATE, state, true);
            if (invoke(CURRENT_STATE) != state) {
                invoke(MOUNT, state);
                this.mountedByUs = state;
            }
            invoke(SET_FIRST_PERSON_HAND, state, true);
        }

        @Override
        public void close() {
            if (this.mountedByUs != null && invoke(CURRENT_STATE) == this.mountedByUs) {
                invoke(UNMOUNT);
            }
            this.mountedByUs = null;
        }
    }
}