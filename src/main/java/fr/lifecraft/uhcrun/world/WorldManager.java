package fr.lifecraft.uhcrun.world;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.world.WorldLoader;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WorldManager {

    private final Main main;
    private final Game game;

    public static World WORLD;
    public static Location SPAWN;

    private final Scoreboard scoreboard;
    private final List<Entity> entityList;

    public WorldManager(Main main) {
        this.main = main;
        this.game = main.getGame();
        this.scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
        this.entityList = new ArrayList<>();

        SPAWN = game.setSpawn(stringToLoc(main.getConfig().getString("world.spawn.location")));
        WORLD = game.setWorld(game.getSpawn().getWorld());

        registerTabTeams();
        createWorlds();
    }

    public void registerObjectives() {
        scoreboard.getObjectives().forEach(Objective::unregister);

        Objective health = scoreboard.registerNewObjective("health", "health");
        health.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        Objective healthBellow = scoreboard.registerNewObjective("showhealth", "dummy");
        healthBellow.setDisplaySlot(DisplaySlot.BELOW_NAME);
        healthBellow.setDisplayName("%");

        scoreboard.registerNewObjective("pkills", "playerKillCount");

    }

    public void registerTabTeams() {
        scoreboard.getTeams().forEach(Team::unregister);

        Team team = scoreboard.registerNewTeam(String.valueOf(1));
        team.setPrefix("§7");
        main.getLogger().info("Tab teams successfully created");
    }

    public void onDisable() {
        main.getServer().getWhitelistedPlayers().forEach(player -> player.setWhitelisted(false));

        deleteWorld(new File("world"));
        deleteWorld(new File("world_nether"));
    }

    public Map<String, Integer> getTop10() {
        Objective objective = scoreboard.getObjective("pkills");

        Map<String, Integer> stats = new HashMap<>();
        scoreboard.getEntries().forEach(playerName -> stats.put(playerName, objective.getScore(playerName).getScore()));

        return stats.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void updateHealth(Player player) {
        Objective showhealth = scoreboard.getObjective("showhealth");
        if (showhealth == null) return;

        double newPHealth = player.getHealth();
        showhealth.getScore(player.getName()).setScore((int) newPHealth * 5);
    }

    public void spawnArmorStand(Location location, String name) {
        ArmorStand as = (ArmorStand) WORLD.spawnEntity(location, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setCustomNameVisible(true);
        as.setCustomName(name);
        as.setGravity(false);
        entityList.add(as);
    }

    public void clearAllCustomEntities() {
        entityList.forEach(Entity::remove);
        entityList.clear();
    }

    public void deleteWorld(File path) {
        System.out.println("Deleting... " + path.getName());

        World world = Bukkit.getWorld(path.getName());
        Bukkit.unloadWorld(world, false);

        try {
            FileUtils.forceDeleteOnExit(path);
            System.out.println("World deleted: " + path.getName());
        } catch (IOException e) {
            System.out.println("Error while deleting world (" + path.getAbsolutePath() + ")\n" + e.getMessage());
        }
    }

    public void createWorlds() {

        for (World world : Bukkit.getWorlds()) {

            world.setDifficulty(Difficulty.NORMAL);
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("sendCommandFeedback", "false");
            world.setGameRuleValue("doDaylightCycle", "false");

            world.setTime(6000);
            world.setPVP(false);
            world.setStorm(false);
            world.setThundering(false);
            world.setSpawnLocation(0, 200, 0);

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

        main.log("§aWorld successfully setup");
        boolean regen = main.getConfig().getBoolean("world.preload.enabled");
        main.log("Preload maps is " + (regen ? "§aenabled" : "§cdisabled"));

        if (regen) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new WorldLoader(WORLD, game.getPreLoad(), game.getPreLoad(), 0),  40, 200);
        }
    }

    public static Location stringToLoc(String string) {
        String[] args = string.split(", ");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }
}
