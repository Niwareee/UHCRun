package fr.niware.uhcrun.player.manager;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.database.GameDatabase;
import fr.niware.uhcrun.database.Rank;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.GameManager;
import fr.niware.uhcrun.game.task.PreGameTask;
import fr.niware.uhcrun.player.DeadPlayer;
import fr.niware.uhcrun.player.UHCPlayer;
import fr.niware.uhcrun.utils.scoreboard.FastMain;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.utils.packet.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Main main;

    private final Game game;
    private final FastMain fastMain;
    private final ActionBar actionBar;

    private final Scoreboard scoreboard;
    private final GameManager gameManager;
    private final GameDatabase accountManager;

    private final Map<UUID, UHCPlayer> players;

    public PlayerManager(Main main) {
        this.main = main;

        this.game = main.getGame();
        this.fastMain = main.getFastMain();
        this.actionBar = new ActionBar();

        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
        this.gameManager = new GameManager(main);
        this.accountManager = main.getAccountManager();

        this.players = new HashMap<>();
    }

    public UHCPlayer put(UHCPlayer uhcPlayer) {
        return players.putIfAbsent(uhcPlayer.getUUID(), uhcPlayer);
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        return players.getOrDefault(uuid, put(new UHCPlayer(uuid, game.getPlayerState(), accountManager.getFromPower(0), 0, 0)));
    }

    public Collection<UHCPlayer> getPlayers() {
        return players.values();
    }

    public void teleportServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        player.sendPluginMessage(main, "BungeeCord", b.toByteArray());
    }

    public void setJoinInventory(Player player) {
        setJoinEffect(player);

        player.teleport(game.getSpawn());
        player.setLevel(game.getCountdownStart());
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void setJoinEffect(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setWalkSpeed(0.2f);
        player.getActivePotionEffects().forEach(potionEffects -> player.removePotionEffect(potionEffects.getType()));
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setFireTicks(0);
    }

    public void setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void loadSQLAccount(UUID uuid) {
        int[] account = accountManager.getDatabaseAccount(uuid);
        Rank rank = accountManager.getFromPower(account[0]);
        put(new UHCPlayer(uuid, game.getPlayerState(), rank, account[1], account[2]));
    }

    public void onJoin(Player player) {
        long start = System.currentTimeMillis();
        Rank rank = players.get(player.getUniqueId()).getRank();
        scoreboard.getTeam("player").addEntry(player.getName());
        fastMain.onJoin(player);

        if (GameState.isInWait()) {
            setJoinInventory(player);

            game.getAlivePlayers().add(player.getUniqueId());
            actionBar.sendToPlayer(player, "§a+ " + rank.getPrefix() + player.getName() + " §7a rejoint. §6(" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");

            if (game.getAlivePlayers().size() >= game.getAutoStartSize() && GameState.isState(GameState.WAITING)) {
                new PreGameTask(main, false).runTaskTimer(main, 0L, 20L);
            }

            System.out.println("Successfully load " + player.getName() + "'s settings in " + (System.currentTimeMillis() - start) + " ms");
            return;
        }

        if (GameState.isInGame()) {
            if (game.getDecoPlayers().contains(player.getUniqueId())) {
                Bukkit.broadcastMessage("§dUHCRun §7» §b" + player.getName() + " §7est revenu dans la partie.");

                game.getAlivePlayers().add(player.getUniqueId());
                game.getDecoPlayers().remove(player.getUniqueId());

                if (player.getGameMode() == GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
                return;
            }
            player.teleport(game.getSpecSpawn());
            setSpectator(player);
            return;
        }
        setSpectator(player);
    }

    public void onQuit(Player player) {
        players.remove(player.getUniqueId());
        fastMain.onQuit(player.getUniqueId());

        if (GameState.isInWait()) {
            game.getAlivePlayers().remove(player.getUniqueId());
            actionBar.sendToAll("§c- §e" + player.getName() + " §7a quitté la partie. §6(" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");
            return;
        }

        if (GameState.isInGame()) {
            if (game.getAlivePlayers().contains(player.getUniqueId())) {
                if (GameState.isState(GameState.TELEPORT) || GameState.isState(GameState.MINING)) {
                    game.getAlivePlayers().remove(player.getUniqueId());
                    game.getDecoPlayers().add(player.getUniqueId());
                    Bukkit.broadcastMessage("§dUHCRun §7» §b" + player.getName() + " §7a quitté la partie.");
                    isOnePlayerLeft();
                    return;
                }
                player.setHealth(0D);
            }
        }
    }

    public void onDeath(Player player) {
        new DeadPlayer(player.getUniqueId(), player.getLocation(), player.getLevel(), player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getActivePotionEffects());

        game.getAlivePlayers().remove(player.getUniqueId());

        player.setHealth(20D);
        player.setGameMode(GameMode.SPECTATOR);
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 5.0F, 2.0F);
        player.getWorld().strikeLightningEffect(player.getLocation());
    }

    public void isOnePlayerLeft() {
        if (game.getAlivePlayers().size() == 1) {
            gameManager.endGame(game.getAlivePlayers().get(0));
        }
    }
}
