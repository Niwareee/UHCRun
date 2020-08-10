package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.world.populator.OrePopulator;
import fr.niware.uhcrun.world.populator.SurgarCanePopulator;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Random;

public class WorldListener implements Listener {

    private final Random random;

    public WorldListener(){
        this.random = new Random();
    }

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

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        final double percent = random.nextDouble();

        // DEBUG
        // System.out.print("Block:" + itemStack.getType() + " Dura: " + itemStack.getDurability() + " Data: " + itemStack.getData());

        if (itemStack.getType() == Material.SAPLING) {
            if (percent <= 45 * 0.01) {
                itemStack.setType(Material.APPLE);
                itemStack.setData(new MaterialData(Material.APPLE));
                itemStack.setDurability((short) 0);
                return;
            }
            event.getEntity().remove();
        } else if (itemStack.getType() == Material.STONE) {
            itemStack.setType(Material.COBBLESTONE);
            itemStack.setDurability((short) 0);
        } else if (itemStack.getType() == Material.GRAVEL) {
            if (percent <= 80 * 0.01) {
                itemStack.setType(Material.ARROW);
                itemStack.setAmount(3);
            } else {
                itemStack.setType(Material.FLINT);
            }
        } else if (itemStack.getType() == Material.IRON_ORE) {
            itemStack.setType(Material.IRON_INGOT);
            itemStack.setAmount(2);
            spawnExtraXp(event.getLocation(), 3);
        } else if (itemStack.getType() == Material.DIAMOND) {
            itemStack.setType(Material.DIAMOND);
            itemStack.setAmount(2);
            spawnExtraXp(event.getLocation(), 10);
        } else if (itemStack.getType() == Material.GOLD_ORE) {
            itemStack.setType(Material.GOLD_INGOT);
            itemStack.setAmount(2);
            spawnExtraXp(event.getLocation(), 4);
        } else if (itemStack.getType() == Material.COAL) {
            itemStack.setType(Material.TORCH);
            itemStack.setAmount(3);
            spawnExtraXp(event.getLocation(), 2);
        } else if (itemStack.getType() == Material.REDSTONE_ORE) {
            spawnExtraXp(event.getLocation(), 2);
        } else if (itemStack.getType() == Material.SAND) {
            itemStack.setType(Material.GLASS);
            itemStack.setAmount(2);
        } else if (itemStack.getType() == Material.OBSIDIAN) {
            itemStack.setType(Material.OBSIDIAN);
            itemStack.setAmount(2);
        } else if (itemStack.getType() == Material.CACTUS) {
            itemStack.setType(Material.LOG);
            itemStack.setAmount(2);
        } else if (itemStack.getType() == Material.SUGAR_CANE) {
            itemStack.setType(Material.BOOK);
            itemStack.setAmount(1);
        }
    }

    public void spawnExtraXp(Location location, int quantity) {
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();
        world.addEntity(new EntityExperienceOrb(world, location.getX(), location.getY(), location.getZ(), quantity));
    }
}
