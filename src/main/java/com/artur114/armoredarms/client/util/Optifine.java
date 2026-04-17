package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.Reflector;

public class Optifine {
    private static final boolean LOADED;

    static {
        LOADED = Reflector.isClassExists("optifine.Installer");
    }

    public static boolean isLoaded() {
        return LOADED;
    }
}
