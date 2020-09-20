package fr.niware.uhcrun.world.patch;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.scoreboard.FastReflection;
import fr.niware.uhcrun.world.patch.hook.PotionAttackDamageNerf;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.MobEffectList;
import org.bukkit.Server;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NMSPatcher {

    private final Server server;
    private final Logger logger;

    public NMSPatcher(Main main){
        this.server = main.getServer();
        this.logger = main.getLogger();
    }

    public void patchSlots(int slots) {
        try {
            Method serverGetHandle = server.getClass().getDeclaredMethod("getHandle");
            Object playerList = serverGetHandle.invoke(server);
            Field maxPlayersField = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");

            maxPlayersField.setAccessible(true);
            maxPlayersField.set(playerList, slots);
        } catch (ReflectiveOperationException  e) {
            e.printStackTrace();
        }

        updateServerProperties();
    }

    public void updateServerProperties() {
        Properties properties = new Properties();
        File propertiesFile = new File("server.properties");

        try {
            try (InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            }

            String maxPlayers = Integer.toString(server.getMaxPlayers());

            if (properties.getProperty("max-players").equals(maxPlayers)) {
                return;
            }

            this.logger.info("Saving max players to server.properties...");
            properties.setProperty("max-players", maxPlayers);

            try (OutputStream os = new FileOutputStream(propertiesFile)) {
                properties.store(os, "Minecraft server properties");
            }

        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "An error occurred while updating the server properties", e);
        }
    }

    public void patchStrength() {
        try {
            FastReflection.setFinalStatic(PotionEffectType.class.getDeclaredField("acceptingNew"), true);
            Field byIdField = FastReflection.getField(PotionEffectType.class, true, "byId");
            Field byNameField = FastReflection.getField(PotionEffectType.class, true, "byName");
            ((Map<?, ?>) byNameField.get(null)).remove("increase_damage");
            ((PotionEffectType[]) byIdField.get(null))[5] = null;

            this.logger.info("Patching strength potions (130% => 43.3%, 260% => 86.6%)");
            FastReflection.setFinalStatic(MobEffectList.class.getDeclaredField("INCREASE_DAMAGE"), (new PotionAttackDamageNerf(5, new MinecraftKey("strength"), false, 9643043)).c("potion.damageBoost").a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5D, 2));
            this.logger.info("Strength potions successfully patched.");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
