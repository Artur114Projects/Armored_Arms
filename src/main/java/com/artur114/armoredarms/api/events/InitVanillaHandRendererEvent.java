package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.api.IVanillaHandRenderer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.Event;

public class InitVanillaHandRendererEvent extends Event {
    private IVanillaHandRenderer renderer = null;

    public void setRendererIfModLoaded(Class<? extends IVanillaHandRenderer> clazz, String modId) {
        if (Loader.isModLoaded(modId)) {
            this.setRenderer(clazz);
        }
    }

    public void setRenderer(Class<? extends IVanillaHandRenderer> clazz) {
        try {
            IVanillaHandRenderer renderer = clazz.newInstance();

            if (this.renderer == null) {
                this.renderer = renderer;
            } else if (this.renderer.isCombinable() && renderer.isCombinable()) {
                this.renderer = IVanillaHandRenderer.combine(this.renderer, renderer);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            new RuntimeException("Failed to create an object", e).printStackTrace(System.err);
        }
    }

    public IVanillaHandRenderer renderer() {
        return this.renderer;
    }
}
