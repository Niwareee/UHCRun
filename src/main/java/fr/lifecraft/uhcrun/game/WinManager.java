package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.player.PlayerManager;
import fr.lifecraft.uhcrun.utils.State;
import fr.lifecraft.uhcrun.utils.Title;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Map;
import java.util.UUID;

public class WinManager {

    private final Main main;
    private final PlayerManager playerManager;
    private final Game game;
    private final Title title;

    private int time = 0;
    private int timeSecond = 0;

    public WinManager(Main main) {
        this.main = main;

        this.playerManager = main.getPlayerManager();
        this.game = main.getGame();
        this.title = main.getTitle();

    }

    public void checkWin() {
        if (game.getAlivePlayers().size() <= 1) {
            launchWin(game.getAlivePlayers().get(0));
        }
    }

    public void launchWin(UUID uuid) {

        Location winLocation = game.getSpawn();
        Player winner = Bukkit.getOfflinePlayer(uuid).getPlayer();

        State.setState(State.FINISH);
        MinecraftServer.getServer().setMotd("§6Fin de la partie");

        Bukkit.getScheduler().runTaskLater(main, () -> {

            game.getWorld().getWorldBorder().setSize(400);
            main.getStructureLoader().paste(winLocation, "win", true);

            winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 10.0F, 10.0F);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.teleport(winLocation);

                if (players.getUniqueId() == uuid)
                    players.playSound(players.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                title.sendTitle(players, 10, 40, 10, players.getUniqueId() == uuid ? "§6Vous avez gagné" : "§6UHCRun", "§fVictoire de §a" + winner.getName());
            }

            game.setInvincibility(true);
            int kills = playerManager.getPlayers().get(winner.getUniqueId()).getKills();

            Bukkit.broadcastMessage("§f§m+------§c§m---------------§f§m------+");
            Bukkit.broadcastMessage("          §d● §ePartie terminée §d● ");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§6Victoire de §3" + winner.getName() + "§6.");
            Bukkit.broadcastMessage("§6Avec un total de §7" + kills + " §6kills.");
            Bukkit.broadcastMessage(" ");

            Bukkit.broadcastMessage("§aTop Kills:");

            Map<String, Integer> top10 = main.getWorldManager().getTop10();

            for (int i = 0; i < 3; i++) {
                if (top10.size() <= i) break;
                String playerName = (String) top10.keySet().toArray()[i];
                Bukkit.broadcastMessage("§a#" + (i + 1) + ". §e" + playerName + " §7» §f" + top10.get(playerName) + " kills");
            }

            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§f§m+------§c§m---------------§f§m------+");

            launchWinFireworks(uuid);
            launchStop();

        }, 10);
    }

    public void launchWinFireworks(UUID id) {
        Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (time < 40) {

                Player winner = Bukkit.getPlayer(id);
                if(winner == null) return;

                    Firework f = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
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

                time++;
            }
        }, 0, 10);
    }

    public void launchStop() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            timeSecond++;

            if(timeSecond == 25) Bukkit.getOnlinePlayers().forEach(players -> playerManager.teleportServer(players, "uhchub"));

            if(timeSecond == 27) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }, 0, 20);

        //Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> Bukkit.getOnlinePlayers().forEach(players -> playerManager.teleportServer(players, "uhchub")), 0L, 500L);
        //Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"), 27 * 20);
    }
}
