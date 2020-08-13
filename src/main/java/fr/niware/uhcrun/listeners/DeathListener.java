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

    private final Scoreboard scoreboard;

    private final PlayerManager playerManager;
    private final WinManager winManager;
    private final Game game;

    public DeathListener(Main main) {
        this.main = main;

        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();

        this.playerManager = main.getPlayerManager();
        this.winManager = main.getWinManager();
        this.game = main.getGame();
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        new DeadPlayer(player.getUniqueId(), player.getLocation(), player.getLevel(), player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getActivePotionEffects());
        playerManager.onDeath(player);

        if (player.getKiller() != null) {
            playerManager.getPlayers().get(player.getUniqueId()).setKillsGame(scoreboard.getObjective("pkills").getScore(player.getName()).getScore());
            game.getDeathPotionEffects().forEach(player.getKiller()::addPotionEffect);
        }

        event.setDeathMessage("§dUHCRun §7» §c" + player.getName() + " §6" + (player.getKiller() == null ? "est mort." : "a été tué par §a" + player.getKiller().getName() + "§6."));

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, winManager::checkWin, 10);
    }
}
