package fr.niware.uhcrun.player.commands;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.tasks.PreGameTask;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.utils.structure.StructureLoader;
import fr.niware.uhcrun.game.state.GameState;
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
                sender.sendMessage("§cUtilisation: /game <start/revive/checkwin/togglepvp>.");
                return true;
            }

            switch (args[0]) {
                case "revive":
                    if (args.length != 2) {
                        sender.sendMessage("§cUtilisation: /game revive <Joueur>.");
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
                        sender.sendMessage("§cErreur: Le joueur '" + args[1] + "' n'a jamais été mort.");
                        return true;
                    }

                    game.getDeadPlayers().get(target.getUniqueId()).revive();
                    sender.sendMessage("§dUHCRun §7» §aVous avez ressuscité le joueur §9" + target.getName() + "§a.");
                    break;

                case "start":
                    if (GameState.isState(GameState.STARTING)) {
                        sender.sendMessage("§cErreur: Le partie est déjà en démarrage.");
                        return true;
                    }

                    if (!GameState.isInWait()) {
                        sender.sendMessage("§cErreur: La partie a déjà commencé.");
                        return true;
                    }

                    new PreGameTask(main, true).runTaskTimer(main, 0L, 20L);
                    break;

                case "checkwin":
                    if (GameState.isInWait()) {
                        sender.sendMessage("§cErreur: La partie n'a pas encore commencé.");
                        return true;
                    }

                    sender.sendMessage("§dLancement du test...");
                    playerManager.isOnePlayerLeft();
                    break;

                case "savestructure":
                    String[] cos1 = args[2].split(",");
                    String[] cos2 = args[3].split(",");
                    Location location1 = new Location(game.getWorld(), Double.parseDouble(cos1[0]), Double.parseDouble(cos1[1]), Double.parseDouble(cos1[2]));
                    Location location2 = new Location(game.getWorld(), Double.parseDouble(cos2[0]), Double.parseDouble(cos2[1]), Double.parseDouble(cos2[2]));

                    Player player = (Player) sender;
                    Location location = player.getLocation();
                    structureLoader.save(location1, location2, location, args[1]);
                    break;

                case "pastestructure":
                    structureLoader.load(args[1]);
                    structureLoader.paste(((Player) sender).getLocation(), args[1], true);
                    break;

                case "togglepvp":
                    if (game.getWorld().getPVP()) {
                        game.getWorld().setPVP(false);
                        sender.sendMessage("§dUHCRun §7» §aLe pvp a été §cdésactivée§a.");
                        break;
                    }

                    game.getWorld().setPVP(true);
                    sender.sendMessage("§dUHCRun §7» §aLe pvp a été §eactivée§a.");
                    break;

                default:
                    return false;
            }
            return false;
        }
        return false;
    }
}
