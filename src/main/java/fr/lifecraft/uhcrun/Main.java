package fr.lifecraft.uhcrun;

import fr.lifecraft.uhcrun.database.SQLManager;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.manager.*;
import fr.lifecraft.uhcrun.structure.Structure;
import fr.lifecraft.uhcrun.structure.StructureLoader;
import fr.lifecraft.uhcrun.world.BiomesPatcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.hook.SlotPatcher;
import fr.lifecraft.uhcrun.scoreboard.ScoreboardManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends JavaPlugin {

    public static Main instance;

    public Game game;

    private ScoreboardManager scoreboardManager;
    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;

    private PropertiesManager propertiesManager;
    private WorldManager worldManager;
    private WinManager winManager;
    private RankManager rankManager;
    private PlayerManager playerManager;
    private StructureLoader structureLoader;

    private SQLManager sqlManager;

    @Override
    public void onLoad() {
        new BiomesPatcher();
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        instance = this;

        scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager();

        this.sqlManager = new SQLManager(this);
        this.game = new Game(this);
        this.structureLoader = new StructureLoader(this);
        this.propertiesManager = new PropertiesManager(this);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.worldManager = new WorldManager(this);
        }, 20);
        this.winManager = new WinManager(this);
        this.rankManager = new RankManager();
        this.playerManager = new PlayerManager(this);

        new RegisterManager(this);

        long stop = System.currentTimeMillis();
        this.log("Plugin successfully enabled in " + (stop - start) + " ms");

        super.onEnable();
    }

    @Override
    public void onDisable() {
        getScoreboardManager().onDisable();
        worldManager.onDisable();
        new SlotPatcher().updateServerProperties();

        super.onDisable();
    }

    public static Main getInstance() {
        return Main.instance;
    }

    public Game getGame() {
        return game;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public WorldManager getWorldManager(){
        return worldManager;
    }

    public WinManager getWinManager() {
        return winManager;
    }

    public SQLManager getSQLManager() {
        return sqlManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public StructureLoader getStructureLoader() {
        return structureLoader;
    }

    public void log(String message) {
        getServer().getConsoleSender().sendMessage("[" + getName() + "] " + message);
    }
}