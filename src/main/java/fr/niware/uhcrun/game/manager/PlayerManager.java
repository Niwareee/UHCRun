package fr.niware.uhcrun.game.manager;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.account.AccountManager;
import fr.niware.uhcrun.account.Rank;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.player.UHCPlayer;
import fr.niware.uhcrun.game.task.PreGameTask;
import fr.niware.uhcrun.utils.scoreboard.FastMain;
import fr.niware.uhcrun.utils.State;
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
    private final ActionBar actionBar;

    private final AccountManager accountManager;
    private final FastMain fastMain;
    private WinManager winManager;

    private final Scoreboard scoreboard;

    private final Map<UUID, UHCPlayer> players;

    public PlayerManager(Main main) {
        this.main = main;
        this.game = main.getGame();
        this.accountManager = main.getAccountManager();
        this.fastMain = main.getFastMain();
        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();

        this.actionBar = new ActionBar();
        this.players = new HashMap<>();

        main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> this.winManager = main.getWinManager(), 1L);
    }

    public UHCPlayer put(UHCPlayer uhcPlayer) {
        return players.putIfAbsent(uhcPlayer.getUUID(), uhcPlayer);
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        return players.getOrDefault(uuid, put(new UHCPlayer(uuid, accountManager.getFromPower(0), 0, 0)));
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
        put(new UHCPlayer(uuid, rank, account[1], account[2]));
    }

    public void onJoin(Player player) {
        long start = System.currentTimeMillis();
        Rank rank = players.get(player.getUniqueId()).getRank();
        scoreboard.getTeam("player").addEntry(player.getName());
        fastMain.onJoin(player);

        if (State.isInWait()) {
            setJoinInventory(player);

            game.getAlivePlayers().add(player.getUniqueId());
            actionBar.sendToPlayer(player, "§a+ " + rank.getPrefix() + player.getName() + " §7a rejoint. §6(" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");

            if (game.getAlivePlayers().size() >= game.getAutoStartSize() && State.isState(State.WAITING)) {
                new PreGameTask(main, false).runTaskTimer(main, 0L, 20L);
            }

            System.out.println("Successfully load " + player.getName() + "'s settings in " + (System.currentTimeMillis() - start) + " ms");
            return;
        }

        if (State.isInGame()) {
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

        if (State.isInWait()) {
            game.getAlivePlayers().remove(player.getUniqueId());
            actionBar.sendToAll("§c- §e" + player.getName() + " §7a quitté la partie. §6(" + game.getAlivePlayers().size() + "/" + game.getSlot() + ")");
            return;
        }

        if (State.isInGame()) {
            if (game.getAlivePlayers().contains(player.getUniqueId())) {
                if (State.isState(State.TELEPORT) || State.isState(State.MINING)) {
                    game.getAlivePlayers().remove(player.getUniqueId());
                    game.getDecoPlayers().add(player.getUniqueId());
                    Bukkit.broadcastMessage("§dUHCRun §7» §b" + player.getName() + " §7a quitté la partie.");
                    winManager.checkWin();
                    return;
                }
                player.setHealth(0D);
            }
        }
    }

    public void onDeath(Player player) {
        player.setHealth(20D);
        setSpectator(player);
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 5.0F, 2.0F);
        player.getWorld().strikeLightningEffect(player.getLocation());

        game.getAlivePlayers().remove(player.getUniqueId());
    }
}
