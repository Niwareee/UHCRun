package fr.niware.uhcrun.world.patchers;

import fr.niware.uhcrun.utils.scoreboard.FastReflection;
import net.minecraft.server.v1_8_R3.*;

import java.lang.reflect.Field;
import java.util.Random;

public class CenterPatcher {
    public static void load() throws ReflectiveOperationException {
        Field worldGenTreesField = BiomeBase.class.getDeclaredField("aA");
        worldGenTreesField.setAccessible(true);

        Field worldGenBigTreeField = BiomeBase.class.getDeclaredField("aB");
        worldGenBigTreeField.setAccessible(true);

        Field worldGenSwampTreeField = BiomeBase.class.getDeclaredField("aC");
        worldGenSwampTreeField.setAccessible(true);

        for (BiomeBase biomeBase : BiomeBase.getBiomes()) {
            if (biomeBase == null) continue;

            worldGenTreesField.set(biomeBase, new WorldGenTreesPatched(false));
            worldGenBigTreeField.set(biomeBase, new WorldGenBigTreePatched(false));
            worldGenSwampTreeField.set(biomeBase, new WorldGenSwampTreePatched());
        }

        Field worldGenForestFirstField = BiomeForest.class.getDeclaredField("aD");
        worldGenForestFirstField.setAccessible(true);

        Field worldGenForestSecondField = BiomeForest.class.getDeclaredField("aE");
        worldGenForestSecondField.setAccessible(true);

        Field worldGenForestTreeField = BiomeForest.class.getDeclaredField("aF");
        worldGenForestTreeField.setAccessible(true);

        FastReflection.setFinalStatic(worldGenForestFirstField, new WorldGenForestPatched(false, true));
        FastReflection.setFinalStatic(worldGenForestSecondField, new WorldGenForestPatched(false, false));
        FastReflection.setFinalStatic(worldGenForestTreeField, new WorldGenForestTreePatched(false));
    }

    private static class WorldGenTreesPatched extends WorldGenTrees {
        WorldGenTreesPatched(boolean b) {
            super(b);
        }

        @Override
        public boolean generate(World world, Random random, BlockPosition blockPosition) {
            return !(blockPosition.getX() > -64 && blockPosition.getX() < 64 && blockPosition.getZ() > -64 && blockPosition.getZ() < 64) && super.generate(world, random, blockPosition);
        }
    }

    private static class WorldGenBigTreePatched extends WorldGenBigTree {
        WorldGenBigTreePatched(boolean b) {
            super(b);
        }

        @Override
        public boolean generate(World world, Random random, BlockPosition blockPosition) {
            return !(blockPosition.getX() > -64 && blockPosition.getX() < 64 && blockPosition.getZ() > -64 && blockPosition.getZ() < 64) && super.generate(world, random, blockPosition);
        }
    }

    private static class WorldGenSwampTreePatched extends WorldGenSwampTree {
        @Override
        public boolean generate(World world, Random random, BlockPosition blockPosition) {
            return !(blockPosition.getX() > -64 && blockPosition.getX() < 64 && blockPosition.getZ() > -64 && blockPosition.getZ() < 64) && super.generate(world, random, blockPosition);
        }
    }

    private static class WorldGenForestPatched extends WorldGenForest {
        WorldGenForestPatched(boolean b, boolean b1) {
            super(b, b1);
        }

        @Override
        public boolean generate(World world, Random random, BlockPosition blockPosition) {
            return !(blockPosition.getX() > -64 && blockPosition.getX() < 64 && blockPosition.getZ() > -64 && blockPosition.getZ() < 64) && super.generate(world, random, blockPosition);
        }
    }

    private static class WorldGenForestTreePatched extends WorldGenForestTree {
        WorldGenForestTreePatched(boolean flag) {
            super(flag);
        }

        @Override
        public boolean generate(World world, Random random, BlockPosition blockPosition) {
            return !(blockPosition.getX() > -64 && blockPosition.getX() < 64 && blockPosition.getZ() > -64 && blockPosition.getZ() < 64) && super.generate(world, random, blockPosition);
        }
    }
}