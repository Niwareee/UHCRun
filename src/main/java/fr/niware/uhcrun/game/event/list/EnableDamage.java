package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class EnableDamage extends UHCEvent {

    public EnableDamage(Game game) {
        super(game);
    }

    @Override
    public void activate() {
        Bukkit.broadcastMessage("§dUHCRun §7» §eLes dégâts sont désormais actifs.");
        game.setInvincibility(false);

        // AVOID LAGS
        game.getWorld().setGameRuleValue("randomTickSpeed", "0");
        game.getWorld().setGameRuleValue("doMobSpawning", "false");
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.WOLF_GROWL, 2F, 2F));
    }
}
