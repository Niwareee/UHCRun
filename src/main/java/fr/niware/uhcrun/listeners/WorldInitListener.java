package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.world.OrePopulator;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldInitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldInit(final WorldInitEvent event) {
        World world = event.getWorld();
        if (world.getEnvironment() == Environment.NORMAL) {
            long start = System.currentTimeMillis();
            world.getPopulators().add(new OrePopulator());
            Main.getInstance().log("§aWorld successfully populated in " + (System.currentTimeMillis() - start) + " ms");
        }
    }
}
