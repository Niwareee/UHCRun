package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.ItemBuilder;
import fr.niware.uhcrun.utils.State;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameListener implements Listener {

    private final Main main;
    private final SimpleDateFormat simpleDateFormat;

    public GameListener(Main main) {
        this.main = main;
        this.simpleDateFormat = new SimpleDateFormat("mm:ss");
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.PROJECTILE) {
            if (event.getEntity() instanceof Player) {
                Player damaged = (Player) event.getEntity();
                if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    Player damager = (Player) ((Projectile) event.getDamager()).getShooter();
                    if (damaged.getHealth() - event.getFinalDamage() > 0) {
                        damager.sendMessage("§dUHCRun §8» §6" + damaged.getName() + " §7est à §c" + getPercent((int) (damaged.getHealth() - event.getFinalDamage()) * 5) + "% §7de sa vie.");
                    }
                }
            }
            return;
        }
        if (event.getDamager().getType() == EntityType.ENDER_PEARL) {
            event.setCancelled(true);

            //event.setDamage(event.getDamage() / 2.5);
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
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(!State.isInGame() || State.isState(State.TELEPORT));
    }

    @EventHandler
    public void onPlaceLava(PlayerBucketEmptyEvent event) {
        if (event.getBucket().equals(Material.LAVA_BUCKET)) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!players.equals(event.getPlayer()) && players.getGameMode() == GameMode.SURVIVAL) {
                    if (event.getBlockClicked().getLocation().distance(players.getLocation()) < 5) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§dUHCRun §8» §cVous êtes trop prêt d'un joueur");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1));
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        if (event.getContents().getIngredient().getType() == Material.GLOWSTONE_DUST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            if (event.getRightClicked().getType() == EntityType.PLAYER) {

                Player target = (Player) event.getRightClicked();
                PlayerInventory targetInventory = target.getInventory();
                Inventory inventory = Bukkit.createInventory(null, 54, "Inventaire de " + target.getName());

                for (int i = 0; i < 36; i++) {
                    if (targetInventory.getItem(i) != null) {
                        inventory.setItem(i, targetInventory.getItem(i));
                    }
                }

                inventory.setItem(45, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()).setName("§a" + target.getName()).setSkullOwner(target.getName()).setName("§a" + target.getName()).addLoreLine("§eVie: §c" + Math.ceil(target.getHealth() / 2) + " ❤").addLoreLine("§eNourriture: §d" + Math.ceil(target.getFoodLevel()) + "/§d20").addLoreLine("§eNiveau: §d" + target.getLevel()).toItemStack());

                List<String> lore = new ArrayList<>();
                for (PotionEffect effect : target.getActivePotionEffects()) {
                    int duration = effect.getDuration();
                    String time = simpleDateFormat.format(duration * 50);
                    lore.add("§e" + effect.getType().getName() + " " + effect.getAmplifier() + 1 + " §7(" + time + " min)");
                }

                inventory.setItem(46, new ItemBuilder(Material.BREWING_STAND_ITEM).setName("§6Effets de potions").setLore((lore.isEmpty() ? (Collections.singletonList("§f» §7Aucun")) : lore)).toItemStack());

                ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack();

                inventory.setItem(36, glass);
                inventory.setItem(37, glass);
                inventory.setItem(38, glass);
                inventory.setItem(39, glass);
                inventory.setItem(40, glass);
                inventory.setItem(41, glass);
                inventory.setItem(42, glass);
                inventory.setItem(43, glass);
                inventory.setItem(44, glass);
                inventory.setItem(48, targetInventory.getHelmet());
                inventory.setItem(49, targetInventory.getChestplate());
                inventory.setItem(50, targetInventory.getLeggings());
                inventory.setItem(51, targetInventory.getBoots());

                player.openInventory(inventory);
            }
        }
    }
}
