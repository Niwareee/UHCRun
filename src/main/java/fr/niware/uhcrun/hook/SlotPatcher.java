package fr.niware.uhcrun.hook;

import fr.niware.uhcrun.Main;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;

public class SlotPatcher {

    private static final Main main = Main.getInstance();

    public static void changeSlots(int slots) throws ReflectiveOperationException {
        Method serverGetHandle = main.getServer().getClass().getDeclaredMethod("getHandle");

        Object playerList = serverGetHandle.invoke(main.getServer());
        Field maxPlayersField = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");

        maxPlayersField.setAccessible(true);
        maxPlayersField.set(playerList, slots);

        updateServerProperties();
    }

    public static void updateServerProperties() {
        Properties properties = new Properties();
        File propertiesFile = new File("server.properties");

        try {
            try (InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            }

            String maxPlayers = Integer.toString(main.getServer().getMaxPlayers());

            if (properties.getProperty("max-players").equals(maxPlayers)) {
                return;
            }

            main.getLogger().info("Saving max players to server.properties...");
            properties.setProperty("max-players", maxPlayers);

            try (OutputStream os = new FileOutputStream(propertiesFile)) {
                properties.store(os, "Minecraft server properties");
            }

        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, "An error occurred while updating the server properties", e);
        }
    }
}
