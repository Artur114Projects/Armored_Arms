package com.artur114.armoredarms.client.util;

import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum ModsEnum {
    AZURE_LIB("azurelib"),
    GECKO_LIB("geckolib");

    private AtomicBoolean loaded = null;
    private final String[] modId;
    ModsEnum(String... modid) {
        this.modId = modid;
    }

    public boolean suppleIfLoaded(Supplier<Boolean> supplier) {
        if (this.isLoaded()) {
            return supplier.get();
        }
        return false;
    }

    public boolean isLoaded() {
        if (this.loaded == null) {
            this.loaded = new AtomicBoolean(Arrays.stream(this.modId).anyMatch((m) -> ModList.get().isLoaded(m)));
        }

        return this.loaded.get();
    }
}