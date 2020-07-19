package fr.lifecraft.uhcrun.scoreboard;

import fr.lifecraft.uhcrun.game.Game;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.State;
import fr.lifecraft.uhcrun.world.WorldManager;

import java.util.UUID;

public class PersonalScoreboard {

    private final UUID uuid;
    private final ObjectiveSign objectiveSign;

    private Player player;
    private Main main = Main.getInstance();

    PersonalScoreboard(Player player) {
        this.player = player;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", "Wait");

        reloadData();
        objectiveSign.addReceiver(player);
    }

    public void reloadData() {
    }

    public void setLines(String ip) {
        objectiveSign.setDisplayName("§6UHCRun §7(Solo)");

        Game game = main.getGame();
        String time = secondsToString(game.getTimer());

        WorldManager worldManager = main.getWorldManager();
        worldManager.updateHealth(player);

        if (State.isInWait()) {
            objectiveSign.setLine(0, "§7");
            objectiveSign.setLine(1, " §c» §7Joueurs: §e" + Bukkit.getOnlinePlayers().size() + "/" + game.getSlot());
            objectiveSign.setLine(2, " §c» §7Démarrage: §b" + game.getCountdownStart() + "s");
            objectiveSign.setLine(3, "§6");
            objectiveSign.setLine(4, ip);
            
            objectiveSign.updateLines();
            return;
        }

        int kills = main.getPlayerManager().getPlayers().get(player.getUniqueId()).getKills();

        if (State.isInGame()) {
            objectiveSign.setLine(0, "§7§m+--------------+");
            objectiveSign.setLine(1, " §7» §eJoueurs: §a" + game.getAlivePlayers().size() + "/" + game.getSlot());
            objectiveSign.setLine(2, " §7» §eBordure: §b" + (int) game.getWorld().getWorldBorder().getSize() / 2);
            objectiveSign.setLine(3, " §7» §eKills: §b" + kills);
            objectiveSign.setLine(4, "§6§9§7§m+--------------+");
            objectiveSign.setLine(5, " §7» §eDurée: §b" + time);
            objectiveSign.setLine(6, " §7» §eTP: §b" + (State.isState(State.TELEPORT) ? "✔" : secondsToString(game.getPvPTime())));
            objectiveSign.setLine(7, "§9§7§m+--------------+");
            objectiveSign.setLine(8, ip);
            
            objectiveSign.updateLines();
            return;

        }
        if (State.isState(State.FINISH)) {
            objectiveSign.setLine(0, "§7" + DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy"));
            objectiveSign.setLine(1, "§5");
            objectiveSign.setLine(2, "§7Gagnant:");
            objectiveSign.setLine(3, "§f» §6" + Bukkit.getOfflinePlayer(game.getAlivePlayers().get(0)).getName());
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

    public void onLogout() {
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}