package fr.niware.uhcrun.world;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WorldManager {

    private final Main main;
    private final Game game;

    public static Location SPAWN;
    public static World WORLD;

    private final Scoreboard scoreboard;
    private final List<Entity> entityList;
    private Objective health;

    public WorldManager(Main main) {
        this.main = main;
        this.game = main.getGame();
        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
        this.entityList = new ArrayList<>();

        SPAWN = game.setSpawn(stringToLoc(main.getConfig().getString("world.spawn.location")));
        WORLD = game.setWorld(game.getSpawn().getWorld());
        game.setSpecSpawn(WORLD.getHighestBlockAt(0, 0).getLocation());

        registerTabTeam();
        patchWorlds();
    }

    public void registerObjectives() {
        scoreboard.getObjectives().forEach(Objective::unregister);

        Objective health = scoreboard.registerNewObjective("health", "health");
        health.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        this.health = scoreboard.registerNewObjective("showhealth", "dummy");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setDisplayName("%");

        scoreboard.registerNewObjective("playerkills", "playerKillCount");
    }

    public void registerTabTeam() {
        scoreboard.getTeams().forEach(Team::unregister);

        Team team = scoreboard.registerNewTeam("player");
        team.setPrefix("§7");
        team.setSuffix("§r");
        main.getLogger().info("Tab team successfully created");
    }

    public void onDisable() {
        main.getServer().getWhitelistedPlayers().forEach(player -> player.setWhitelisted(false));

        deleteWorld(new File("world"));
        deleteWorld(new File("world_nether"));
    }

    public Map<String, Integer> getTop10() {
        Objective objective = scoreboard.getObjective("playerkills");

        Map<String, Integer> stats = new HashMap<>();
        scoreboard.getEntries().forEach(playerName -> stats.put(playerName, objective.getScore(playerName).getScore()));

        return stats.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void updateHealth(Player player) {
        if (health == null) return;
        double newPHealth = player.getHealth();
        health.getScore(player.getName()).setScore((int) newPHealth * 5);
    }

    public void clearAllCustomEntities() {
        entityList.forEach(Entity::remove);
        entityList.clear();
    }

    public void deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    public void patchWorlds() {
        for (World world : main.getServer().getWorlds()) {
            world.setDifficulty(Difficulty.NORMAL);
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("sendCommandFeedback", "false");
            world.setGameRuleValue("doDaylightCycle", "false");

            world.setPVP(false);
            world.setTime(6000);
            world.setStorm(false);
            world.setThundering(false);
            world.setKeepSpawnInMemory(true);
            world.setSpawnLocation(SPAWN.getBlockX(), SPAWN.getBlockY(), SPAWN.getBlockZ());

            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setSize(game.getSizeNether() * 2);
            worldBorder.setCenter(0D, 0L);
            worldBorder.setWarningDistance(10);
            worldBorder.setWarningTime(10);
            worldBorder.setDamageAmount(1D);
            worldBorder.setDamageBuffer(1D);
        }

        main.getServer().setSpawnRadius(0);
        main.getServer().getWhitelistedPlayers().clear();
        main.getServer().setWhitelist(false);

        MinecraftServer.getServer().setAllowFlight(true);

        main.log("§aWorlds successfully patch");
        boolean regen = main.getConfig().getBoolean("game.preload");
        main.log("Preload maps is " + (regen ? "§aenabled" : "§cdisabled"));

        if (regen) {
            new WorldLoader(main).generateChunks(WORLD, game.getPreLoad());
        }
    }

    public static Location stringToLoc(String string) {
        String[] args = string.split(", ");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }
}
