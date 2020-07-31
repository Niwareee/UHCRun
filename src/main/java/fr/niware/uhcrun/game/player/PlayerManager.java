package fr.niware.uhcrun.game.player;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.account.Rank;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.PreGameManager;
import fr.niware.uhcrun.scoreboard.ScoreboardManager;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.utils.packet.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
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

    private final Map<UUID, PlayerUHC> players;

    public PlayerManager(Main main) {
        this.main = main;
        this.game = main.getGame();

        this.scoreboardManager = main.getScoreboardManager();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        this.players = new HashMap<>();
    }

    public Map<UUID, PlayerUHC> getPlayers() {
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
        }
    }

    public void onJoin(Player player, Rank rank) {
        Team team = scoreboard.getTeam(String.valueOf(1));
        team.addEntry(player.getName());
        scoreboardManager.onLogin(player);

        if (State.isInWait()) {
            setJoinInventory(player);

            new ActionBar("§a+ " + rank.getPrefix() + player.getName() + " §7a rejoint. §6(" + Bukkit.getOnlinePlayers().size() + "/" + game.getSlot() + ")").sendToAll();
            game.getAlivePlayers().add(player.getUniqueId());

            if (Bukkit.getOnlinePlayers().size() >= game.getAutoStartSize() && State.isState(State.WAITING)) {
                new PreGameManager(false);
            }
            return;
        }

        if (State.isInGame()) {
            if (game.getDecoPlayers().contains(player.getUniqueId())) {
                new ActionBar("§dUHCRun §8» §b" + player.getName() + " §7est revenu dans la partie.").sendToAll();

                game.getAlivePlayers().add(player.getUniqueId());
                game.getDecoPlayers().remove(player.getUniqueId());

                if (player.getGameMode() == GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);

                return;
            }
            player.teleport(game.getSpecSpawn());
            setSpec(player);
            return;
        }

        setSpec(player);
    }

    public void onQuit(Player player) {
        game.getAlivePlayers().remove(player.getUniqueId());
        scoreboardManager.onLogout(player);

        if (State.isInWait()) {
            players.remove(player.getUniqueId());
            new ActionBar("§c- §e" + player.getName() + " §7a quitté la partie. §6(" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + game.getSlot() + ")").sendToAll();
            return;
        }

        if (State.isInGame()) {
            if (game.getAlivePlayers().contains(player.getUniqueId())) {
                if (State.isState(State.TELEPORT) || State.isState(State.MINING)) {
                    game.getDecoPlayers().add(player.getUniqueId());
                    new ActionBar("§aUHCRun §8» §b" + player.getName() + " §7a quitté la partie.").sendToAll();
                    return;
                }
                player.setHealth(0);
            }
        }
    }

    public void onDeath(Player player) {
        player.setMaxHealth(20D);
        setSpec(player);
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 5.0F, 2.0F);
        player.getWorld().strikeLightningEffect(player.getLocation());

        game.getAlivePlayers().remove(player.getUniqueId());
    }
}
