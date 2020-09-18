package fr.niware.uhcrun.register;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.commands.CommandMain;
import fr.niware.uhcrun.listeners.*;
import fr.niware.uhcrun.world.patch.NMSPatcher;
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

        //main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
    }

    public void registerListener() {

        PluginManager pluginManager = main.getServer().getPluginManager();
        List<Listener> listeners = new ArrayList<>();

        listeners.add(new AutoLapisListener());
        listeners.add(new CraftListener());
        listeners.add(new BlockListener(main));
        listeners.add(new DeathListener(main));
        listeners.add(new ChatListener(main));
        listeners.add(new ConnectionListener(main));
        listeners.add(new TempListener(main).moveListener);
        listeners.add(new TempListener(main).chunkListener);
        listeners.add(new GameListener(main));

        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, main);
        }
    }

    public void registerCommands() {
        main.getCommand("game").setExecutor(new CommandMain(main));
    }
}
