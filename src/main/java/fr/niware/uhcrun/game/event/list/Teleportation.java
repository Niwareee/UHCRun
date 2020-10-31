package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.ScatterTask;
import fr.niware.uhcrun.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Teleportation extends UHCEvent {

    public Teleportation(UHCRun main, Game game) {
        super(main, game);
    }

    @Override
    public void activate() {
        GameState.setState(GameState.TELEPORT);
        Bukkit.setWhitelist(false);

        game.getWorld().setSpawnLocation(0, game.getSpecSpawn().getBlockY(), 0);
        game.setRunnable(false);
        game.getWorld().getWorldBorder().setSize(game.getSizeMap() * 2);

        for (UHCPlayer uhcPlayer : main.getPlayerManager().getPlayers()) {
            uhcPlayer.sendActionBar("§7Téléportation...");

            Player player = uhcPlayer.getPlayer();
            player.playSound(player.getLocation(), Sound.EAT, 3f, 3f);
            player.getInventory().clear();
            player.setLevel(0);
        }

        new ScatterTask(main, true).runTaskTimer(main, 0L, 10L);
    }
}
