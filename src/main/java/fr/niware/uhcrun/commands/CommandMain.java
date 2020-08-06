package fr.niware.uhcrun.commands;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.PreGameManager;
import fr.niware.uhcrun.game.WinManager;
import fr.niware.uhcrun.structure.StructureLoader;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.world.OrePopulator;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldInitEvent;

import java.lang.reflect.Field;

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
                    case "revive":
                        Player target = Bukkit.getPlayer(args[1]);

                        if (args.length != 2) {
                            sender.sendMessage("§cUtilisation: /game revive <Joueur>");
                            return true;
                        }

                        if (target == null) {
                            sender.sendMessage("§cErreur: Le joueur '§f" + args[1] + "§c' n'est pas connecté.");
                            return true;
                        }

                        if (game.getAlivePlayers().contains(target.getUniqueId())) {
                            sender.sendMessage("§cErreur: Le joueur '" + args[1] + "' est déjà en vie.");
                            return true;
                        }

                        game.getDeadPlayers().get(target.getUniqueId()).revive();
                        break;

                    case "start":
                        if (State.isState(State.STARTING)) {
                            sender.sendMessage("§cErreur: Le partie est déjà en démarrage.");
                            return true;
                        }

                        if (!State.isInWait()) {
                            sender.sendMessage("§cErreur: La partie a déjà commencée");
                            return true;
                        }

                        /*if (Bukkit.getOnlinePlayers().size() < 2) {
                            sender.sendMessage("§cErreur: Vous devez être minimum 2 joueurs.");
                            return true;
                        }*/

                        new PreGameManager(true);
                        break;

                    case "checkwin":
                        if (State.isInWait()) {
                            sender.sendMessage("§cErreur: La partie n'a pas encore commencé.");
                            return true;
                        }

                        sender.sendMessage("§dLancement du test...");
                        winManager.checkWin();
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
                }
            }

        }
        return false;
    }
}
