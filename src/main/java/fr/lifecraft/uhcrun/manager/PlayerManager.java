package fr.lifecraft.uhcrun.manager;

import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.player.UHCPlayer;
import fr.lifecraft.uhcrun.rank.Rank;
import fr.lifecraft.uhcrun.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.lifecraft.uhcrun.Main;
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
    private final World world;

    private final ScoreboardManager scoreboardManager;
    private final Scoreboard scoreboard;

    private final Map<UUID, UHCPlayer> players;

    public PlayerManager(Main main) {
        this.main = main;
        this.game = main.getGame();
        this.world = game.getWorld();

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
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void setPlayInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setWalkSpeed(0.2f);
        player.getActivePotionEffects().clear();
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setFireTicks(0);
    }
    
    public void setSpec(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(new Location(world, 0, world.getHighestBlockYAt(0, 0), 0));
    	setPlayInventory(player);
    }

    public void onJoin(Player player) {
        Team team = scoreboard.getTeam(String.valueOf(1));
        team.addEntry(player.getName());
        scoreboardManager.onLogin(player);
    }

    public void onQuit(Player player) {
        players.remove(player.getUniqueId());
        scoreboardManager.onLogout(player);
    }
}
