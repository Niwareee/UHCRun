package fr.lifecraft.uhcrun.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.lifecraft.uhcrun.Main;

@SuppressWarnings("deprecation")
public class BlockListener implements Listener {

	private final Main main;
	private List<ItemStack> dropItems;

	public BlockListener(Main main){
		this.main = main;
		this.dropItems = new ArrayList<>();

	}
	
	@EventHandler
	public void onKill(EntityDeathEvent event) {

		Entity entity = event.getEntity();
        final Random random = new Random();
        final double r = random.nextDouble();

		if (entity.getType() == EntityType.CHICKEN) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.RAW_CHICKEN) {
					drops.setType(Material.COOKED_CHICKEN);
					if(r <= 50 * 0.01) {
						event.getDrops().add(new ItemStack(Material.ARROW));
					}
				}
			}
		}
		else if (entity.getType() == EntityType.SHEEP) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.MUTTON) {
					drops.setType(Material.COOKED_MUTTON);
					if(r <= 50 * 0.01) {
						event.getDrops().add(new ItemStack(Material.STRING));
					}
				}
			}
		}
		else if (entity.getType() == EntityType.RABBIT) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.RABBIT) {
					drops.setType(Material.COOKED_RABBIT);
					if(r <= 50 * 0.01) {
						event.getDrops().add(new ItemStack(Material.LEATHER));
					}
				}
				if (drops.getType() == Material.RABBIT_HIDE) {
					event.getDrops().remove(new ItemStack(Material.RABBIT_HIDE, 1));
				}
			}
		}
		else if (entity.getType() == EntityType.PIG) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.PORK) {
					drops.setType(Material.GRILLED_PORK);
					if(r <= 50 * 0.01) {
						event.getDrops().add(new ItemStack(Material.LEATHER));
					}
				}
			}
		}
		else if (entity.getType() == EntityType.ZOMBIE) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.ROTTEN_FLESH) {
					drops.setType(Material.COOKED_BEEF);
				}
			}
			entity.getWorld().spawn(entity.getLocation(), ExperienceOrb.class).setExperience(3);
			
		}
		else if (entity.getType() == EntityType.HORSE) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.LEATHER) {
					drops.setType(Material.BOOK);
				}
			}
			
		}
		else if (entity.getType() == EntityType.COW || entity.getType() == EntityType.MUSHROOM_COW) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.RAW_BEEF) {
					drops.setType(Material.COOKED_BEEF);
				}
				if (drops.getType() == Material.LEATHER) {
					drops.setType(Material.BOOK);
				}
			}
		}
		else if (entity.getType() == EntityType.PIG_ZOMBIE) {
			event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 1));
		}
		else if (entity.getType() == EntityType.SQUID) {
			dropItems = event.getDrops();
			for (ItemStack drops : dropItems) {
				if (drops.getType() == Material.INK_SACK) {
					drops.setType(Material.COOKED_FISH);
				}
			}		
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
        }
        else if (block.getType() == Material.STONE) {
        	if(block.getData() == 1 || block.getData() == 3 || block.getData() == 5) {
				block.setType(Material.AIR);
				block.getState().update();
				block.getWorld().dropItemNaturally(location, new ItemStack(Material.COBBLESTONE, 1));
        	}
		}
        else if (block.getType() == Material.GRAVEL) {
            block.setType(Material.AIR);
            block.getState().update();
        	if(percent <= 80 * 0.01) {
	            block.getWorld().dropItemNaturally(location, new ItemStack(Material.ARROW, 3));
        	}else {
	            block.getWorld().dropItemNaturally(location, new ItemStack(Material.FLINT, 1));
        	}
        }
        else if (block.getType() == Material.IRON_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(location, new ItemStack(Material.IRON_INGOT, 2));
			block.getWorld().spawn(location, ExperienceOrb.class).setExperience(3);
			event.setExpToDrop(3);
		}
        else if (block.getType() == Material.DIAMOND_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(location, new ItemStack(Material.DIAMOND, 2));
			block.getWorld().spawn(location, ExperienceOrb.class).setExperience(10);
		}
        else if (block.getType() == Material.GOLD_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLD_INGOT, 2));
			block.getWorld().spawn(location, ExperienceOrb.class).setExperience(4);
		} 
        else if (block.getType() == Material.COAL_ORE) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.TORCH, 3));
            block.getWorld().spawn(location, ExperienceOrb.class).setExperience(2);
        }
        else if (block.getType() == Material.REDSTONE_ORE) {
        	block.getWorld().spawn(location, ExperienceOrb.class).setExperience(3);
        }
        else if (block.getType() == Material.SAND) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.GLASS, 2));
        }
        else if (block.getType() == Material.CACTUS) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.LOG, 2));
        }
        else if (block.getType() == Material.SUGAR_CANE || block.getType() == Material.SUGAR_CANE_BLOCK) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.BOOK, 1));
        }
        else if (block.getType() == Material.DEAD_BUSH) {
            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(location, new ItemStack(Material.STICK, 2));
        }
        else if (block.getType() == Material.QUARTZ_ORE){
			event.setExpToDrop(20);
			event.getBlock().breakNaturally(new ItemStack(Material.DIAMOND_PICKAXE));
			event.setCancelled(true);
		}
        else if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
			Location blockLocation = event.getBlock().getLocation();
			final World world = blockLocation.getWorld();
            final int x = location.getBlockX();
            final int y = location.getBlockY();
            final int z = location.getBlockZ();
            if (!validChunk(world, x - 5, y - 5, z - 5, x + 5, y + 5, z + 5)) {
                return;
            }
            Bukkit.getServer().getScheduler().runTask(main, () -> {
                for (int offX = -4; offX <= 4; offX++) {
                    for (int offY = -4; offY <= 4; offY++) {
                        for (int offZ = -4; offZ <= 4; offZ++) {
                            if ((world.getBlockTypeIdAt(x + offX, y + offY, z + offZ) == Material.LEAVES.getId()) || (world.getBlockTypeIdAt(x + offX, y + offY, z + offZ) == Material.LEAVES_2.getId())) {
                                breakLeaf(world, x + offX, y + offY, z + offZ);
                            }
                        }
                    }
                }
            });
            breakTree(event.getBlock(), event.getPlayer());
        }
    }

	public boolean validChunk(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		if ((maxY >= 0) && (minY < world.getMaxHeight())) {
			minX >>= 4;
			minZ >>= 4;
			maxX >>= 4;
			maxZ >>= 4;
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (!world.isChunkLoaded(x, z)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	private void breakLeaf(World world, int x, int y, int z) {
		Block block = world.getBlockAt(x, y, z);

		byte range = 4;
		byte max = 32;
		int[] blocks = new int[max * max * max];
		int off = range + 1;
		int mul = max * max;
		int div = max / 2;
		if (validChunk(world, x - off, y - off, z - off, x + off, y + off, z + off)) {
			for (int offX = -range; offX <= range; offX++) {
				for (int offY = -range; offY <= range; offY++) {
					for (int offZ = -range; offZ <= range; offZ++) {
						int type = world.getBlockTypeIdAt(x + offX, y + offY, z + offZ);
						blocks[((offX + div) * mul + (offY + div) * max + offZ
								+ div)] = ((type == Material.LEAVES.getId()) || (type == Material.LEAVES_2.getId()) ? -2
										: (type == Material.LOG.getId()) || (type == Material.LOG_2.getId()) ? 0 : -1);
					}
				}
			}

			int offX;

			for (offX = 1; offX <= 4; offX++) {
				for (int offY = -range; offY <= range; offY++) {
					for (int offZ = -range; offZ <= range; offZ++) {
						for (int type = -range; type <= range; type++) {
							if (blocks[((offY + div) * mul + (offZ + div) * max + type + div)] == offX - 1) {
								int i = (offY + div - 1) * mul + (offZ + div) * max + type + div;
								if (blocks[i] == -2) {
									blocks[i] = offX;
								}
								if (blocks[((offY + div + 1) * mul + (offZ + div) * max + type + div)] == -2) {
									blocks[((offY + div + 1) * mul + (offZ + div) * max + type + div)] = offX;
								}
								if (blocks[((offY + div) * mul + (offZ + div - 1) * max + type + div)] == -2) {
									blocks[((offY + div) * mul + (offZ + div - 1) * max + type + div)] = offX;
								}
								if (blocks[((offY + div) * mul + (offZ + div + 1) * max + type + div)] == -2) {
									blocks[((offY + div) * mul + (offZ + div + 1) * max + type + div)] = offX;
								}
								if (blocks[((offY + div) * mul + (offZ + div) * max + (type + div - 1))] == -2) {
									blocks[((offY + div) * mul + (offZ + div) * max + (type + div - 1))] = offX;
								}
								if (blocks[((offY + div) * mul + (offZ + div) * max + type + div + 1)] == -2) {
									blocks[((offY + div) * mul + (offZ + div) * max + type + div + 1)] = offX;
								}
							}
						}
					}
				}
			}
		}
		if (blocks[(div * mul + div * max + div)] < 0) {
			LeavesDecayEvent event = new LeavesDecayEvent(block);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}

	public void breakTree(Block log, Player player) {
		if ((log.getType() != Material.LOG) && (log.getType() != Material.LOG_2)) {
			return;
		}
		log.breakNaturally();
		BlockBreakEvent event;
		BlockFace[] arrayOfBlockFace;
		int j = (arrayOfBlockFace = BlockFace.values()).length;
		for (int i = 0; i < j; i++) {
			BlockFace face = arrayOfBlockFace[i];
			if ((log.getRelative(face).getType() == Material.LOG)
					|| (log.getRelative(face).getType() == Material.LOG_2)) {
				event = new BlockBreakEvent(log.getRelative(face), player);
				Bukkit.getServer().getPluginManager().callEvent(event);
				event = new BlockBreakEvent(log.getRelative(face).getRelative(BlockFace.UP), player);
				Bukkit.getServer().getPluginManager().callEvent(event);

			}
		}
		log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, Material.LEAVES);
	}
}
