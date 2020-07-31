package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.player.PlayerManager;
import fr.lifecraft.uhcrun.rank.Rank;
import fr.lifecraft.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	private final PlayerManager playerManager;

	public ChatListener(){
		this.playerManager = Main.getInstance().getPlayerManager();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if (!State.isInGame()) {
			Rank rank = playerManager.getPlayers().get(player.getUniqueId()).getRank();
			if (rank.getPower() > 1) {
				event.setFormat(rank.getPrefix() + "%1$s" + "§7: " + rank.getColor() + "%2$s");
			} else {
				event.setFormat("§7" + "%1$s" + "§7: §7" + "%2$s");
			}
		} else {
			String message = event.getMessage();
			if (player.getGameMode() == GameMode.SPECTATOR) {
				event.setCancelled(true);
				Bukkit.getOnlinePlayers().stream().filter(spec -> spec.getGameMode() == GameMode.SPECTATOR).forEach(spec -> spec.sendMessage("§f[Spec] §7" + player.getDisplayName() + " §f» §7" + message));
			} else {
				event.setFormat("§7[§c" + playerManager.getPlayers().get(player.getUniqueId()).getKills() + "§7] §e" + "%1$s" +  "§7: §f" + "%2$s");
			}
		}
	}

}