package fr.niware.uhcrun.game.task;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.utils.Scatter;
import fr.niware.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameTask extends BukkitRunnable {

    private final Main main;
    private final Game game;

    public GameTask(Main main) {
        this.main = main;
        this.game = main.getGame();
    }

    @Override
    public void run() {
        if (State.isState(State.FINISH)) cancel();

        game.addTimer();
        main.getGame().removePvPTime();
        int pvpTime = game.getPvPTime();

        if (pvpTime == 60 || pvpTime == 30 || pvpTime == 10 || pvpTime == 5 || pvpTime == 4 || pvpTime == 3 || pvpTime == 2 || pvpTime == 1) {
            Bukkit.broadcastMessage("§dUHCRun §7» §eTéléportation dans §f" + pvpTime + " §e" + (pvpTime != 1 ? "secondes." : "seconde."));
            return;
        }

        if (pvpTime == 0) {
            launchTeleport();
        }

        if (State.isState(State.PVP)) {
            int timer = game.getTimer();
            if (timer == 1210 || timer == 1215 || timer == 1216 || timer == 1217 || timer == 1218 || timer == 1219) {
                Bukkit.broadcastMessage("§dUHCRun §7» §eDégâts actifs dans §f" + (1220 - timer) + " §e" + (timer != 1219 ? "secondes." : "seconde."));
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

    public void launchTeleport() {
        game.setInvincibility(true);
        State.setState(State.PVP);
        MinecraftServer.getServer().setPVP(true);

        new Scatter(false).runTaskTimer(main, 0L, 5L);

        Bukkit.broadcastMessage("§7§m+---------------------+");
        Bukkit.broadcastMessage("              §6● §eCombats §6●");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("  §f» §aPvP activé.");
        Bukkit.broadcastMessage("  §f» §aFinal heal acivé.");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("              §6● §eBordure §6●");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" §f» §aRéduction en cours.");
        Bukkit.broadcastMessage(" §f» §aTaille finale: §b" + game.getFinalBorderSize() + " §a/ §b-" + game.getFinalBorderSize());
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§7§m+---------------------+");

        for (UUID uuid : game.getAlivePlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.setHealth(20D);
            player.setFoodLevel(20);
        }

        lauchBorder();
        lauchKickOffline();
    }

    public void lauchBorder() {
        WorldBorder worldBorder = game.getWorld().getWorldBorder();
        worldBorder.setSize(game.getTPBorder() * 2);
        worldBorder.setSize(game.getFinalBorderSize() * 2, game.getBorderMoveTime() * 60);
    }

    public void lauchKickOffline() {
        List<UUID> uuids = new ArrayList<>(game.getAlivePlayers());

        uuids.stream().filter(uuid -> Bukkit.getPlayer(uuid) == null).forEach(uuid -> game.getAlivePlayers().remove(uuid));

        Bukkit.broadcastMessage("§dUHCRun §7» §cLes joueurs déconnectés ont été éliminés.");
        main.getWinManager().checkWin();
    }
}

