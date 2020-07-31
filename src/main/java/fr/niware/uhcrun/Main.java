package fr.lifecraft.uhcrun;

import fr.lifecraft.uhcrun.database.SQLManager;
import fr.lifecraft.uhcrun.game.WinManager;
import fr.lifecraft.uhcrun.listeners.WorldListener;
import fr.lifecraft.uhcrun.register.*;
import fr.lifecraft.uhcrun.player.PlayerManager;
import fr.lifecraft.uhcrun.rank.RankManager;
import fr.lifecraft.uhcrun.structure.StructureLoader;
import fr.lifecraft.uhcrun.utils.Title;
import fr.lifecraft.uhcrun.world.BiomesPatcher;
import fr.lifecraft.uhcrun.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.lifecraft.uhcrun.game.Game;
import fr.lifecraft.uhcrun.hook.SlotPatcher;
import fr.lifecraft.uhcrun.scoreboard.ScoreboardManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends JavaPlugin {

    public static Main instance;

    private Game game;
    private Title title;

    private ScoreboardManager scoreboardManager;
    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;

    private WorldManager worldManager;
    private WinManager winManager;
    private RankManager rankManager;
    private PlayerManager playerManager;
    private StructureLoader structureLoader;

    private SQLManager sqlManager;

    @Override
    public void onLoad() {
        BiomesPatcher.removeBiomes();
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        instance = this;

        scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager();

        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);

        getServer().getScheduler().runTaskLater(this, () -> {
            this.sqlManager = new SQLManager(this);
            this.game = new Game(this);
            PropertiesManager.enablePatch();
            this.structureLoader = new StructureLoader(this);
            this.playerManager = new PlayerManager(this);
            this.worldManager = new WorldManager(this);
            this.winManager = new WinManager(this);
            this.rankManager = new RankManager();

            new RegisterManager(this);
        }, 40);

        this.title = new Title();

        this.log("Plugin successfully enabled in " + (System.currentTimeMillis() - start) + " ms");

        super.onEnable();
    }

    @Override
    public void onDisable() {
        getScoreboardManager().onDisable();
        worldManager.onDisable();
        SlotPatcher.updateServerProperties();

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

    public Title getTitle() {
        return title;
    }

    public void log(String message) {
        getServer().getConsoleSender().sendMessage("[" + getName() + "] " + message);
    }
}