package fr.lifecraft.uhcrun.manager;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.commands.CommandMain;
import fr.lifecraft.uhcrun.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class RegisterManager {

    private final Main main;

    public RegisterManager(Main main){
        this.main = main;

        registerListener();
        registerCommands();

        main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
    }


    public void registerListener() {

        PluginManager pm = Bukkit.getPluginManager();
        List<Listener> listeners = new ArrayList<Listener>();

        listeners.add(new ChatEvent());
        listeners.add(new JoinQuitEvent(main));
        listeners.add(new NoMoveEvent().listener);
        listeners.add(new GameEvents(main));

        for (Listener listener : listeners) {
            pm.registerEvents(listener, main);
        }
    }

    public void registerCommands() {
        main.getCommand("game").setExecutor(new CommandMain(main));
    }
}
