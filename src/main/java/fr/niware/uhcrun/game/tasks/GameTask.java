package fr.niware.uhcrun.game.tasks;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.GameManager;
import fr.niware.uhcrun.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final Game game;

    public GameTask(UHCRun main) {
        this.game = main.getGame();
    }

    @Override
    public void run() {
        if (GameState.isState(GameState.FINISH)) this.cancel();

        game.addTimer();
        game.removePvPTime();
        int pvpTime = game.getPvPTime();

        if (pvpTime == 60 || pvpTime == 30 || pvpTime == 10 || pvpTime == 5 || pvpTime == 4 || pvpTime == 3 || pvpTime == 2 || pvpTime == 1) {
            Bukkit.broadcastMessage("§dUHCRun §7» §eTéléportation dans §f" + pvpTime + " §e" + (pvpTime != 1 ? "secondes." : "seconde."));
            return;
        }

        if (pvpTime == 0) {
            GameManager.get().launchPvP();
            return;
        }

        if (GameState.isState(GameState.PVP)) {
            int timer = game.getTimer();
            if (timer == 1210 || timer == 1215 || timer == 1216 || timer == 1217 || timer == 1218 || timer == 1219) {
                Bukkit.broadcastMessage("§dUHCRun §7» §eDégâts actifs dans §f" + (1220 - timer) + " §e" + (timer != 1219 ? "secondes." : "seconde."));
                return;
            }

            if (timer == 1220) {
                game.setInvincibility(false);

                // AVOID LAGS
                game.getWorld().setGameRuleValue("randomTickSpeed", "0");
                game.getWorld().setGameRuleValue("doMobSpawning", "false");
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.WOLF_GROWL, 2F, 2F));
            }
        }
    }
}

