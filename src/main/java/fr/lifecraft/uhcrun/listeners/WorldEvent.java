package fr.lifecraft.uhcrun.listeners;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.world.OrePopulator;

public class WorldEvent implements Listener {

    private final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldInit(final WorldInitEvent event) {
        World world = event.getWorld();

        if (world.getEnvironment() == Environment.NORMAL) {

            long start = System.currentTimeMillis();

            for (BlockPopulator pop : world.getPopulators()) {
                if (pop instanceof OrePopulator) {
                    return;
                }
            }
            world.getPopulators().add(new OrePopulator());

            long stop = System.currentTimeMillis();
            main.log("§aWorld successfully populated in " + (stop - start) + " ms");
        }

    }
}
