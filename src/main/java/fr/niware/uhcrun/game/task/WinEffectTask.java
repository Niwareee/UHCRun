package fr.niware.uhcrun.game.task;

import fr.niware.uhcrun.utils.ColorsUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class WinEffectTask extends BukkitRunnable {

    private final Player player;
    private int time;

    public WinEffectTask(Player player) {
        this.player = player;
        this.time = 0;
    }

    @Override
    public void run() {
        if (this.time < 30) {
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getPlayer().getLocation(), EntityType.FIREWORK);
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

            this.time++;
        }
    }
}