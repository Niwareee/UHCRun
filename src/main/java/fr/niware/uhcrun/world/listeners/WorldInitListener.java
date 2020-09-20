package fr.niware.uhcrun.world.listeners;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.world.patchers.BiomesPatcher;
import fr.niware.uhcrun.world.patchers.CenterPatcher;
import fr.niware.uhcrun.world.patchers.WorldGenCavesPatcher;
import fr.niware.uhcrun.world.populators.OrePopulator;
import fr.niware.uhcrun.world.populators.SurgarCanePopulator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldInitListener implements Listener {

    private final UHCRun main;

    public WorldInitListener(UHCRun main){
        this.main = main;
    }

    @EventHandler
    public void onWorldInit(final WorldInitEvent event) {
        World world = event.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        long start = System.currentTimeMillis();
        BiomesPatcher.removeBiomes();

        try {
            CenterPatcher.load();
            WorldGenCavesPatcher.load(world, 3);
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }

        OrePopulator orePopulator = new OrePopulator();
        orePopulator.addRule(new OrePopulator.Rule(Material.DIAMOND_ORE, 4, 0, 64, 5));
        orePopulator.addRule(new OrePopulator.Rule(Material.IRON_ORE, 4, 0, 64, 12));
        orePopulator.addRule(new OrePopulator.Rule(Material.GOLD_ORE, 2, 0, 64, 8));
        orePopulator.addRule(new OrePopulator.Rule(Material.LAPIS_ORE, 3, 0, 64, 4));
        orePopulator.addRule(new OrePopulator.Rule(Material.OBSIDIAN, 4, 0, 32, 4));

        world.getPopulators().add(orePopulator);
        world.getPopulators().add(new SurgarCanePopulator(1));

        main.log("§aWorld successfully init in " + (System.currentTimeMillis() - start) + " ms");
    }
}
