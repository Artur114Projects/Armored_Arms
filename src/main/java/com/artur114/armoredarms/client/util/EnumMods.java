package com.artur114.armoredarms.client.util;



import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public enum EnumMods {
    // noop...
    ;


    private AtomicBoolean loaded = null;
    private final String[] modId;

    EnumMods(String... modid) {
        this.modId = modid;
    }

    public boolean isLoaded() {
        if (this.loaded == null) {
            this.loaded = new AtomicBoolean(Arrays.stream(this.modId).anyMatch(modId -> ModList.get().isLoaded(modId)));
        }

        return this.loaded.get();
    }
}