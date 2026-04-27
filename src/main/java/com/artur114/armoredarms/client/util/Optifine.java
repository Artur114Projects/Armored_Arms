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

    public static boolean isShaders() {
        try {
            return Reflector.invokeMethod(Class.forName("Config"), null, "isShaders", new Class[0], new Object[0]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
