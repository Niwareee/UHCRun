package fr.niware.uhcrun.player.commands;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.PreGameTask;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.utils.structure.StructureLoader;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

    private final UHCRun main;
    private final Game game;
    private final PlayerManager playerManager;
    private final StructureLoader structureLoader;

    public CommandListener(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
        this.playerManager = main.getPlayerManager();
        this.structureLoader = main.getStructureLoader();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (cmd.getName().equalsIgnoreCase("game")) {
            if (args.length == 0) {
                return false;
            }

            switch (args[0]) {
                case "revive":
                    if (args.length != 2) {
                        sender.sendMessage("§cUtilisation: /game revive <Joueur>.");
                        return true;
                    }

                    if (GameState.isInWait()) {
                        return true;
                    }

                    Player target = main.getServer().getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage("§cErreur: Le joueur '§f" + args[1] + "§c' n'est pas connecté.");
                        return true;
                    }

                    if (game.getAlivePlayers().contains(target.getUniqueId())) {
                        sender.sendMessage("§cErreur: Le joueur '" + args[1] + "' est déjà en vie.");
                        return true;
                    }

                    if (game.getDeadPlayers().get(target.getUniqueId()) == null) {
                        sender.sendMessage("§cErreur: Le joueur '" + args[1] + "' n'est pas mort.");
                        return true;
                    }

                    game.getDeadPlayers().get(target.getUniqueId()).revive(target);
                    sender.sendMessage("§dUHCRun §7» §aVous avez ressuscité le joueur §9" + target.getName() + "§a.");
                    return true;

                case "start":
                    if (GameState.isState(GameState.STARTING)) {
                        sender.sendMessage("§cErreur: Le partie est déjà en train de démarrer.");
                        return true;
                    }

                    if (!GameState.isInWait()) {
                        sender.sendMessage("§cErreur: La partie a déjà commencé.");
                        return true;
                    }

                    new PreGameTask(main, true).runTaskTimer(main, 0L, 20L);
                    return true;

                case "checkwin":
                    if (GameState.isInWait()) {
                        sender.sendMessage("§cErreur: La partie n'a pas encore commencé.");
                        return true;
                    }

                    sender.sendMessage("§dLancement du test...");
                    playerManager.checkIsEnd();
                    return true;

                case "savestructure":
                    String[] cos1 = args[2].split(",");
                    String[] cos2 = args[3].split(",");
                    Location location1 = new Location(game.getWorld(), Double.parseDouble(cos1[0]), Double.parseDouble(cos1[1]), Double.parseDouble(cos1[2]));
                    Location location2 = new Location(game.getWorld(), Double.parseDouble(cos2[0]), Double.parseDouble(cos2[1]), Double.parseDouble(cos2[2]));

                    Player player = (Player) sender;
                    Location location = player.getLocation();
                    structureLoader.save(location1, location2, location, args[1]);
                    return true;

                case "pastestructure":
                    structureLoader.load(args[1]);
                    structureLoader.paste(((Player) sender).getLocation(), args[1], true);
                    return true;

                case "togglepvp":
                    if (GameState.isInWait()) {
                        return true;
                    }

                    if (game.getWorld().getPVP()) {
                        game.getWorld().setPVP(false);
                        sender.sendMessage("§dUHCRun §7» §aLe PvP a été §cdésactivée§a.");
                        break;
                    }

                    game.getWorld().setPVP(true);
                    sender.sendMessage("§dUHCRun §7» §aLe PvP a été §eactivée§a.");
                    return true;

                case "version":
                    sender.sendMessage("§aVersion du plugin: §6" + main.getDescription().getVersion());
                    return true;

                default:
                    return false;
            }
            return true;
        }
        return false;
    }
}
