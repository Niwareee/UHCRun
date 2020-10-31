package fr.niware.uhcrun.utils.scoreboard;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.player.UHCPlayer;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.utils.Orientation;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastMain {

    private final UHCRun main;
    private final Game game;

    private final Orientation orientation = new Orientation();
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    private Location spawn;
    private PlayerManager playerManager;

    public FastMain(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
    }

    public void launchTask() {
        this.playerManager = main.getPlayerManager();
        this.spawn = game.getSpecSpawn();

        main.getServer().getScheduler().runTaskTimerAsynchronously(main, () -> {
            for (FastBoard board : boards.values()) {
                main.getWorldManager().updateHealth(board.getPlayer());
                updateBoard(board, board.getPlayer());
            }
        }, 0, 5);

        main.log("Scoreboard module successfully load");
        main.log("Main task sucessfully start");
    }

    private void updateBoard(FastBoard board, Player player) {
        if (GameState.isInWait()){
            board.updateLines(
                    "",
                    " §c» §7Joueurs: §e" + game.getAlivePlayers().size() + "/" + game.getSlot(),
                    " §c» §7Démarrage: §b" + game.getCountdownStart() + "s",
                    "",
                    "§6play.nontia.fr"
            );
            return;
        }

        UHCPlayer uhcPlayer = playerManager.getUHCPlayer(player.getUniqueId());
        String time = secondsToString(game.getTimer());

        if (GameState.isInGame()) {
            if (GameState.isState(GameState.MINING)) {
                uhcPlayer.sendActionBar("§6Téléportation: " + secondsToStringColor(game.getPvPTime()));
            }

            Location location = player.getLocation();

            board.updateLines(
                    "§7§m+--------------+",
                    " §7» §eJoueurs: §a" + game.getAlivePlayers().size() + "/" + game.getSlot(),
                    " §7» §eBordure: §b" + (int) game.getWorld().getWorldBorder().getSize() / 2,
                    " §7» §eKills: §b" + uhcPlayer.getKillsGame(),
                    "§6§9§7§m+--------------+",
                    " §7» §eDurée: §b" + time,
                    " §7» §eCentre: §b" + (int) Math.ceil(location.distance(spawn)) + " " + orientation.getOrientation(location.getX(), location.getZ(), location.getYaw()),
                    "§9§7§m+--------------+",
                    "§6play.nontia.fr"
            );
            return;
        }

        if (GameState.isState(GameState.FINISH)) {
            board.updateLines(
                    "§7" + DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy"),
                    "§5",
                    "§7Gagnant:",
                    "§f» §a" + game.getWinner().getName(),
                    "§8",
                    "§7Kills: §e" + uhcPlayer.getKillsGame(),
                    "§7Durée: §e" + time + "s",
                    "§3",
                    "§6play.nontia.fr"
            );
        }
    }

    public void onJoin(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle("§6UHCRun §7(Solo)");
        boards.put(player.getUniqueId(), board);
    }

    public void onQuit(UUID uuid) {
        FastBoard board = boards.remove(uuid);
        if (board != null) {
            board.delete();
        }
    }

    private String secondsToString(int pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }

    private String secondsToStringColor(int pTime) {
        if (pTime / 60 < 1) {
            return String.format("§c%02d:%02d", pTime / 60, pTime % 60);
        }
        return String.format("§b%02d:%02d", pTime / 60, pTime % 60);
    }
}
