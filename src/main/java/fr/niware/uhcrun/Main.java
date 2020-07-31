package fr.niware.uhcrun;

import fr.niware.uhcrun.database.SQLManager;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.WinManager;
import fr.niware.uhcrun.hook.SlotPatcher;
import fr.niware.uhcrun.listeners.WorldInitListener;
import fr.niware.uhcrun.game.player.PlayerManager;
import fr.niware.uhcrun.account.AccountManager;
import fr.niware.uhcrun.register.PropertiesManager;
import fr.niware.uhcrun.register.RegisterManager;
import fr.niware.uhcrun.scoreboard.ScoreboardManager;
import fr.niware.uhcrun.structure.StructureLoader;
import fr.niware.uhcrun.utils.packet.Title;
import fr.niware.uhcrun.world.BiomesPatcher;
import fr.niware.uhcrun.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

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
    private AccountManager accountManager;
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

        this.getServer().getPluginManager().registerEvents(new WorldInitListener(), this);

        getServer().getScheduler().runTaskLater(this, () -> {
            this.sqlManager = new SQLManager(this);
            this.game = new Game(this);
            PropertiesManager.enablePatch();
            this.structureLoader = new StructureLoader(this);
            this.playerManager = new PlayerManager(this);
            this.winManager = new WinManager(this);
            this.accountManager = new AccountManager();
            this.worldManager = new WorldManager(this);

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

    public AccountManager getAccountManager() {
        return accountManager;
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