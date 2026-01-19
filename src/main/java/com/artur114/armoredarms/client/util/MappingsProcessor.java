package com.artur114.armoredarms.client.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MappingsProcessor {
    private static final Map<String, String> fieldsDeObfObf = new HashMap<>(9656);
    private static final Map<String, String> fieldsObfDeObf = new HashMap<>(9656);
    private static final String[] gavnoMappings = new String[] {
            "f_170673_,OVERLAY_SCALE",
            "f_170674_,HAT_OVERLAY_SCALE",
            "f_102808_,head",
            "f_102809_,hat",
            "f_102810_,body",
            "f_102811_,rightArm",
            "f_102812_,leftArm",
            "f_102813_,rightLeg",
            "f_102814_,leftLeg"
    };

    public static void load() {
        try {
            loadCSVMappings();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private static void loadCSVMappings() throws IOException {
        for (int i = 0; i != gavnoMappings.length; i++){
            String[] values = gavnoMappings[i].split(",");
            MappingsProcessor.fieldsDeObfObf.put(values[1], values[0]);
            MappingsProcessor.fieldsObfDeObf.put(values[0], values[1]);
        }
    }

    public static String getObfuscatedFieldName(String name) {
        String obfuscatedName = fieldsDeObfObf.get(name);
        if (obfuscatedName == null) {
            return name;
        }
        return obfuscatedName;
    }

    public static String getDeObfuscatedFieldName(String name) {
        String deObfuscatedName = fieldsObfDeObf.get(name);
        if (deObfuscatedName == null) {
            return name;
        }
        return deObfuscatedName;
    }

    static {
        load();
    }
}
