package com.artur114.armoredarms.core.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapelessLocationMapTest {

    @Test
    public void testAll() {
        ShapelessLocationMap<String> map = new ShapelessLocationMap<>();

        String ingot = "ingot";
        String block = "block";

        String stone = "stone";
        String iron = "iron";

        String grass = "grass";
        String copper = "copper";

        String dirt = "dirt";
        String brass = "brass";

        map.put(ShapelessLocation.location("*", "*"), "absolute");

        map.put(ShapelessLocation.location(block, "*"), stone);
        map.put(ShapelessLocation.location(ingot, "*"), iron);

        map.put(ShapelessLocation.location(block, grass), grass);
        map.put(ShapelessLocation.location(ingot, copper), copper);

        assertEquals(stone, map.get(ShapelessLocation.location(block, dirt)));
        assertEquals(iron, map.get(ShapelessLocation.location(ingot, brass)));

        assertEquals(grass, map.get(ShapelessLocation.location(block, grass)));
        assertEquals(copper, map.get(ShapelessLocation.location(ingot, copper)));

        assertArrayEquals(new String[] {"absolute", iron, copper}, map.getAll(ShapelessLocation.location(ingot, "*")).toArray(new String[0]));
        assertArrayEquals(new String[] {"absolute", grass, stone}, map.getAll(ShapelessLocation.location(block, "*")).toArray(new String[0]));

        assertTrue(map.values().containsAll(Arrays.asList(stone, iron, grass, copper, "absolute")));
    }
}