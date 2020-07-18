package fr.lifecraft.uhcrun.commands;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.game.PreGameManager;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.manager.WorldManager;
import fr.lifecraft.uhcrun.structure.StructureLoader;
import fr.lifecraft.uhcrun.utils.State;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMain implements CommandExecutor {

    private final Game game;
    private final WinManager winManager;
    private final StructureLoader structureLoader;

    public CommandMain(Main main){
        this.game = main.getGame();
        this.winManager = main.getWinManager();
        this.structureLoader = main.getStructureLoader();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("game")) {
                if(args.length == 0){
                    sender.sendMessage("§cUtilisation: /game start.");
                    return true;
                }

                if (args[0].equals("savestructure")) {
                    String[] cos1 = args[2].split(",");
                    String[] cos2 = args[3].split(",");
                    Location location1 = new Location(WorldManager.WORLD, Double.parseDouble(cos1[0]), Double.parseDouble(cos1[1]), Double.parseDouble(cos1[2]));
                    Location location2 = new Location(WorldManager.WORLD, Double.parseDouble(cos2[0]), Double.parseDouble(cos2[1]), Double.parseDouble(cos2[2]));

                    Player player = (Player) sender;
                    Location location = player.getLocation();
                    structureLoader.save(location1, location2, location, args[1]);

                } else if (args[0].equals("pastestructure")) {
                    structureLoader.load(args[1]);
                    structureLoader.paste(((Player) sender).getLocation(), args[1], true);

                } else if (args[0].equals("checkwin")) {
                    sender.sendMessage("§dLancement du test . . .");
                    winManager.checkWin();
                    return true;

                } else if (args[0].equals("start")) {
                    if (State.isState(State.WAITING)) {
                        if (!game.isStarting() && !game.isForcestart()) {
                            game.setForcestart(true);

                            sender.sendMessage("§dUHCRun §7» §aVous avez forcé le démarrage de la partie.");
                            new PreGameManager();

                        } else {
                            sender.sendMessage("§cErreur: Le partie est déjà en démarrage.");
                        }
                    } else {
                        sender.sendMessage("§cErreur: La partie a déjà commencée");
                    }
                }
            }

        }
        return false;
    }
}
