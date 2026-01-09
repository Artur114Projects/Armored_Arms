package com.artur114.armoredarms.client.integration;

import cpw.mods.fml.common.Loader;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ModsList {
    COSMETIC_ARMOR("cosmeticarmorreworked"),
    THAUMCRAFT("Thaumcraft"),
    BACKHAND("backhand");

    private AtomicBoolean loaded = null;
    private final String[] modId;
    ModsList(String... modid) {
        this.modId = modid;
    }

    public boolean isLoaded() {
        if (this.loaded == null) {
            this.loaded = new AtomicBoolean(Arrays.stream(this.modId).anyMatch(Loader::isModLoaded));
        }

        return this.loaded.get();
    }
}
