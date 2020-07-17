package fr.lifecraft.uhcrun.game;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.Scatter;
import fr.lifecraft.uhcrun.utils.State;

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
                game.setTimer(timer + 1);
                
                if ((timer + 30) == game.getPvPTime() * 60) {
                    Bukkit.broadcastMessage("§dUHCRun §8» §7Téléportation dans §e30 §7secondes.");
                }
                
                if ((timer + 10) == game.getPvPTime() * 60) {
                    Bukkit.broadcastMessage("§dUHCRun §8» §7Téléportation dans §e10 §7secondes.");
                }
                
                if ((timer + 5) == game.getPvPTime() * 60) {
                    Bukkit.broadcastMessage("§dUHCRun §8» §7Téléportation dans §e5 §7secondes.");
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
    	
        new Scatter(false, game.getTPBorder() * 2 - 10).runTaskTimer(main, 0L, 2);

        Bukkit.broadcastMessage("§7§m+------------------------------------+");
        Bukkit.broadcastMessage(" §f» §6Le PvP est désormais §aactivé§6.");
        Bukkit.broadcastMessage(" §f» §6Tous les joueurs ont reçu un heal final.");
        Bukkit.broadcastMessage("§7§m+------------------------------------+");
        
        for (UUID uuid : game.getAlivePlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            
            player.setHealth(Bukkit.getPlayer(uuid).getMaxHealth());
            player.playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.WOLF_GROWL, 2F, 2F);
        }
        
    	border();
        
        Bukkit.getScheduler().runTaskLater(main, () -> {
        	game.setInvincibility(false);
        	
        	World world = Bukkit.getWorld("world");
    		world.setGameRuleValue("randomTickSpeed", "0");
    		world.setGameRuleValue("doMobSpawning", "false");
    		
        }, 10 * 20);
    }

    public void border() {
        Bukkit.broadcastMessage("§dUHCRun §8» §7Réduction de la bordure en cours.");
        Bukkit.broadcastMessage("§dUHCRun §8» §7Rapprochez vous du §ecentre §7sous peine de dégats.");
        
        WorldBorder wb = game.getWorld().getWorldBorder();
        wb.setSize(game.getTPBorder() * 2);
        wb.setSize(game.getFinalBorderSize() * 2, game.getBorderMoveTime() * 60);

        kickOffline();
    }

    public void kickOffline() {
        List<UUID> uuids = new ArrayList<>(game.getAlivePlayers());

        for (UUID uuid : uuids) {
            if (Bukkit.getPlayer(uuid) == null) {
                game.getAlivePlayers().remove(uuid);
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> Bukkit.broadcastMessage("§dUHCRun §8» §7Les joueurs déconnectés ont été §céliminés§7."), 2);
        main.getWinManager().checkWin();
   }

}

