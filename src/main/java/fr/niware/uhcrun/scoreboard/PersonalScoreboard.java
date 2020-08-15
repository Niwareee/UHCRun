package fr.niware.uhcrun.scoreboard;

import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.PlayerOrientation;
import fr.niware.uhcrun.utils.packet.ActionBar;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.niware.uhcrun.utils.State;

import java.util.UUID;

public class PersonalScoreboard {

    private final Main main = Main.getInstance();

    private final Player player;
    private final UUID uuid;
    private final Location spawn;

    private final PlayerOrientation playerOrientation;
    private final ActionBar actionBar;
    private final ObjectiveSign objectiveSign;

    PersonalScoreboard(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.spawn = main.getGame().getSpecSpawn();

        this.playerOrientation = new PlayerOrientation();

        reloadData();
        this.actionBar = new ActionBar();
        this.objectiveSign = new ObjectiveSign("sidebar", "Wait");
        this.objectiveSign.addReceiver(player);
    }

    public void reloadData() {
    }

    public void setLines(String ip) {
        objectiveSign.setDisplayName("§6UHCRun §7(Solo)");

        Game game = main.getGame();
        String time = secondsToString(game.getTimer());

        if (State.isInWait()) {
            objectiveSign.setLine(0, "§7");
            objectiveSign.setLine(1, " §c» §7Joueurs: §e" + game.getAlivePlayers().size() + "/" + game.getSlot());
            objectiveSign.setLine(2, " §c» §7Démarrage: §b" + game.getCountdownStart() + "s");
            objectiveSign.setLine(3, "§6");
            objectiveSign.setLine(4, ip);

            objectiveSign.updateLines();
            return;
        }

        int kills = main.getPlayerManager().getUHCPlayer(player.getUniqueId()).getKillsGame();

        if (State.isInGame()) {
            main.getWorldManager().updateHealth(player);

            if (State.isState(State.MINING))
                actionBar.sendToPlayer(player, "§6Téléportation: " + secondsToStringColor(game.getPvPTime()));

            objectiveSign.setLine(0, "§7§m+--------------+");
            objectiveSign.setLine(1, " §7» §eJoueurs: §a" + game.getAlivePlayers().size() + "/" + game.getSlot());
            objectiveSign.setLine(2, " §7» §eBordure: §b" + (int) game.getWorld().getWorldBorder().getSize() / 2);
            objectiveSign.setLine(3, " §7» §eKills: §b" + kills);
            objectiveSign.setLine(4, "§6§9§7§m+--------------+");
            objectiveSign.setLine(5, " §7» §eDurée: §b" + time);
            objectiveSign.setLine(6, " §7» §eCentre: §b" + (int) Math.ceil(player.getLocation().distance(spawn)) + " " + playerOrientation.getOrientation(player));
            objectiveSign.setLine(7, "§9§7§m+--------------+");
            objectiveSign.setLine(8, ip);

            objectiveSign.updateLines();
            return;
        }

        if (State.isState(State.FINISH)) {
            objectiveSign.setLine(0, "§7" + DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy"));
            objectiveSign.setLine(1, "§5");
            objectiveSign.setLine(2, "§7Gagnant:");
            objectiveSign.setLine(3, "§f» §a" + game.getWinner().getName());
            objectiveSign.setLine(4, "§8");
            objectiveSign.setLine(5, "§7Kills: §e" + kills);
            objectiveSign.setLine(6, "§7Durée: §e" + time + "s");
            objectiveSign.setLine(7, "§3");
            objectiveSign.setLine(8, ip);

            objectiveSign.updateLines();
        }

    }

    private String secondsToString(int pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }

    private String secondsToStringColor(int pTime) {
        if(pTime / 60 < 1){
            return String.format("§c%02d:%02d", pTime / 60, pTime % 60);
        }
        return String.format("§b%02d:%02d", pTime / 60, pTime % 60);
    }

    public void onLogout() {
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}