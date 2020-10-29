package fr.niware.uhcrun.player;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DeadPlayer {

    private static final Game game = UHCRun.getInstance().getGame();

    private final UUID uuid;
    private final Location location;
    private final int levelXP;
    private final ItemStack[] armor;
    private final ItemStack[] inventory;
    private final List<PotionEffect> potionEffects;

    public DeadPlayer(UUID uuid, Location location, int levelXP, ItemStack[] armor, ItemStack[] inventory, Collection<PotionEffect> potionEffects) {
        this.uuid = uuid;
        this.location = location;
        this.levelXP = levelXP;
        this.armor = armor;
        this.inventory = inventory;
        this.potionEffects = new ArrayList<>(potionEffects);

        game.getDeadPlayers().put(uuid, this);
    }

    public void revive(Player player) {
        for (Entity entity : location.getChunk().getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(location);
        player.setLevel(levelXP);

        player.getInventory().setArmorContents(armor);
        player.getInventory().setContents(inventory);

        potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 10, false, false));
        potionEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 10, false, false));
        potionEffects.forEach(player::addPotionEffect);

        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 4F, 4F);
        player.sendMessage("§dUHCRun §7» §6Vous avez été ressuscité !");

        game.getAlivePlayers().add(uuid);
        game.getDeadPlayers().remove(uuid);
    }

}