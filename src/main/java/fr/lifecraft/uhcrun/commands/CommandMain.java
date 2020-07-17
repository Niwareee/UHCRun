package fr.lifecraft.uhcrun.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.PreGameManager;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.structure.StructureLoader;
import fr.lifecraft.uhcrun.utils.State;
import fr.lifecraft.uhcrun.manager.WorldManager;

public class CommandMain implements CommandExecutor {

    private final Main main;
    private final WinManager winManager;
    private final StructureLoader structureLoader;

    public CommandMain(Main main){
        this.main = main;
        this.winManager = main.getWinManager();
        this.structureLoader = main.getStructureLoader();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("game")) {
                if(args.length == 0){
                    sender.sendMessage("§cUtilisation: /game start");
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
                        if (!main.getGame().isForcestart()) {
                            main.getGame().setForcestart(true);

                            sender.sendMessage("§dUHCRun §8» §aVous avez forcé le démarrage.");
                            Bukkit.broadcastMessage(" ");
                            Bukkit.broadcastMessage("§dUHCRun §8» §aLe démarrage la partie a été forcée.");
                            Bukkit.broadcastMessage(" ");

                            Bukkit.getOnlinePlayers().forEach(all -> all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 4));

                            new PreGameManager();
                        } else {
                            sender.sendMessage("§cErreur: Le redémarrage a déjà été forcé.");
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
