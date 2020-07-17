package fr.lifecraft.uhcrun.utils;

import fr.lifecraft.uhcrun.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lifecraft.uhcrun.Main;

import java.util.*;

public class Scatter extends BukkitRunnable {

    private final Game game;

    private final List<Block> blocks = new ArrayList<>();
    private final Map<UUID, Location> stayLocs = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    private final int b;
    private boolean j;
    private final boolean start;

    public Scatter(boolean start, int b) {
        this.b = b;
        this.j = true;
        this.start = start;

        this.game = Main.getInstance().getGame();
    }

    public void run() {

        if (this.j) {
            game.getAlivePlayers().forEach(uuid -> this.players.add(Bukkit.getPlayer(uuid)));
            this.j = false;
        }

        if (this.players.size() == 0) {
            game.setRunnable(true);
            new ActionBar("§aTéléportation des joueurs avec succès").sendToAll();
            this.cancel();
            return;
        }

        Random random = new Random();
        Player playerToTp = players.get(random.nextInt(players.size()));

        if (playerToTp != null) {
            if (playerToTp.getGameMode() != GameMode.SPECTATOR) {
                playerToTp.setGameMode(GameMode.SURVIVAL);

                playerToTp.teleport(randomLocation());
                playerToTp.setVelocity(new Vector(0, 2, 0));

                if (this.start) {
                    int pourcent = Math.round((float) 100 / this.players.size());
                    new ActionBar("§7Téléportation...  §6(" + pourcent + "%)").sendToAll();
                    playerToTp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 1, false, false));
                    playerToTp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 9, false, false));
                    setSpawnSpot(playerToTp);
                }
            }
        }
        this.players.remove(playerToTp);
    }

    private Location randomLocation() {
        Random random = new Random();
        int x = (random.nextInt(2) == 0 ? +1 : -1) * random.nextInt(b / 2);
        int z = (random.nextInt(2) == 0 ? +1 : -1) * random.nextInt(b / 2);
        Location location = game.getWorld().getHighestBlockAt(x, z).getLocation();
        location.setY(location.getY() + 35);
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load();
        }
        return location;
    }

    @SuppressWarnings("deprecation")
    private void setSpawnSpot(Player player) {
        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                Block block = player.getLocation().clone().add(x, -6, z).getBlock();
                block.setType(Material.STAINED_GLASS);
                if (x == -3 || x == 2 || z == -3 || z == 2)
                    block.setData((byte) 6);
                blocks.add(block);
            }
        }

        stayLocs.put(player.getUniqueId(), player.getLocation().clone().add(0, -4, 0));

    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public Map<UUID, Location> getStayLocs() {
        return stayLocs;
    }
}
