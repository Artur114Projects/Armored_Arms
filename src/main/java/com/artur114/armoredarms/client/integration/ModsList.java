package com.artur114.armoredarms.client.integration;

import cpw.mods.fml.common.Loader;

import java.util.concurrent.atomic.AtomicBoolean;

public enum ModsList {
    THAUMCRAFT("Thaumcraft");

    private AtomicBoolean loaded = null;
    private final String modId;
    ModsList(String modid) {
        this.modId = modid;
    }

    public boolean isLoaded() {
        if (this.loaded == null) {
            this.loaded = new AtomicBoolean(Loader.isModLoaded(this.modId));
        }

        return this.loaded.get();
    }
}
