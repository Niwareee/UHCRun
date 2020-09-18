package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.PlayerManager;
import fr.niware.uhcrun.game.manager.WinManager;
import fr.niware.uhcrun.game.player.DeadPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;

public class DeathListener implements Listener {

    private final Main main;

    private final Game game;
    private final Scoreboard scoreboard;
    private final WinManager winManager;
    private final PlayerManager playerManager;

    public DeathListener(Main main) {
        this.main = main;

        this.game = main.getGame();
        this.playerManager = main.getPlayerManager();
        this.winManager = main.getWinManager();
        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        playerManager.onDeath(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, winManager::checkWin, 10);

        if (player.getKiller() == null) {
            event.setDeathMessage("§dUHCRun §7» §c" + player.getName() + " §6est mort.");
            return;
        }

        playerManager.getUHCPlayer(player.getUniqueId()).setKillsGame(scoreboard.getObjective("playerkills").getScore(player.getName()).getScore());
        game.getDeathPotionEffects().forEach(player.getKiller()::addPotionEffect);
        event.setDeathMessage("§dUHCRun §7» §c" + player.getName() + " §6a été tué par §a" + player.getKiller().getName() + "§6.");

       // event.setDeathMessage("§dUHCRun §7» §c" + player.getName() + " §6" + (player.getKiller() == null ? "est mort." : "a été tué par §a" + player.getKiller().getName() + "§6."));
    }
}
