package fr.lifecraft.uhcrun.world;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class OrePopulator extends BlockPopulator {
    private static final int[] iterations = {3, 4, 4, 4, 2, 4};
    private static final int[] amount = {3, 8, 6, 6, 6, 4};
    private static final Material[] type = {Material.REDSTONE_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN};
    private static final int[] minHeight = {5, 5, 5, 5, 5, 5};
    private static final int[] maxHeight = {64, 64, 64, 64, 64, 64};

    public void populate(World world, Random random, Chunk chunk) {
        for (int i = 0; i < type.length; i++) {
            for (int j = 0; j < iterations[i]; j++) {
                int x = chunk.getX() * 16 + random.nextInt(16);
                int y = minHeight[i] + random.nextInt(maxHeight[i] - minHeight[i]);
                int z = chunk.getZ() * 16 + random.nextInt(16);

                makeOres(world, chunk, random, x, y, z, amount[i], type[i]);
            }
        }
    }

    private static void makeOres(World world, Chunk chunk, Random random, int x, int y, int z, int amount, Material type) {
        double angle = random.nextDouble() * 3.141592653589793D;

        double x1 = x + 8 + Math.sin(angle) * amount / 8.0D;
        double x2 = x + 8 - Math.sin(angle) * amount / 8.0D;
        double z1 = z + 8 + Math.cos(angle) * amount / 8.0D;
        double z2 = z + 8 - Math.cos(angle) * amount / 8.0D;

        double y1 = y + random.nextInt(3) + 2;
        double y2 = y + random.nextInt(3) + 2;
        for (int i = 0; i <= amount; i++) {
            double xPos = x1 + (x2 - x1) * i / amount;
            double yPos = y1 + (y2 - y1) * i / amount;
            double zPos = z1 + (z2 - z1) * i / amount;

            double fuzz = random.nextDouble() * amount / 16.0D;
            double fuzzXZ = (Math.sin((float) (i * 3.141592653589793D / amount)) + 1.0D) * fuzz + 1.0D;
            double fuzzY = (Math.sin((float) (i * 3.141592653589793D / amount)) + 1.0D) * fuzz + 1.0D;

            int xStart = (int) Math.floor(xPos - fuzzXZ / 2.0D);
            int yStart = (int) Math.floor(yPos - fuzzY / 2.0D);
            int zStart = (int) Math.floor(zPos - fuzzXZ / 2.0D);

            int xEnd = (int) Math.floor(xPos + fuzzXZ / 2.0D);
            int yEnd = (int) Math.floor(yPos + fuzzY / 2.0D);
            int zEnd = (int) Math.floor(zPos + fuzzXZ / 2.0D);
            for (int ix = xStart; ix <= xEnd; ix++) {
                double xThresh = (ix + 0.5D - xPos) / (fuzzXZ / 2.0D);
                if (xThresh * xThresh < 1.0D) {
                    for (int iy = yStart; iy <= yEnd; iy++) {
                        double yThresh = (iy + 0.5D - yPos) / (fuzzY / 2.0D);
                        if (xThresh * xThresh + yThresh * yThresh < 1.0D) {
                            for (int iz = zStart; iz <= zEnd; iz++) {
                                double zThresh = (iz + 0.5D - zPos) / (fuzzXZ / 2.0D);
                                if (xThresh * xThresh + yThresh * yThresh + zThresh * zThresh < 1.0D) {
                                    Block block = world.getBlockAt(ix, iy, iz);
                                    if ((block != null) && (block.getType() == Material.STONE)) {
                                        block.setType(type);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
