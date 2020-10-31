package fr.niware.uhcrun.player.listeners;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.UHCPlayer;
import fr.niware.uhcrun.player.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;

public class DeathListener implements Listener {

    private final UHCRun main;
    private final Game game;
    private final PlayerManager playerManager;

    public DeathListener(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
        this.playerManager = main.getPlayerManager();
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        playerManager.onDeath(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, playerManager::checkIsEnd, 10);

        if (player.getKiller() == null) {
            event.setDeathMessage("§dUHCRun §7» §c" + player.getName() + " §6est mort.");
            return;
        }

        Player killer = player.getKiller();
        UHCPlayer uhcKiller = playerManager.getUHCPlayer(killer.getUniqueId());

        uhcKiller.setKillsGame(uhcKiller.getKillsGame() + 1);
        game.getDeathPotionEffects().forEach(killer::addPotionEffect);
        event.setDeathMessage("§dUHCRun §7» §c" + player.getName() + " §6a été tué par §a" + killer.getName() + "§6.");
    }
}
