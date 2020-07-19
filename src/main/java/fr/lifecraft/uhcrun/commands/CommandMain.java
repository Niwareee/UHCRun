package fr.lifecraft.uhcrun.commands;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.game.PreGameManager;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.structure.StructureLoader;
import fr.lifecraft.uhcrun.utils.State;
import fr.lifecraft.uhcrun.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMain implements CommandExecutor {

    private final Game game;
    private final WinManager winManager;
    private final StructureLoader structureLoader;

    public CommandMain(Main main) {
        this.game = main.getGame();
        this.winManager = main.getWinManager();
        this.structureLoader = main.getStructureLoader();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("game")) {
                if (args.length == 0) {
                    sender.sendMessage("§cUtilisation: /game start.");
                    return true;
                }

                switch (args[0]) {
                    case "savestructure":
                        String[] cos1 = args[2].split(",");
                        String[] cos2 = args[3].split(",");
                        Location location1 = new Location(WorldManager.WORLD, Double.parseDouble(cos1[0]), Double.parseDouble(cos1[1]), Double.parseDouble(cos1[2]));
                        Location location2 = new Location(WorldManager.WORLD, Double.parseDouble(cos2[0]), Double.parseDouble(cos2[1]), Double.parseDouble(cos2[2]));

                        Player player = (Player) sender;
                        Location location = player.getLocation();
                        structureLoader.save(location1, location2, location, args[1]);

                        break;
                    case "pastestructure":
                        structureLoader.load(args[1]);
                        structureLoader.paste(((Player) sender).getLocation(), args[1], true);

                        break;
                    case "checkwin":
                        sender.sendMessage("§dLancement du test . . .");
                        winManager.checkWin();
                        return true;

                    case "start":
                        if (State.isState(State.STARTING)){
                            sender.sendMessage("§cErreur: Le partie est déjà en démarrage.");
                            return true;
                        }

                        if (!State.isInWait()) {
                            sender.sendMessage("§cErreur: La partie a déjà commencée");
                            return true;
                        }

                        if (Bukkit.getOnlinePlayers().size() < 2) {
                            sender.sendMessage("§cErreur: Vous devez être minimum 2 joueurs.");
                            return true;
                        }

                        game.setForceStart(true);
                        sender.sendMessage("§dUHCRun §7» §aVous avez forcé le démarrage de la partie.");
                        new PreGameManager();

                        break;
                }
            }

        }
        return false;
    }
}
