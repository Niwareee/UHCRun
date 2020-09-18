package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.State;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BlockListener implements Listener {

    private final Main main;
    private final Random random;
    private final List<BlockFace> faces;

    public BlockListener(Main main) {
        this.main = main;
        this.random = new Random();

        this.faces = new ArrayList<>();
        this.faces.add(BlockFace.NORTH);
        this.faces.add(BlockFace.EAST);
        this.faces.add(BlockFace.SOUTH);
        this.faces.add(BlockFace.WEST);
        this.faces.add(BlockFace.UP);
        this.faces.add(BlockFace.DOWN);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
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
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.TNT) {
            block.setType(Material.AIR);
            block.getWorld().spawnEntity(block.getLocation().add(0.5D, 0.0D, 0.5D), EntityType.PRIMED_TNT);
            return;
        }

        if (!State.isState(State.FINISH)) {
            if (block.getY() < 130) {
                return;
            }

            event.getPlayer().sendMessage("§cErreur: Vous ne pouvez pas aller plus haut.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceLava(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }

        for (Entity entity : event.getPlayer().getNearbyEntities(2D, 2D, 2D)) {
            if (entity.getType() != EntityType.PLAYER) {
                return;
            }

            HumanEntity target = (HumanEntity) entity;
            if (target.getGameMode() != GameMode.SURVIVAL) {
                return;
            }

            event.setCancelled(true);
            event.getPlayer().sendMessage("§dUHCRun §7» §cVous êtes trop prêt du joueur §e" + target.getName() + "§c.");
            return;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.LOG || event.getBlock().getType() == Material.LOG_2) {
            main.getServer().getScheduler().runTask(main, () -> removeTree(event.getBlock(), true, 3));
        }
    }

    private void removeTree(Block block, boolean nearWood, int range) {
        if (range < 0)
            return;

        main.getServer().getScheduler().runTask(main, () -> {
            if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
                LeavesDecayEvent event = new LeavesDecayEvent(block);
                main.getServer().getPluginManager().callEvent(event);
                block.breakNaturally();

                if (10 > new Random().nextInt(100))
                    block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.LEAVES);
            }

            if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                for (ItemStack item : block.getDrops())
                    block.getWorld().dropItemNaturally(block.getLocation(), item);

                block.setType(Material.AIR);
            }

            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    for (int x = -1; x <= 1; x++) {
                        Block block1 = block.getRelative(x, y, z);

                        if (block1.getType() == Material.LOG || block1.getType() == Material.LOG_2) {
                            removeTree(block1, nearWood, range - ((z == 0 && x == 0 || nearWood) ? 0 : 1));
                        } else if (block1.getType() == Material.LEAVES || block1.getType() == Material.LEAVES_2) {
                            int finalZ = z;
                            int finalX = x;

                            main.getServer().getScheduler().runTaskAsynchronously(main, () -> {
                                if (!this.isNearWood(block1, 2))
                                    main.getServer().getScheduler().runTask(main, () -> removeTree(block1, false, nearWood ? 4 : (range - ((finalZ == 0 && finalX == 0) ? 0 : 1))));
                            });
                        }
                    }
                }
            }
        });
    }

    public boolean isNearWood(Block block, int range) {
        if (range <= 0)
            return false;

        for (BlockFace face : this.faces) {
            Block block1 = block.getRelative(face);

            if (block1.getType() == Material.LOG || block1.getType() == Material.LOG_2)
                return true;
            else if ((block1.getType() == Material.LEAVES || block1.getType() == Material.LEAVES_2) && this.isNearWood(block1, range - 1))
                return true;
        }

        return false;
    }
}
