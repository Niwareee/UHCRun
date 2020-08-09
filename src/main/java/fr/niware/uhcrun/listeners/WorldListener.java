package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.world.populator.OrePopulator;
import fr.niware.uhcrun.world.populator.SurgarCanePopulator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldInit(final WorldInitEvent event) {
        World world = event.getWorld();
        if (world.getEnvironment() == Environment.NORMAL) {
            long start = System.currentTimeMillis();

            world.getPopulators().add(new SurgarCanePopulator(2));
            world.getPopulators().add(new OrePopulator());

            Main.getInstance().log("§aWorld successfully populated in " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        Block block = event.getBlock();
        Random random = new Random();

        if (random.nextDouble() <= 0.02) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5D, 0.3, 0.5D), new ItemStack(Material.APPLE, 1));
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.WITCH || event.getEntityType() == EntityType.GUARDIAN || event.getEntityType() == EntityType.BAT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (!State.isState(State.MINING)) {
            Player player = event.getPlayer();

            event.setCancelled(true);
            player.sendMessage("§cErreur: Le nether est désactivé.");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2F, 2F);
        }
    }
}
