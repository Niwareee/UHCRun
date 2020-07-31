package fr.lifecraft.uhcrun.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class BlockListener implements Listener {

    private final Random random;

    public BlockListener() {
        this.random = new Random();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

        System.out.print(random.nextInt(100));
        System.out.print(random.nextInt(100));
        System.out.print(random.nextInt(50));
        System.out.print(random.nextInt(50));
        EntityType entityType = event.getEntity().getType();
        List<ItemStack> newDrops = null;

        if (entityType == EntityType.COW || entityType == EntityType.HORSE) {
            newDrops = new ArrayList<>();
            for (ItemStack stack : event.getDrops()) {
                if (stack.getType() == Material.RAW_BEEF) {
                    newDrops.add(new ItemStack(Material.COOKED_BEEF, stack.getAmount() * 2));
                } else if (stack.getType() == Material.LEATHER) {
                    newDrops.add(new ItemStack(Material.BOOK));
                }
            }
        } else if (entityType == EntityType.SHEEP) {
            newDrops = event.getDrops().stream().filter(stack -> stack.getType() == Material.MUTTON).map(stack -> new ItemStack(Material.COOKED_MUTTON, stack.getAmount() * 2)).collect(Collectors.toList());
            if (random.nextInt(32) >= 16) {
                newDrops.add(new ItemStack(Material.LEATHER, random.nextInt(5) + 1));
            }
            if (random.nextInt(32) >= 16) {
                newDrops.add(new ItemStack(Material.STRING, random.nextInt(2) + 1));
            }
        } else if (entityType == EntityType.PIG) {
            newDrops = event.getDrops().stream().filter(stack -> stack.getType() == Material.PORK).map(stack -> new ItemStack(Material.GRILLED_PORK, stack.getAmount() * 2)).collect(Collectors.toList());
            if (random.nextInt(32) >= 16) {
                newDrops.add(new ItemStack(Material.LEATHER, random.nextInt(5) + 1));
            }
        } else if (entityType == EntityType.RABBIT) {
            newDrops = event.getDrops().stream().filter(stack -> stack.getType() == Material.RABBIT).map(stack -> new ItemStack(Material.COOKED_RABBIT, stack.getAmount() * 2)).collect(Collectors.toList());
        } else if (entityType == EntityType.CHICKEN) {
            newDrops = new ArrayList<>();
            for (ItemStack stack : event.getDrops()) {
                if (stack.getType() == Material.RAW_CHICKEN) {
                    newDrops.add(new ItemStack(Material.COOKED_CHICKEN, stack.getAmount() * 2));
                } else if (stack.getType() == Material.FEATHER) {
                    newDrops.add(new ItemStack(Material.ARROW, stack.getAmount()));
                }
            }
        } else if (entityType == EntityType.ZOMBIE) {
            newDrops = new ArrayList<>();
            for (ItemStack stack : event.getDrops()) {
                if (stack.getType() == Material.ROTTEN_FLESH) {
                    newDrops.add(new ItemStack(Material.COOKED_BEEF, stack.getAmount() * 2));
                }
            }

        } else if (entityType == EntityType.SQUID) {
            newDrops = new ArrayList<>();
            newDrops.add(new ItemStack(Material.COOKED_FISH, random.nextInt(5) + 1));
        } else if (entityType == EntityType.SKELETON) {
            newDrops = new ArrayList<>();
            for (ItemStack stack : event.getDrops()) {
                if (stack.getType() == Material.ARROW) {
                    newDrops.add(new ItemStack(Material.ARROW, stack.getAmount() * 2));
                }
                if (stack.getType() == Material.BOW) {
                    stack.setDurability((short) 0);
                    newDrops.add(stack);
                }
            }

        } else if (entityType == EntityType.PIG_ZOMBIE) {
            newDrops = new ArrayList<>();
            for (ItemStack stack : event.getDrops()) {
                newDrops.add(new ItemStack(Material.GOLD_INGOT, stack.getAmount() * 2));
            }
        }
        if (newDrops != null) {
            event.getDrops().clear();
            event.getDrops().addAll(newDrops);
        }
        event.setDroppedExp(event.getDroppedExp() * 2);
    }

    @EventHandler
    public void BlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.TNT) {
            event.setCancelled(true);
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
            event.getPlayer().updateInventory();
            Location newLocation = block.getLocation().add(0.5D, 0.0D, 0.5D);
            block.getWorld().spawnEntity(newLocation, EntityType.PRIMED_TNT);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Location location = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY() + 0.3D, block.getLocation().getBlockZ() + 0.5D);

        final Random random = new Random();
        final double percent = random.nextDouble();

        if (percent <= 2 * 0.01 && block.getType() == Material.LEAVES) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.APPLE));
        } else if (block.getType() == Material.STONE) {
            if (block.getData() == 1 || block.getData() == 3 || block.getData() == 5) {
                block.setType(Material.AIR);
                block.getState().update();
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.COBBLESTONE, 1));
            }
        } else if (block.getType() == Material.GRAVEL) {
            block.setType(Material.AIR);
            block.getState().update();
            if (percent <= 80 * 0.01) {
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.ARROW, 3));
            } else {
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.FLINT, 1));
            }
        } else if (block.getType() == Material.IRON_ORE) {
            Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY() + 0.5D, block.getLocation().getBlockZ() + 0.5D);
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT, 2));
            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(3);
            event.setExpToDrop(3);
        } else if (block.getType() == Material.DIAMOND_ORE) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.DIAMOND, 2));
            block.getWorld().spawn(block.getLocation(), ExperienceOrb.class).setExperience(10);
        } else if (block.getType() == Material.GOLD_ORE) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItem(location, new ItemStack(Material.GOLD_INGOT, 2));
            block.getWorld().spawn(block.getLocation(), ExperienceOrb.class).setExperience(4);
        } else if (block.getType() == Material.COAL_ORE) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.TORCH, 3));
            block.getWorld().spawn(location, ExperienceOrb.class).setExperience(2);
        } else if (block.getType() == Material.REDSTONE_ORE) {
            block.getWorld().spawn(location, ExperienceOrb.class).setExperience(3);
        } else if (block.getType() == Material.SAND) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.GLASS, 2));
        } else if (block.getType() == Material.OBSIDIAN) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.OBSIDIAN, 2));
        } else if (block.getType() == Material.CACTUS) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.LOG, 2));
        } else if (block.getType() == Material.SUGAR_CANE || block.getType() == Material.SUGAR_CANE_BLOCK) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.BOOK, 1));
        } else if (block.getType() == Material.DEAD_BUSH) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.STICK, 2));
        }
    }
}
