package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.WinManager;
import fr.niware.uhcrun.game.player.DeadPlayer;
import fr.niware.uhcrun.game.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

public class DeathListener implements Listener {

    private final Main main;

    private final Scoreboard scoreboard;

    private final PlayerManager playerManager;
    private final WinManager winManager;

    public DeathListener(Main main) {
        this.main = main;

        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();

        this.playerManager = main.getPlayerManager();
        this.winManager = main.getWinManager();
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        player.setHealth(20D);
        new DeadPlayer(player.getUniqueId(), player.getLocation(), player.getLevel(), player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getActivePotionEffects());
        playerManager.onDeath(player);

        if (player.getKiller() != null) {
            playerManager.getPlayers().get(player.getUniqueId()).setKillsGame(scoreboard.getObjective("pkills").getScore(player.getName()).getScore());
            player.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 20, 1));
        }

        event.setDeathMessage("§aUHC §8» §c" + player.getName() + " §7" + (player.getKiller() == null ? "est mort." : "a été tué par §a" + player.getKiller().getName() + "§7."));

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, winManager::checkWin, 10);
    }
}
