package fr.lifecraft.uhcrun.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.lifecraft.uhcrun.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager {
    private final Map<UUID, PersonalScoreboard> scoreboards;
    @SuppressWarnings({"rawtypes", "unused"})
    private final ScheduledFuture glowingTask;
    @SuppressWarnings({"rawtypes", "unused"})
    private final ScheduledFuture reloadingTask;
    private int ipCharIndex;
    private int cooldown;

    public ScoreboardManager() {
        scoreboards = new HashMap<>();
        ipCharIndex = 0;
        cooldown = 0;

        glowingTask = Main.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            String ip = colorIpAt();
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getInstance().getExecutorMonoThread().execute(() -> scoreboard.setLines(ip));
        }, 80, 80, TimeUnit.MILLISECONDS);

        reloadingTask = Main.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getInstance().getExecutorMonoThread().execute(scoreboard::reloadData);
        }, 1, 1, TimeUnit.SECONDS);
    }

    private String colorIpAt() {
        String ip = "play.blabla.fr";
 
        if (cooldown > 0) {
            cooldown--;
            return ChatColor.YELLOW + ip;
        }
 
        StringBuilder formattedIp = new StringBuilder();
 
        if (ipCharIndex > 0) {
            formattedIp.append(ip.substring(0, ipCharIndex - 1));
            formattedIp.append(ChatColor.GOLD).append(ip.substring(ipCharIndex - 1, ipCharIndex));
        } else {
            formattedIp.append(ip.substring(0, ipCharIndex));
        }
 
        formattedIp.append(ChatColor.RED).append(ip.charAt(ipCharIndex));
 
        if (ipCharIndex + 1 < ip.length()) {
            formattedIp.append(ChatColor.GOLD).append(ip.charAt(ipCharIndex + 1));
 
            if (ipCharIndex + 2 < ip.length())
                formattedIp.append(ChatColor.YELLOW).append(ip.substring(ipCharIndex + 2));
 
            ipCharIndex++;
        } else {
            ipCharIndex = 0;
            cooldown = 50;
        }
 
        return ChatColor.YELLOW + formattedIp.toString();
    }

    public void onDisable() {
        scoreboards.values().forEach(PersonalScoreboard::onLogout);
    }

    public void onLogin(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            return;
        }
        scoreboards.put(player.getUniqueId(), new PersonalScoreboard(player));
    }

    public void onLogout(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            scoreboards.get(player.getUniqueId()).onLogout();
            scoreboards.remove(player.getUniqueId());
        }
    }

    public void update(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            scoreboards.get(player.getUniqueId()).reloadData();
        }
    }

}