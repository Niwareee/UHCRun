package fr.niware.uhcrun.register;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.commands.CommandMain;
import fr.niware.uhcrun.listeners.*;
import org.bukkit.Bukkit;
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

        main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
    }


    public void registerListener() {

        PluginManager pluginManager = Bukkit.getPluginManager();
        List<Listener> listeners = new ArrayList<>();

        listeners.add(new ChatListener());
        listeners.add(new ConnectionListener(main));
        listeners.add(new TempListener().moveListener);
        listeners.add(new TempListener().chunkListener);
        listeners.add(new GameListener(main));

        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, main);
        }
    }

    public void registerCommands() {
        main.getCommand("game").setExecutor(new CommandMain(main));
    }
}
