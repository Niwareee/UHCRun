package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.*;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

import fr.niware.uhcrun.world.OrePopulator;

public class WorldListener extends Event implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldInit(final WorldInitEvent event) {
        World world = event.getWorld();

        if (world.getEnvironment() == Environment.NORMAL) {

            long start = System.currentTimeMillis();

            for (BlockPopulator populators : world.getPopulators()) {
                if (populators instanceof OrePopulator) {
                    return;
                }
            }

            //world.getPopulators().add(new OrePopulator());

            Main.getInstance().log("§aWorld successfully populated in " + (System.currentTimeMillis() - start) + " ms");
        }

    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
