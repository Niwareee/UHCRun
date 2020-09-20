package fr.niware.uhcrun.player.listeners;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final UHCRun main;
    private final Game game;
    private final PlayerManager playerManager;

    public ConnectionListener(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
        this.playerManager = main.getPlayerManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        main.getServer().getScheduler().runTaskLater(main, () -> playerManager.onJoin(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        main.getServer().getScheduler().runTaskAsynchronously(main, () -> playerManager.onQuit(event.getPlayer()));
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (GameState.isState(GameState.PRELOAD)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Bukkit.getMotd());
            return;
        }

        if (game.getAlivePlayers().size() >= game.getSlot()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cLe serveur est plein. (" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");
        }

        playerManager.loadSQLAccount(event.getUniqueId());
    }
}
