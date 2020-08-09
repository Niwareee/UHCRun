package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.State;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class BlockListener implements Listener {

    private final Random random;
    private final Main main;

    public BlockListener(Main main) {
        this.random = new Random();
        this.main = main;
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
        if (event.getBlock().getType() == Material.TNT) {
            event.setCancelled(true);

            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
            event.getPlayer().updateInventory();

            event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation().add(0.5D, 0.0D, 0.5D), EntityType.PRIMED_TNT);
            return;
        }

        if (!State.isState(State.FINISH)) {
            if (event.getBlock().getY() >= 130) {
                event.getPlayer().sendMessage("§cErreur: Vous ne pouvez pas aller plus haut.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlaceLava(PlayerBucketEmptyEvent event) {
        if (event.getBucket().equals(Material.LAVA_BUCKET)) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.getUniqueId() != event.getPlayer().getUniqueId() && players.getGameMode() == GameMode.SURVIVAL) {
                    if (event.getBlockClicked().getLocation().distance(players.getLocation()) < 5) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§dUHCRun §8» §cVous êtes trop prêt d'un joueur");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            final ArrayList<Block> bList = new ArrayList<>();
            this.checkLeaves(event.getBlock());
            bList.add(event.getBlock());
            new BukkitRunnable() {
                public void run() {
                    for (int i = 0; i < bList.size(); ++i) {
                        Block block = bList.get(i);
                        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {

                            for (ItemStack item : block.getDrops()) {
                                block.getWorld().dropItemNaturally(block.getLocation(), item);
                            }

                            block.setType(Material.AIR);
                            checkLeaves(block);
                        }

                        BlockFace[] var6;
                        int var5 = (var6 = BlockFace.values()).length;

                        for (int var8 = 0; var8 < var5; ++var8) {
                            BlockFace blockFace = var6[var8];
                            if (block.getRelative(blockFace).getType() == Material.LOG || block.getRelative(blockFace).getType() == Material.LOG_2) {
                                bList.add(block.getRelative(blockFace));
                            }
                        }

                        bList.remove(block);
                        if (bList.size() == 0) {
                            this.cancel();
                        }
                    }

                }
            }.runTaskTimer(main, 1L, 1L);
        }

    }

    @SuppressWarnings("deprecation")
    private void breakLeaf(World world, int x, int y, int z) {
        Block block = world.getBlockAt(x, y, z);
        byte data = block.getData();
        if ((data & 4) != 4) {
            byte range = 4;
            byte max = 32;
            int[] blocks = new int[max * max * max];
            int off = range + 1;
            int mul = max * max;
            int div = max / 2;
            if (this.validChunk(world, x - off, y - off, z - off, x + off, y + off, z + off)) {
                int offX;
                int offY;
                int offZ;
                int type;
                for (offX = -range; offX <= range; ++offX) {
                    for (offY = -range; offY <= range; ++offY) {
                        for (offZ = -range; offZ <= range; ++offZ) {
                            Material mat = world.getBlockAt(x + offX, y + offY, z + offZ).getType();

                            blocks[(offX + div) * mul + (offY + div) * max + offZ + div] = mat != Material.LOG && mat != Material.LOG_2 ? (mat != Material.LEAVES && mat != Material.LEAVES_2 ? -1 : -2) : 0;
                        }
                    }
                }

                for (offX = 1; offX <= 4; ++offX) {
                    for (offY = -range; offY <= range; ++offY) {
                        for (offZ = -range; offZ <= range; ++offZ) {
                            for (type = -range; type <= range; ++type) {
                                if (blocks[(offY + div) * mul + (offZ + div) * max + type + div] == offX - 1) {
                                    if (blocks[(offY + div - 1) * mul + (offZ + div) * max + type + div] == -2) {
                                        blocks[(offY + div - 1) * mul + (offZ + div) * max + type + div] = offX;
                                    }

                                    if (blocks[(offY + div + 1) * mul + (offZ + div) * max + type + div] == -2) {
                                        blocks[(offY + div + 1) * mul + (offZ + div) * max + type + div] = offX;
                                    }

                                    if (blocks[(offY + div) * mul + (offZ + div - 1) * max + type + div] == -2) {
                                        blocks[(offY + div) * mul + (offZ + div - 1) * max + type + div] = offX;
                                    }

                                    if (blocks[(offY + div) * mul + (offZ + div + 1) * max + type + div] == -2) {
                                        blocks[(offY + div) * mul + (offZ + div + 1) * max + type + div] = offX;
                                    }

                                    if (blocks[(offY + div) * mul + (offZ + div) * max + (type + div - 1)] == -2) {
                                        blocks[(offY + div) * mul + (offZ + div) * max + (type + div - 1)] = offX;
                                    }

                                    if (blocks[(offY + div) * mul + (offZ + div) * max + type + div + 1] == -2) {
                                        blocks[(offY + div) * mul + (offZ + div) * max + type + div + 1] = offX;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (blocks[div * mul + div * max + div] < 0) {
                LeavesDecayEvent event = new LeavesDecayEvent(block);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                block.breakNaturally();
                if (10 > (new Random()).nextInt(100)) {
                    world.playEffect(block.getLocation(), Effect.STEP_SOUND, Material.LEAVES.getId());
                }
            }

        }
    }

    public boolean validChunk(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (maxY >= 0 && minY < world.getMaxHeight()) {
            minX >>= 4;
            minZ >>= 4;
            maxX >>= 4;
            maxZ >>= 4;

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (!world.isChunkLoaded(x, z)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void checkLeaves(Block block) {
        Location loc = block.getLocation();
        final World world = loc.getWorld();
        final int x = loc.getBlockX();
        final int y = loc.getBlockY();
        final int z = loc.getBlockZ();
        if (this.validChunk(world, x - 5, y - 5, z - 5, x + 5, y + 5, z + 5)) {
            Bukkit.getServer().getScheduler().runTask(main, () -> {
                for (int offX = -4; offX <= 4; ++offX) {
                    for (int offY = -4; offY <= 4; ++offY) {
                        for (int offZ = -4; offZ <= 4; ++offZ) {
                            if (world.getBlockAt(x + offX, y + offY, z + offZ).getType() == Material.LEAVES || world.getBlockAt(x + offX, y + offY, z + offZ).getType() == Material.LEAVES_2) {
                                breakLeaf(world, x + offX, y + offY, z + offZ);
                            }
                        }
                    }
                }

            });
        }
    }
}
