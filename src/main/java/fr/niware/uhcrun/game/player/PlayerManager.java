package fr.niware.uhcrun.player;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.scoreboard.ScoreboardManager;
import fr.niware.uhcrun.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Main main;
    private final Game game;

    private final ScoreboardManager scoreboardManager;
    private final Scoreboard scoreboard;

    private final Map<UUID, UHCPlayer> players;

    public PlayerManager(Main main) {
        this.main = main;
        this.game = main.getGame();

        this.scoreboardManager = main.getScoreboardManager();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        this.players = new HashMap<>();
    }

    public Map<UUID, UHCPlayer> getPlayers() {
        return players;
    }

    public void teleportServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            System.out.print("ERROR: " + e);
        }
        player.sendPluginMessage(main, "BungeeCord", b.toByteArray());
    }

    public void setJoinInventory(Player player) {
        setPlayInventory(player);

        player.teleport(game.getSpawn());
        player.setLevel(game.getCountdownStart());
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void setPlayInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setWalkSpeed(0.2f);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setFireTicks(0);
    }

    public void setSpec(Player player) {
        if (player != null) {
            System.out.print("spectator");
            player.setGameMode(GameMode.SPECTATOR);
            setPlayInventory(player);
        }
    }

    public void onJoin(Player player) {
        Team team = scoreboard.getTeam(String.valueOf(1));
        team.addEntry(player.getName());
        scoreboardManager.onLogin(player);
    }

    public void onQuit(Player player) {
        game.getAlivePlayers().remove(player.getUniqueId());
        scoreboardManager.onLogout(player);

        if (State.isInWait()) players.remove(player.getUniqueId());
    }
}
