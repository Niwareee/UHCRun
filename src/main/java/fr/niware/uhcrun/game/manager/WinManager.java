package fr.niware.uhcrun.game.manager;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.utils.WinEffect;
import fr.niware.uhcrun.utils.packet.Title;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class WinManager {

    private final Main main;
    private final PlayerManager playerManager;
    private final Game game;
    private final Title title;

    private int timeSecond = 0;

    public WinManager(Main main) {
        this.main = main;

        this.playerManager = main.getPlayerManager();
        this.game = main.getGame();
        this.title = main.getTitle();
    }

    public void checkWin() {
        if (game.getAlivePlayers().size() <= 1) {
            launchWin(game.getAlivePlayers().get(0));
        }
    }

    public void launchWin(UUID uuid) {
        Location winLocation = game.getSpawn();

        Player winner = Bukkit.getOfflinePlayer(uuid).getPlayer();
        game.setWinner(winner);
        game.setInvincibility(true);

        State.setState(State.FINISH);
        MinecraftServer.getServer().setMotd("§6Fin de la partie");

        Bukkit.getScheduler().runTaskLater(main, () -> {
            main.getStructureLoader().paste(winLocation, "win", true);

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.teleport(winLocation);

                if (players.getUniqueId() != uuid) {
                    players.playSound(players.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                    title.sendTitle(players, 10, 40, 10, "§6UHCRun", "§fVictoire de §a" + winner.getName());
                    continue;
                }

                title.sendTitle(players, 10, 40, 10, "§6Vous avez gagné", "§fVictoire de §a" + winner.getName());
                winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 8.0F, 8.0F);
            }

            game.getWorld().getWorldBorder().setSize(400);
            int killsWinner = playerManager.getPlayers().get(winner.getUniqueId()).getKillsGame();

            Bukkit.broadcastMessage("§f§m+------§6§m-----------§f§m------+");
            Bukkit.broadcastMessage("          §a§l● §ePartie terminée §a§l●");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(" §7Victoire de §a" + winner.getName() + "§7.");
            Bukkit.broadcastMessage(" §7Avec un total de §f" + killsWinner + " §7kills.");
            Bukkit.broadcastMessage(" ");

            Bukkit.broadcastMessage(" §aTop Kills:");

            Map<String, Integer> top10 = main.getWorldManager().getTop10();

            for (int i = 0; i < 3; i++) {
                if (top10.isEmpty()) {
                    Bukkit.broadcastMessage(" §7- §cAucun");
                    break;
                }
                if (top10.size() <= i) break;
                String playerName = (String) top10.keySet().toArray()[i];
                Bukkit.broadcastMessage(" §a#" + (i + 1) + ". §e" + playerName + " §7» §f" + top10.get(playerName) + " kills");
            }


            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§f§m+------§6§m-----------§f§m------+");

            new WinEffect(winner);
            launchStop();

        }, 10);
    }

    public void launchStop() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            timeSecond++;

            if (timeSecond == 10) {
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> main.getAccountManager().sendFinishSQL());
            }

            if (timeSecond == 25) {
                Bukkit.getOnlinePlayers().forEach(players -> playerManager.teleportServer(players, game.getHubServerName()));
            }

            if (timeSecond == 27) main.getServer().shutdown();

        }, 0, 20);
    }
}
