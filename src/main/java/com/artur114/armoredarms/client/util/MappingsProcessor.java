package com.artur114.armoredarms.client.util;

import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MappingsProcessor {
    private static final Map<String, String> fieldsDeObfObf = new HashMap<>(9656);
    private static final Map<String, String> fieldsObfDeObf = new HashMap<>(9656);

    public static void load() {
        try {
            loadCSVMappings(fieldsDeObfObf, fieldsObfDeObf, "fields.csv");
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private static void loadCSVMappings(Map<String, String> deObfObf, Map<String, String> obfDeObf, String fileName) throws IOException {
        InputStream stream = Minecraft.class.getResourceAsStream("/assets/armoredarms/mappings/" + fileName);
        if (stream == null) {
            throw new NullPointerException();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            deObfObf.put(values[1], values[0]);
            obfDeObf.put(values[0], values[1]);
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
