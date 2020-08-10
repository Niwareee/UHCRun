package fr.niware.uhcrun.utils;

import fr.niware.uhcrun.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public class WinnerFirework {
    private int task;
    private int i = 0;

    public WinnerFirework(final Main plugin, final int loops, final Player player) {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            generateRandomFirework(player.getLocation(), 5, 5);
            if (i >= loops) {
                plugin.getServer().getScheduler().cancelTask(task);
            }
            i++;
        }, 20L, 10L).getTaskId();
    }

    public static void generateRandomFirework(Location location, int heightMin, int heightMax) {
        Random random = new Random();

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();

        int effectsCount = random.nextInt(3) + 1;

        for (int i = 0; i < effectsCount; i++) {
            meta.addEffect(generateRandomFireworkEffect());
        }

        meta.setPower((int) Math.min(Math.floor((heightMin / 5) + random.nextInt(heightMax / 5)), 128D));

        firework.setFireworkMeta(meta);
    }

    private static FireworkEffect generateRandomFireworkEffect() {
        Random rand = new Random();
        Builder fireworkBuilder = FireworkEffect.builder();

        int colorCount = rand.nextInt(3) + 1;
        int trailCount = rand.nextInt(3) + 1;

        fireworkBuilder.flicker(rand.nextInt(3) == 1);
        fireworkBuilder.trail(rand.nextInt(3) == 1);

        for (int i = 0; i < colorCount; i++) {
            fireworkBuilder.withColor(generateRandomColor());
        }

        for (int i = 0; i < trailCount; i++) {
            fireworkBuilder.withFade(generateRandomColor());
        }

        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        fireworkBuilder.with(types[rand.nextInt(types.length)]);

        return fireworkBuilder.build();
    }

    private static Color generateRandomColor() {
        Random rand = new Random();
        return Color.fromBGR(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}