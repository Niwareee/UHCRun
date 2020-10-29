package fr.niware.uhcrun.game.tasks;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class ScatterTask extends BukkitRunnable {

    private final Game game;

    private final List<Player> players;

    private int index;
    private final boolean isStart;

    public ScatterTask(UHCRun main, boolean isStart) {
        this.game = main.getGame();

        this.players = new ArrayList<>();
        this.isStart = isStart;
        this.index = 0;

        game.getAlivePlayers().forEach(uuid -> this.players.add(Bukkit.getPlayer(uuid)));
    }

    public void run() {
        if (this.players.size() == 0) {
            game.setRunnable(true);
            this.cancel();
            if (this.isStart) game.sendToAll("§aTéléportation des joueurs avec succès");
            return;
        }

        Random random = new Random();
        int i = random.nextInt(players.size());
        Player playerToTp = players.get(i);

        if (playerToTp != null) {
            PaperLib.teleportAsync(playerToTp, randomLocation());
            playerToTp.setVelocity(new Vector(0, 1, 0));

            if (this.isStart) {
                int percent = Math.round((float) 100 / this.players.size());
                game.sendToAll("§7Téléportation en cours...  §6(" + percent + "%)");
                setSpawnSpot(playerToTp);
            }
        }

        index++;
        this.players.remove(playerToTp);
    }

    private Location randomLocation() {
        int sizeTP = (this.isStart ? game.getSizeMap() - 10 : game.getSizeTpBorder() - 10);

        double a = index * 2.0D * Math.PI / game.getAlivePlayers().size();
        int x = (int) Math.round(sizeTP / 3.0D * Math.cos(a) + game.getSpawn().getX());
        int z = (int) Math.round(sizeTP / 3.0D * Math.sin(a) + game.getSpawn().getZ());

        System.out.print("Found new location in x: " + x + " z: " + z);
        //Random random = new Random();
        // int x = (random.nextInt(2) == 0 ? +1 : -1) * random.nextInt(sizeTP);
        // int z = (random.nextInt(2) == 0 ? +1 : -1) * random.nextInt(sizeTP);
        // Location location = game.getWorld().getHighestBlockAt(x, z).getLocation().add(0, 50, 0);

        CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(game.getWorld(), x, z);
        Location location = chunk.join().getWorld().getHighestBlockAt(x, z).getLocation().add(0, 50, 0);
        if(!chunk.join().isLoaded()){
            chunk.join().load();
        }
        return location;

    }

    @SuppressWarnings("deprecation")
    private void setSpawnSpot(Player player) {
        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                Block block = player.getLocation().clone().add(x, 0, z).getBlock();
                block.setType(Material.STAINED_GLASS);
                if (x == -3 || x == 2 || z == -3 || z == 2)
                    block.setData((byte) 1);
                game.getBlocks().add(block);
            }
        }
        game.getStayLocs().put(player.getUniqueId(), player.getLocation().clone().add(0, 1, 0));
    }
}
