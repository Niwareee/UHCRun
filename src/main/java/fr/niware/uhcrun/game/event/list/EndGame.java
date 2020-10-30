package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.EndTask;
import fr.niware.uhcrun.player.UHCPlayer;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class EndGame extends UHCEvent {

    private final UHCRun main;
    private final Game game;

    public EndGame(UHCRun main){
        this.main = main;
        this.game = main.getGame();
    }

    public void activate(UUID uuid) {
        Location winLocation = game.getSpawn();

        Player winner = Bukkit.getOfflinePlayer(uuid).getPlayer();
        game.setWinner(winner);
        game.setInvincibility(true);

        GameState.setState(GameState.FINISH);

        Bukkit.getScheduler().runTaskLater(main, () -> {
            main.getStructureLoader().paste(winLocation, "win", true);

            for (UHCPlayer uhcPlayer : main.getPlayerManager().getPlayers()) {
                Player player = uhcPlayer.getPlayer();
                PaperLib.teleportAsync(player, winLocation);

                if (uhcPlayer.getUUID() != uuid) {
                    player.playSound(player.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                    uhcPlayer.sendTitle(10, 40, 10, "§6UHCRun", "§fVictoire de §a" + winner.getName());
                    continue;
                }

                uhcPlayer.sendTitle(10, 40, 10, "§6Vous avez gagné !", "§fVictoire de §a" + winner.getName());
                winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 8.0F, 8.0F);
            }

            game.getWorld().getWorldBorder().setSize(400);
            int killsWinner = main.getPlayerManager().getUHCPlayer(winner.getUniqueId()).getKillsGame();

            Bukkit.broadcastMessage("§f§m+-------§6§m-----------§f§m-------+");
            Bukkit.broadcastMessage("      §a§l● §ePartie terminée §a§l●");
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
            Bukkit.broadcastMessage("§f§m+-------§6§m-----------§f§m-------+");

            new EndTask(main, winner).runTaskTimer(main, 5L, 5L);

        }, 10);
    }

    @Override
    public void activate() {

    }
}
