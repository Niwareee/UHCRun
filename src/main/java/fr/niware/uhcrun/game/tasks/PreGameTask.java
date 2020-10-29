package fr.niware.uhcrun.game.tasks;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.GameManager;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGameTask extends BukkitRunnable {

    private final Game game;
    private final WorldManager worldManager;

    private final boolean forceStart;

    public PreGameTask(UHCRun main, boolean forceStart) {
        this.game = main.getGame();
        this.worldManager = main.getWorldManager();

        this.forceStart = forceStart;
        game.setRunnable(true);
        GameState.setState(GameState.STARTING);

        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 4f));
        Bukkit.broadcastMessage(" \n" + "§dUHCRun §7» §aDémarrage dans §6" + game.getCountdownStart() + " §asecondes." + "\n ");
    }

    @Override
    public void run() {
        if (!game.isRunnable()) {
            return;
        }
        game.removeCountdownStart();

        int countdown = game.getCountdownStart();
        if (countdown > 0) {
            Bukkit.getOnlinePlayers().forEach(players -> players.setLevel(countdown));
            if (countdown < 4) {
                Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 4f, 4f));
            }
            return;
        }

        if (countdown == 0) {
            if (game.getAlivePlayers().size() < game.getAutoStartSize() && !forceStart) {
                game.resetCountdownStart();
                this.cancel();
                return;
            }

            GameManager.get().startTeleport();
            return;
        }

        if (countdown == -5) {
            worldManager.clearAllCustomEntities();
            return;
        }

        if (countdown < -5 && countdown > -9) {
            game.sendToAll("§7» §eDémarrage dans §f" + (countdown + 9) + "s§e.");
            return;
        }

        if (countdown == -9) {
            this.cancel();
            GameManager.get().startGame();
        }
    }
}


