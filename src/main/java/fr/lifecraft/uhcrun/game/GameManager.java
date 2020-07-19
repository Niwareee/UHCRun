package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.Scatter;
import fr.lifecraft.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameManager {

    private final Main main;
    private final Game game;

    public GameManager() {

        this.main = Main.getInstance();
        this.game = main.getGame();

        Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (State.isState(State.MINING)) {
                int timer = game.getTimer();
                game.addTimer();
                
                if ((timer + 30) == game.getPvPTime() * 60) {
                    Bukkit.broadcastMessage("§dUHCRun §7» §eTéléportation dans §f30 §esecondes.");
                }
                
                if ((timer + 10) == game.getPvPTime() * 60) {
                    Bukkit.broadcastMessage("§dUHCRun §7» §eTéléportation dans §f10 §esecondes.");
                }
                
                if ((timer + 5) == game.getPvPTime() * 60) {
                    Bukkit.broadcastMessage("§dUHCRun §7» §eTéléportation dans §f5 §esecondes.");
                }

                if (timer == game.getPvPTime() * 60) {
                    if (State.isState(State.MINING)) PvP();
                }

            }
        }, 0L, 20L);
    }

    public void PvP() {
        game.setInvincibility(true);
        State.setState(State.TELEPORT);
        game.getWorld().setPVP(true);
    	
        new Scatter(false).runTaskTimer(main, 0L, 5L);

        Bukkit.broadcastMessage("§7§m+--------------------------------+");
        Bukkit.broadcastMessage("              §6● §aPvP §6●");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("  §f» §aPvP activé.");
        Bukkit.broadcastMessage("  §f» §aFinalHeal appliqué.");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("              §6● §aBordure §6●");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(" §f» §aRéduction en cours.");
        Bukkit.broadcastMessage(" §f» §aTaille finale: " + game.getFinalBorderSize());
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7§m+--------------------------------+");
        
        for (UUID uuid : game.getAlivePlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            
            player.setHealth(20D);
            player.playSound(player.getLocation(), Sound.WOLF_GROWL, 2F, 2F);
        }
        
    	border();
        kickOffline();
        
        Bukkit.getScheduler().runTaskLater(main, () -> {
        	game.setInvincibility(false);
        	
        	World world = Bukkit.getWorld("world");
    		world.setGameRuleValue("randomTickSpeed", "0");
    		world.setGameRuleValue("doMobSpawning", "false");
    		
        }, 10 * 20);
    }

    public void border() {
        WorldBorder worldBorder = game.getWorld().getWorldBorder();
        worldBorder.setSize(game.getTPBorder() * 2);
        worldBorder.setSize(game.getFinalBorderSize() * 2, game.getBorderMoveTime() * 60);
    }

    public void kickOffline() {
        List<UUID> uuids = new ArrayList<>(game.getAlivePlayers());

        for (UUID uuid : uuids) {
            if (Bukkit.getPlayer(uuid) == null) {
                game.getAlivePlayers().remove(uuid);
            }
        }

        Bukkit.broadcastMessage("§dUHCRun §7» §cLes joueurs déconnectés ont été éliminés.");
        main.getWinManager().checkWin();
   }

}

