package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.player.PlayerManager;
import org.bukkit.scoreboard.Scoreboard;

public class DeathListener implements Listener {

    private final Main main;

    private final Game game;
    private final Scoreboard scoreboard;

    private final PlayerManager playerManager;
    private final WinManager winManager;

    public DeathListener(Main main) {
        this.main = main;

        this.game = main.getGame();
        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();

        this.playerManager = main.getPlayerManager();
        this.winManager = main.getWinManager();
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {

        Player player = event.getEntity();

        // MODIFICATIONS SUR LE JOUEUR
        player.setMaxHealth(20D);
        playerManager.setSpec(player);
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 5.0F, 2.0F);
        player.getWorld().strikeLightningEffect(player.getLocation());

        game.getAlivePlayers().remove(player.getUniqueId());

        if (player.getKiller() != null) {
            playerManager.getPlayers().get(player.getUniqueId()).setKills(scoreboard.getObjective("pkills").getScore(player.getName()).getScore());
            player.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 20, 1));
        }

        event.setDeathMessage("§aUHC §8» §c" + player.getName() + " §7" + (player.getKiller() == null ? "est mort." : "a été tué par §a" + player.getKiller().getName() + "§7."));

        //scheduleSyncDelayedTask
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, winManager::checkWin, 10);
    }
}
