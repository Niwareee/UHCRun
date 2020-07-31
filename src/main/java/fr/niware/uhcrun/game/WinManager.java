package fr.niware.uhcrun.game;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.player.PlayerManager;
import fr.niware.uhcrun.utils.Colors;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.utils.packet.Title;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Map;
import java.util.Random;
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
        game.setWinner(winner);
        game.setInvincibility(true);

        State.setState(State.FINISH);
        MinecraftServer.getServer().setMotd("§6Fin de la partie");

        Bukkit.getScheduler().runTaskLater(main, () -> {

            main.getStructureLoader().paste(winLocation, "win", true);

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.teleport(winLocation);

                if (players.getUniqueId() != uuid) {
                    players.playSound(players.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                    title.sendTitle(players, 10, 40, 10, "§6UHCRun", "§fVictoire de §a" + winner.getName());
                    continue;
                }

                title.sendTitle(players, 10, 40, 10, "§6Vous avez gagné", "§fVictoire de §a" + winner.getName());
                winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 8.0F, 8.0F);
            }

            game.getWorld().getWorldBorder().setSize(400);
            int kills = playerManager.getPlayers().get(winner.getUniqueId()).getKillsGame();

            Bukkit.broadcastMessage("§f§m+------§6§m-----------§f§m------+");
            Bukkit.broadcastMessage("          §a§l● §ePartie terminée §a§l●");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(" §7Victoire de §a" + winner.getName() + "§7.");
            Bukkit.broadcastMessage(" §7Avec un total de §f" + kills + " §7kills.");
            Bukkit.broadcastMessage(" ");

            Bukkit.broadcastMessage(" §aTop Kills:");

            Map<String, Integer> top10 = main.getWorldManager().getTop10();

            for (int i = 0; i < 3; i++) {
                if (top10.isEmpty()) {
                    Bukkit.broadcastMessage(" §7- §cAucun");
                    break;
                }
                if (top10.size() <= i) break;
                String playerName = (String) top10.keySet().toArray()[i];
                Bukkit.broadcastMessage(" §a#" + (i + 1) + ". §e" + playerName + " §7» §f" + top10.get(playerName) + " kills");
            }


            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§f§m+------§6§m-----------§f§m------+");

            launchWinFireworks();
            launchStop();

        }, 10);
    }

    public void launchWinFireworks() {
        Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (time < 40) {

                Player winner = game.getWinner();
                if (winner == null) return;

                Firework firework = (Firework) winner.getWorld().spawnEntity(winner.getPlayer().getLocation(), EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                Random random = new Random();
                int i = random.nextInt(4) + 1;
                FireworkEffect.Type type = FireworkEffect.Type.BALL;
                if (i == 1) {
                    type = FireworkEffect.Type.BALL;
                }

                if (i == 2) {
                    type = FireworkEffect.Type.BALL_LARGE;
                }

                if (i == 3) {
                    type = FireworkEffect.Type.BURST;
                }

                if (i == 4) {
                    type = FireworkEffect.Type.CREEPER;
                }

                if (i == 5) {
                    type = FireworkEffect.Type.STAR;
                }

                int randint_1 = random.nextInt(15) + 1;
                int randint_2 = random.nextInt(15) + 1;
                Color color_1 = Colors.getColor(randint_1);
                Color color_2 = Colors.getColor(randint_2);
                FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(color_1).withFade(color_2).with(type).trail(random.nextBoolean()).build();
                fireworkMeta.addEffect(effect);
                int power = random.nextInt(2) + 1;
                fireworkMeta.setPower(power);
                firework.setFireworkMeta(fireworkMeta);
                time++;
            }
        }, 0, 5);
    }

    public void launchStop() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            timeSecond++;

            if (timeSecond == 10) {
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> main.getAccountManager().sendFinishSQL());
            }

            if (timeSecond == 25) {
                Bukkit.getOnlinePlayers().forEach(players -> playerManager.teleportServer(players, game.getHubServerName()));
            }

            if (timeSecond == 27) main.getServer().shutdown();

        }, 0, 20);
    }
}
