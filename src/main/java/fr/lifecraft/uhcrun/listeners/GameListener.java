package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.utils.ItemBuilder;
import fr.lifecraft.uhcrun.utils.State;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameListener implements Listener {

    private final Main main;
    private final Game game;

    public GameListener(Main main){
        this.main = main;
        this.game = main.getGame();
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(e.toWeatherState());
    }
    
	@EventHandler
	public void onAchiev(PlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(!State.isState(State.FINISH)) {
			if(e.getBlock().getY() >= 130) {
				e.getPlayer().sendMessage("§cErreur: Vous ne pouvez pas poser plus haut.");
				e.setCancelled(true);
			}
		}
	}

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == DamageCause.PROJECTILE) {
            if (e.getEntity() instanceof Player) {
                Player damaged = (Player) e.getEntity();
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
                    if (damaged.getHealth() - e.getFinalDamage() > 0) {
                        damager.sendMessage("§dUHCRun §8» §6" + damaged.getName() + " §7est à §c" + getPercent((int) (damaged.getHealth() - e.getFinalDamage()) * 5) + "% §7de sa vie.");
                    }
                }
            }
        }
    }

    private String getPercent(int s) {

        if (s < 6) return "§4" + s;
        if (s < 16) return "§c" + s;
        if (s < 51) return "§e" + s;

        return "§a" + s;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (main.getGame().isInvincibility() || player.getGameMode() == GameMode.SPECTATOR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHungerMeterChange(FoodLevelChangeEvent event) {
        if (!State.isInGame() || State.isState(State.TELEPORT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (State.isInWait()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Chicken) {
            int i = game.getFeatherRate();
            event.getDrops().add(new ItemStack(Material.FEATHER, i));
        }
        if (event.getEntity() instanceof Spider || event.getEntity() instanceof CaveSpider) {
            int i = game.getStringRate();
            event.getDrops().add(new ItemStack(Material.STRING, i));
        }
    }

   /* @EventHandler
	public void appleRate(LeavesDecayEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.LEAVES) {
			Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY() + 0.5D, block.getLocation().getBlockZ() + 0.5D);
			Random random = new Random();
			double r = random.nextDouble();

			if (r <= 1 * 0.01 && block.getType() == Material.LEAVES) {
				block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
			}
		}
    }*/
   
    @EventHandler
    public void onPortal(PlayerPortalEvent e) {

	    if(!State.isState(State.MINING)) {

	    	Player player = e.getPlayer();
		    player.sendMessage("§cErreur: Le nether est désactivé.");
		    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2F, 2F);
		    
		    e.setCancelled(true);
	    }
    }
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntityType() == EntityType.WITCH || event.getEntityType() == EntityType.GUARDIAN || event.getEntityType() == EntityType.BAT) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1));
		}
	}

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            if (event.getRightClicked().getType() == EntityType.PLAYER) {

                Player target = (Player) event.getRightClicked();
                PlayerInventory targetInventory = target.getInventory();
                Inventory inv = Bukkit.createInventory(null, 54, "Inventaire de " + target.getName());

                for (int i = 0; i < 36; i++) {
                    if (targetInventory.getItem(i) != null) {
                        inv.setItem(i, targetInventory.getItem(i));
                    }
                }

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setOwner(target.getName());

                ItemStack head = new ItemBuilder(skull).setName("§a" + target.getName()).setSkullOwner(target.getName()).setName("§a" + target.getName()).addLoreLine("§eVie: §c" + Math.ceil(target.getHealth() / 2) + " ❤").addLoreLine("§eNourriture: §d" + Math.ceil(target.getFoodLevel()) + "/§d20").addLoreLine("§eNiveau: §d" + target.getLevel()).toItemStack();

                List<String> lore = new ArrayList<>();
                for (PotionEffect effect : target.getActivePotionEffects()) {
                    int duration = effect.getDuration();
                    String time = new SimpleDateFormat("mm:ss").format(duration * 50);
                    lore.add("§e" + effect.getType() + " " + effect.getAmplifier() + 1 + " §7(" + time + " min)");
                }

                ItemStack potionsEffect = new ItemBuilder(Material.BREWING_STAND_ITEM).setName("§6Effets de potions").setLore((lore.isEmpty() ? (Collections.singletonList("§f» §7Aucun")) : lore)).toItemStack();
                inv.setItem(46, potionsEffect);
                ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§f").toItemStack();
                inv.setItem(45, head);

                inv.setItem(36, glass);
                inv.setItem(37, glass);
                inv.setItem(38, glass);
                inv.setItem(39, glass);
                inv.setItem(40, glass);
                inv.setItem(41, glass);
                inv.setItem(42, glass);
                inv.setItem(43, glass);
                inv.setItem(44, glass);
                inv.setItem(48, targetInventory.getHelmet());
                inv.setItem(49, targetInventory.getChestplate());
                inv.setItem(50, targetInventory.getLeggings());
                inv.setItem(51, targetInventory.getBoots());
             
                player.openInventory(inv);
            }
        }
    }
    
	/*@EventHandler
	public void onBeginBreak(BlockDamageEvent event) {
		event.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
		if (event.getBlock().getType() == Material.OBSIDIAN) {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20000, 2, true, true));
		} else {
			event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
		}
	}*/
    
    /*@EventHandler
    public void onPotionFix(EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) e.getDamager();
        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            e.setDamage(e.getDamage() * 0.7);
        }

        if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            if (victim.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                e.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, e.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * 1.2);
            }
        }
    }*/
   
}
