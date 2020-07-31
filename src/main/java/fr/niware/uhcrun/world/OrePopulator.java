package fr.niware.uhcrun.world;

import java.util.Random;

import net.minecraft.server.v1_8_R3.WorldGenCaves;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class OrePopulator extends BlockPopulator {

    private static final int[] iterations = {3, 4, 4, 4, 2, 4};
    private static final int[] amount = {4, 6, 4, 4, 4, 4};
    private static final Material[] type = {Material.REDSTONE_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN};
    // private static final int[] minHeight = {5, 5, 5, 5, 5, 5};
    private static final int[] maxHeight = {64, 64, 64, 64, 64, 64};

    public void populate(World world, Random random, Chunk source) {
        for (int i = 0; i < type.length; i++) {
            for (int j = 0; j < iterations[i]; j++)
                makeOres(source, random, random.nextInt(16), random.nextInt(maxHeight[i]), random.nextInt(16), amount[i], type[i]);
        }
    }

    private static void makeOres(Chunk source, Random random, int originX, int originY, int originZ, int amount, Material type) {
        for (int i = 0; i < amount; i++) {
            int x = originX + random.nextInt(amount / 2) - amount / 4;
            int y = originY + random.nextInt(amount / 2) - amount / 4;
            int z = originZ + random.nextInt(amount / 2) - amount / 4;
            x &= 0xF;
            z &= 0xF;
            if (y <= 127 && y >= 0) {
                Block block = source.getBlock(x, y, z);
                if (block.getType() == Material.STONE)
                    block.setType(type, false);
            }
        }
    }
}

