package fr.niware.uhcrun.game.manager;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.player.commands.CommandMain;
import fr.niware.uhcrun.player.listeners.*;
import fr.niware.uhcrun.world.listeners.BlockListener;
import fr.niware.uhcrun.world.listeners.ChunkListener;
import fr.niware.uhcrun.world.patch.NMSPatcher;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class RegisterManager {

    private final Main main;

    public RegisterManager(Main main){
        this.main = main;

        registerCommands();
        registerListener();

        NMSPatcher nmsPatcher = new NMSPatcher(main);
        nmsPatcher.patchSlots(main.getGame().getSlot());
        nmsPatcher.patchStrength();
    }

    public void registerListener() {
        PluginManager pluginManager = main.getServer().getPluginManager();
        List<Listener> listeners = new ArrayList<>();

        listeners.add(new AutoLapisListener());
        listeners.add(new ChatListener(main));
        listeners.add(new ConnectionListener(main));
        listeners.add(new DeathListener(main));
        listeners.add(new GameListener(main));
        listeners.add(new MoveListener(main).moveListener);
        listeners.add(new PlayerListener(main));

        listeners.add(new BlockListener(main));
        listeners.add(new ChunkListener().chunkListener);

        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, main);
        }
    }

    public void registerCommands() {
        PluginCommand pluginCommand = main.getCommand("game");
        pluginCommand.setPermission("op");
        pluginCommand.setPermissionMessage("§cErreur: Vous n'avez pas la permission");
        pluginCommand.setDescription("Commande principale de la partie.");
        pluginCommand.setExecutor(new CommandMain(main));
    }
}
