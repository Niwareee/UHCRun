package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.game.PreGameManager;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.player.PlayerManager;
import fr.lifecraft.uhcrun.rank.RankManager;
import fr.lifecraft.uhcrun.player.UHCPlayer;
import fr.lifecraft.uhcrun.rank.Rank;
import fr.lifecraft.uhcrun.utils.ActionBar;
import fr.lifecraft.uhcrun.utils.State;
import fr.lifecraft.uhcrun.world.WorldLoader;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final Game game;
    private final RankManager rankManager;
    private final PlayerManager playerManager;
    private final WinManager winManager;

    public ConnectionListener(Main main) {
        this.game = main.getGame();
        this.rankManager = main.getRankManager();
        this.playerManager = main.getPlayerManager();
        this.winManager = main.getWinManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage(null);

        Rank rank = rankManager.getDatabaseRank(player.getUniqueId());

        if (rank == null) {
            rankManager.createAccount(player);
            rank = rankManager.ranks.get(0);
        }

        new UHCPlayer(player.getUniqueId(), 0, rank);
        playerManager.onJoin(player);

        if (State.isInWait()) {
            playerManager.setJoinInventory(player);

            new ActionBar("§a+ " + rank.getPrefix() + player.getName() + " §7a rejoint. §6(" + Bukkit.getOnlinePlayers().size() + "/" + game.getSlot() + ")").sendToAll();
            game.getAlivePlayers().add(player.getUniqueId());

            if (Bukkit.getOnlinePlayers().size() >= game.getAutoStart() && State.isState(State.WAITING)) {
                new PreGameManager();
            }

        } else if (State.isInGame()) {
            if (game.getDecoPlayers().contains(player.getUniqueId())) {
                new ActionBar("§dUHCRun §8» §b" + player.getName() + " §7est revenu dans la partie.").sendToAll();

                game.getAlivePlayers().add(player.getUniqueId());
                game.getDecoPlayers().remove(player.getUniqueId());

                if (player.getGameMode() == GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);

            } else {
                playerManager.setSpec(player);
            }

        } else {
            playerManager.setSpec(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(null);

        playerManager.onQuit(player);

        if (State.isInWait()) {
            game.getAlivePlayers().remove(player.getUniqueId());

            new ActionBar("§c- §e" + player.getName() + " §7a quitté la partie. §6(" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + game.getSlot() + ")").sendToAll();
            return;
        }
        if (State.isInGame()) {
            winManager.checkWin();
            if (game.getAlivePlayers().contains(player.getUniqueId())) {
                if (game.getTimer() < game.getBorderTime() * 60) {
                    game.getDecoPlayers().add(player.getUniqueId());
                    new ActionBar("§aUHCRun §8» §b" + player.getName() + " §7a quitté la partie.").sendToAll();
                    return;
                }
                player.setHealth(0);
            }
        }
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        if (State.isState(State.PREGEN)) {
            int percent = (int) ((((float) WorldLoader.loaded) / ((float) WorldLoader.area)) * 100.0);
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "§cRégénération en cours: " + percent + "%");
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (State.isInWait()) {
            Player player = e.getPlayer();
            if (Bukkit.getOnlinePlayers().size() >= game.getSlot() && !player.isOp()) {
                e.disallow(PlayerLoginEvent.Result.KICK_FULL, "§cLe serveur est plein.");
            }
        }
    }

}
