package fr.lifecraft.uhcrun.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;

import fr.lifecraft.uhcrun.Main;

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
