package fr.niware.uhcrun.utils;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.utils.packet.ActionBar;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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

    public Scatter(Main main, boolean start) {
        this.game = main.getGame();
        this.actionBar = new ActionBar();

        this.players = new ArrayList<>();
        this.isStart = start;

        game.getAlivePlayers().forEach(uuid -> this.players.add(Bukkit.getPlayer(uuid)));
    }

    public void run() {
        if (this.players.size() == 0) {
            game.setRunnable(true);
            actionBar.sendToAll("§aTéléportation des joueurs avec succès");
            this.cancel();
            return;
        }

        Random random = new Random();
        Player playerToTp = players.get(random.nextInt(players.size()));

        if (playerToTp != null) {
            PaperLib.teleportAsync(playerToTp, randomLocation());
            playerToTp.setVelocity(new Vector(0, 1, 0));

            if (this.isStart) {
                int percent = Math.round((float) 100 / this.players.size());
                actionBar.sendToAll("§7Téléportation en cours...  §6(" + percent + "%)");
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
        Location location = game.getWorld().getHighestBlockAt(x, z).getLocation().add(0, 50, 0);

        int i = 0;

        for (int x1 = -3; x1 < 3; x1++) {
            for (int z1 = -3; z1 < 3; z1++) {
                location.add(x1 * 16, 0, z1 * 16).getChunk().load();
                i++;
                System.out.print("test" + i);
            }
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
