package com.artur114.armoredarms.client.util;

import net.minecraftforge.fml.common.Loader;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public enum EnumMods {
    COSMETIC_ARMOR("cosmeticarmorreworked"),
    CYBERWARE("cyberware");


    private AtomicBoolean loaded = null;
    private final String[] modId;

    EnumMods(String... modid) {
        this.modId = modid;
    }

    public boolean isLoaded() {
        if (this.loaded == null) {
            this.loaded = new AtomicBoolean(Arrays.stream(this.modId).anyMatch(Loader::isModLoaded));
        }

        return this.loaded.get();
    }
}
