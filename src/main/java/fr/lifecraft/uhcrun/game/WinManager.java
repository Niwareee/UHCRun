package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.database.SQLManager;
import fr.lifecraft.uhcrun.player.PlayerManager;
import fr.lifecraft.uhcrun.world.WorldManager;
import fr.lifecraft.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class WinManager {

    private final Main main;
    private final WorldManager worldManager;
    private final PlayerManager playerManager;
    private final Game game;
    private final SQLManager sqlManager;

    private int task = 0;

    public WinManager(Main main){
        this.main = main;

        this.worldManager = main.getWorldManager();
        this.playerManager = main.getPlayerManager();
        this.game = main.getGame();
        this.sqlManager = main.getSQLManager();
    }

    private static int time = 0;

    public void checkWin() {
		if (game.getAlivePlayers().size() <= 1) {
            launchWin(game.getAlivePlayers().get(0));
		}
    }
    
    public void launchWin(UUID id) {

        if (!State.isState(State.PVP)) return;

        Location loc = game.getSpawn();
        Player winner = Bukkit.getOfflinePlayer(id).getPlayer();

        winner.sendMessage("§6VICTOIRE: §aVous avez reçu §650 §acoins.");
        State.setState(State.FINISH);
        MinecraftServer.getServer().setMotd("§6Fin de la partie");

        Bukkit.getScheduler().runTaskLater(main, () -> {

        	game.getWorld().getWorldBorder().setSize(400);
        	main.getStructureLoader().paste(loc, "win", true);

        	for (Player pl : Bukkit.getOnlinePlayers()) {
        		pl.teleport(loc);

        		if (pl.getUniqueId() == id) {
        			pl.setPlayerListName("§6Vainqueur " + pl.getName());
                  	pl.sendTitle("§6Vous avez gagné", "§fVictoire de §a" + winner.getName());
                  	pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 10.0F, 10.0F);
        		}else {
                  	pl.sendTitle("", "§fVictoire de §a" + winner.getName());
                  	pl.playSound(pl.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                }
                
        	}
        	
            game.setInvincibility(true);
        	int kills = playerManager.getPlayers().get(winner.getUniqueId()).getKills();
            
            Bukkit.broadcastMessage("§f§m+------§c§m---------------§f§m------+");
            Bukkit.broadcastMessage("          §d✦ §ePartie terminée §d✦ ");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§6Victoire de §3" + winner.getName() + "§6.");
            Bukkit.broadcastMessage("§6Avec un total de §7" + kills + " §6kills.");
            Bukkit.broadcastMessage(" ");

            Bukkit.broadcastMessage("§aTop Kills:");

            Map<String, Integer> top10 = worldManager.getTop10();

            for (int i = 0; i < 3; i++) {
                if (top10.size() <= i) break;
                String playerName = (String) top10.keySet().toArray()[i];
                Bukkit.broadcastMessage("§a#" + (i + 1) + ". §e" + playerName + " §7» §f" + top10.get(playerName) + " kills");
                Player player = Bukkit.getPlayer(playerName);
                player.sendMessage("§aTop Kills: Vous avez reçu §630 §acoins.");
            }

            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§f§m+------§c§m---------------§f§m------+");

            launchWinFireworks();
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
                System.out.print(task);
                Bukkit.getOnlinePlayers().forEach(all -> playerManager.teleportServer(all, "uhchub"));
            }, 0L, 25 * 20L);

            Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"), 27 * 20);
        }, 10);
    }

    public void launchWinFireworks() {
        Bukkit.getScheduler().runTaskTimer(main, () -> {

            if (State.isState(State.FINISH)) {
                if (time < 40) {

                    Bukkit.getOnlinePlayers().stream().filter(op -> op.getGameMode() != GameMode.SPECTATOR).forEach(op -> {
                        Firework f = (Firework) op.getWorld().spawnEntity(op.getLocation(), EntityType.FIREWORK);
                        f.detonate();
                        FireworkMeta fM = f.getFireworkMeta();
                        FireworkEffect effect = FireworkEffect.builder()
                                .flicker(true)
                                .withColor(Color.YELLOW)
                                .withFade(Color.ORANGE)
                                .with(FireworkEffect.Type.STAR)
                                .trail(true)
                                .build();

                        fM.setPower(2);
                        fM.addEffect(effect);
                        f.setFireworkMeta(fM);
                    });

                    time++;
                }
            }
        }, 0, 10);
    }

}
