package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.PlayerManager;
import fr.niware.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final Main main;
    private final Game game;
    private final PlayerManager playerManager;

    public ConnectionListener(Main main) {
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
        if (State.isState(State.PRELOAD)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Bukkit.getMotd());
            return;
        }

        if (game.getAlivePlayers().size() >= game.getSlot()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cLe serveur est plein. (" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");
        }

        playerManager.loadSQLAccount(event.getUniqueId());
    }
}
