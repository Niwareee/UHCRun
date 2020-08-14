package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.manager.PlayerManager;
import fr.niware.uhcrun.account.Rank;
import fr.niware.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final PlayerManager playerManager;

    public ChatListener() {
        this.playerManager = Main.getInstance().getPlayerManager();
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!State.isInGame()) {
            Rank rank = playerManager.getUHCPlayer(player.getUniqueId()).getRank();
            if (rank.getPower() != 0) {
                event.setFormat(rank.getPrefix() + "%1$s" + "§7: " + rank.getColor() + "%2$s");
                return;
            }
            event.setFormat("§7" + "%1$s" + "§7: §f" + "%2$s");
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            String message = event.getMessage();
            Bukkit.getOnlinePlayers().stream().filter(spectator -> spectator.getGameMode() == GameMode.SPECTATOR).forEach(spectator -> spectator.sendMessage("§f[Spec] §7" + player.getDisplayName() + " §f» §7" + message));
            return;
        }

        event.setFormat("§7[§c" + playerManager.getUHCPlayer(player.getUniqueId()).getKillsGame() + "§7] §e" + "%1$s" + "§7: §f" + "%2$s");
    }

}
