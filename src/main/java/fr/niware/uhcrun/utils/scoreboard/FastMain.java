package fr.niware.uhcrun.scoreboard;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.PlayerManager;
import fr.niware.uhcrun.utils.Orientation;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.utils.packet.ActionBar;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastMain {

    private final Game game;
    private PlayerManager playerManager;
    private final Orientation orientation;
    private final ActionBar actionBar;

    private final Location spawn;
    private final Map<UUID, FastBoard> boards;

    public FastMain(Main main) {
        this.game = main.getGame();
        main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> this.playerManager = main.getPlayerManager(), 1L);
        this.orientation = new Orientation();
        this.actionBar = new ActionBar();

        this.spawn = game.getSpecSpawn();
        this.boards = new HashMap<>();

        main.getServer().getScheduler().runTaskTimer(main, () -> {
            System.out.print("dd");
            for (FastBoard board : boards.values()) {
                updateBoard(board, board.getPlayer());
            }
        }, 0, 20);
    }

    private void updateBoard(FastBoard board, Player player) {
        if (State.isInWait()) {
            System.out.print("test");
            board.updateLines(
                    "",
                    " §c» §7Joueurs: §e" + game.getAlivePlayers().size() + "/" + game.getSlot(),
                    " §c» §7Démarrage: §b" + game.getCountdownStart() + "s",
                    "",
                    "§6play.nontia.fr"
            );
            return;
        }

        int kills = playerManager.getUHCPlayer(player.getUniqueId()).getKillsGame();
        String time = secondsToString(game.getTimer());

        if (State.isInGame()) {

            if (State.isState(State.MINING)) {
                actionBar.sendToPlayer(board.getPlayer(), "§6Téléportation: " + secondsToStringColor(game.getPvPTime()));
            }

            board.updateLines(
                    "§7§m+--------------+",
                    " §7» §eJoueurs: §a" + game.getAlivePlayers().size() + "/" + game.getSlot(),
                    " §7» §eBordure: §b" + (int) game.getWorld().getWorldBorder().getSize() / 2,
                    " §7» §eKills: §b" + kills,
                    "§6§9§7§m+--------------+",
                    " §7» §eDurée: §b" + time,
                    " §7» §eCentre: §b" + (int) Math.ceil(player.getLocation().distance(spawn)) + " " + orientation.getOrientation(player),
                    "§9§7§m+--------------+",
                    "§6play.nontia.fr"
            );
            return;
        }

        if (State.isState(State.FINISH)) {
            board.updateLines(
                    "§7" + DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy"),
                    "§5",
                    "§7Gagnant:",
                    "§f» §a" + game.getWinner().getName(),
                    "§8",
                    "§7Kills: §e" + kills,
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
