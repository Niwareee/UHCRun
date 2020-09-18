package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.ItemBuilder;
import fr.niware.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Furnace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameListener implements Listener {

    private final Main main;
    private final SimpleDateFormat simpleDateFormat;

    public GameListener(Main main) {
        this.main = main;
        this.simpleDateFormat = new SimpleDateFormat("mm 'minutes' ss 'secondes'");
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
        System.out.print(event.getCause());
        if (event.getCause() != DamageCause.PROJECTILE) {
            return;
        }

        if (event.getDamager().getType() == EntityType.ENDER_PEARL) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Projectile entity = (Projectile) event.getDamager();
            if (entity.getShooter() instanceof Player) {
                Player damager = (Player) entity.getShooter();
                if (damaged.getHealth() - event.getFinalDamage() > 0) {
                    damager.sendMessage("§dUHCRun §8» §6" + damaged.getName() + " §7est à §c" + getPercent((int) (damaged.getHealth() - event.getFinalDamage()) * 5) + "% §7de sa vie.");
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
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(!State.isInGame() || State.isState(State.TELEPORT));
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        ItemStack itemStack = event.getContents().getIngredient();
        if (itemStack.getType() == Material.GLOWSTONE_DUST) {
            System.out.print("test");
            event.setCancelled(true);
            itemStack.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        Furnace block = (Furnace) event.getBlock().getState();
        new BukkitRunnable() {
            public void run() {
                if (block.getCookTime() > 0 || block.getBurnTime() > 0) {
                    block.setCookTime((short) (block.getCookTime() + 4));
                    block.update();
                } else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(main, 1L, 1L);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }

        if (event.getRightClicked().getType() != EntityType.PLAYER) {
            return;
        }

        Player target = (Player) event.getRightClicked();
        PlayerInventory targetInventory = target.getInventory();
        Inventory inventory = Bukkit.createInventory(null, 54, "Inventaire de " + target.getName());

        inventory.setContents(targetInventory.getContents());

        inventory.setItem(45, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()).setName("§a" + target.getName()).setSkullOwner(target.getName()).setName("§a" + target.getName()).addLoreLine("§eVie: §c" + Math.ceil(target.getHealth() / 2) + " ❤").addLoreLine("§eNourriture: §d" + Math.ceil(target.getFoodLevel()) + "/§d20").addLoreLine("§eNiveau d'xp: §d" + target.getLevel()).toItemStack());

        List<String> lore = new ArrayList<>();
        for (PotionEffect effect : target.getActivePotionEffects()) {
            int duration = effect.getDuration();
            String time = simpleDateFormat.format(duration * 50);
            lore.add("§e" + effect.getType().getName() + " " + effect.getAmplifier() + 1 + " §7(" + time + ")");
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
