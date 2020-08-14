package fr.niware.uhcrun.world.patch;

import net.minecraft.server.v1_8_R3.BiomeBase;

import java.lang.reflect.Field;

public class BiomesPatcher {

    public static void removeBiomes() {
        try {
            Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);
            if ((biomesField.get(null) instanceof BiomeBase[])) {
                BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
                biomes[BiomeBase.STONE_BEACH.id] = BiomeBase.DESERT;
                biomes[BiomeBase.ROOFED_FOREST.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FROZEN_RIVER.id] = BiomeBase.RIVER;
                biomes[BiomeBase.COLD_TAIGA_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.COLD_TAIGA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.EXTREME_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.EXTREME_HILLS_PLUS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.FOREST;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.DESERT;
                biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.DESERT;
                biomes[BiomeBase.MEGA_TAIGA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MEGA_TAIGA_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MESA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MESA_PLATEAU.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MESA_PLATEAU_F.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.ICE_PLAINS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.ICE_MOUNTAINS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.ICE_MOUNTAINS.id] = BiomeBase.PLAINS;
                biomesField.set(null, biomes);
            }
        } catch (Exception ignored) {
        }
    }
}
