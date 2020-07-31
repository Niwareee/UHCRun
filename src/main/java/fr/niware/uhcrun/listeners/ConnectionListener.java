package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.account.AccountManager;
import fr.niware.uhcrun.account.Rank;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.player.PlayerManager;
import fr.niware.uhcrun.game.player.PlayerUHC;
import fr.niware.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final Game game;
    private final AccountManager accountManager;
    private final PlayerManager playerManager;

    public ConnectionListener(Main main) {
        this.game = main.getGame();
        this.accountManager = main.getAccountManager();
        this.playerManager = main.getPlayerManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        int[] account = accountManager.getDatabaseAccount(player.getUniqueId());
        Rank rank = accountManager.getFromPower(account[0]);

        new PlayerUHC(player.getUniqueId(), player.getName(), rank, 0, account[1], account[2]);
        playerManager.onJoin(player, rank);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        playerManager.onQuit(player);
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (State.isState(State.PREGEN)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Bukkit.getMotd());
            return;
        }
        if (game.getAlivePlayers().size() >= game.getSlot()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cLe serveur est plein. (" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");
        }
    }

}
