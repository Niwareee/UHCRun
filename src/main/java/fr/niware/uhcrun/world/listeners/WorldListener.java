package fr.niware.uhcrun.world.listeners;

import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Random;

public class WorldListener implements Listener {

    private final Random random;

    public WorldListener() {
        this.random = new Random();
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.getEntityType() != EntityType.DROPPED_ITEM) {
            return;
        }

        ItemStack itemStack = event.getEntity().getItemStack();

        if (itemStack.getType() == Material.SAPLING) {
            double percent = random.nextDouble();
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
            double percent = random.nextDouble();
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