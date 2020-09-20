package fr.niware.uhcrun.game.tasks;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.database.GameDatabase;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.utils.ColorsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EndTask extends BukkitRunnable {

    private final GameDatabase gameDatabase;
    private final PlayerManager playerManager;
    private final Entity player;
    private final Game game;
    private int time;

    public EndTask(UHCRun main, Entity player) {
        this.gameDatabase = main.getAccountManager();
        this.playerManager = main.getPlayerManager();
        this.player = player;
        this.game = main.getGame();
        this.time = 0;
    }

    @Override
    public void run() {
        this.time++;

        if (time < 30) {
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = firework.getFireworkMeta();

            Random random = new Random();
            int randInt = random.nextInt(6) + 1;

            FireworkEffect.Type type = FireworkEffect.Type.BALL;

            if (randInt == 2)
                type = FireworkEffect.Type.BALL_LARGE;
            else if (randInt == 3)
                type = FireworkEffect.Type.BURST;
            else if (randInt == 4)
                type = FireworkEffect.Type.CREEPER;
            else if (randInt == 5)
                type = FireworkEffect.Type.STAR;

            int r1i = random.nextInt(14) + 1;
            int r2i = random.nextInt(14) + 1;

            Color color1 = ColorsUtils.getColor(r1i);
            Color color2 = ColorsUtils.getColor(r2i);

            FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(color1).withFade(color2).with(type).trail(random.nextBoolean()).build();
            fwm.addEffect(effect);

            int rp = random.nextInt(2);
            fwm.setPower(rp);

            firework.setFireworkMeta(fwm);
            return;
        }

        if (time == 40) {
            gameDatabase.sendFinishSQL();
            return;
        }

        if (time == 100) {
            Bukkit.getOnlinePlayers().forEach(players -> playerManager.teleportServer(players, game.getHubServerName()));
            return;
        }

        if (time == 108) Bukkit.shutdown();
    }
}