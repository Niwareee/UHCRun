package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.ScatterTask;
import fr.niware.uhcrun.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class StartPvP extends UHCEvent {

    public StartPvP(Game game) {
        super(game);
    }

    @Override
    public void activate() {
        GameState.setState(GameState.PVP);

        game.setInvincibility(true);
        game.getWorld().setPVP(true);

        new ScatterTask(main,false).runTaskTimer(main, 0L, 5L);

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

        for (UHCPlayer uhcPlayer : playerManager.getPlayers()) {
            if (uhcPlayer.getPlayer() == null) continue;
            Player player = uhcPlayer.getPlayer();

            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.setHealth(20D);
            player.setFoodLevel(20);
        }

        // REDUCE BORDER
        new MoveBorder(game).activate();
        // REMOVE OFFLINE PLAYERS FROM GAME
        new KickOffLine(UHCRun.get(), game).activate();
    }
}
