package fr.niware.uhcrun.utils;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.utils.packet.ActionBar;
import io.papermc.lib.PaperLib;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scatter extends BukkitRunnable {

    private final Game game;
    private final ActionBar actionBar;

    private final List<Player> players;

    private final boolean isStart;
    private boolean isAdd;

    public Scatter(boolean start) {
        this.players = new ArrayList<>();

        this.isAdd = true;
        this.isStart = start;

        this.actionBar = new ActionBar();
        this.game = Main.getInstance().getGame();
    }

    public void run() {
        if (this.isAdd) {
            this.players.addAll(Bukkit.getOnlinePlayers());
            this.isAdd = false;
        }

        if (this.players.size() == 0) {
            game.setRunnable(true);
            actionBar.sendToAll("§aTéléportation des joueurs avec succès");
            this.cancel();
            return;
        }

        Random random = new Random();
        Player playerToTp = players.get(random.nextInt(players.size()));

        if (playerToTp != null && playerToTp.getGameMode() != GameMode.SPECTATOR) {

            PaperLib.teleportAsync(playerToTp, randomLocation());
            playerToTp.setVelocity(new Vector(0, 1, 0));
            System.out.print("done");
            //playerToTp.teleport(randomLocation());

            if (this.isStart) {
                int percent = Math.round((float) 100 / this.players.size());
                actionBar.sendToAll("§7Téléportation...  §6(" + percent + "%)");
                playerToTp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1, false, false));
                playerToTp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 9, false, false));
                setSpawnSpot(playerToTp);
            }
        }

        this.players.remove(playerToTp);
    }

    private Location randomLocation() {
        Random random = new Random();
        int sizeTP = (this.isStart ? game.getSizeMap() - 10 : game.getTPBorder() - 10);
        int x = (random.nextInt(2) == 0 ? +1 : -1) * random.nextInt(sizeTP);
        int z = (random.nextInt(2) == 0 ? +1 : -1) * random.nextInt(sizeTP);
        System.out.print("Found new location in x: " + x + " z: " + z);
        Location location = game.getWorld().getHighestBlockAt(x, z).getLocation().add(0, 35,0);
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
                    block.setData((byte) 1);
                game.getBlocks().add(block);
            }
        }
        game.getStayLocs().put(player.getUniqueId(), player.getLocation().clone().add(0, -4, 0));
    }

}
