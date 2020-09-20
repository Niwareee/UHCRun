package fr.niware.uhcrun;

import fr.niware.uhcrun.database.GameDatabase;
import fr.niware.uhcrun.database.sql.SQLManager;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.GameManager;
import fr.niware.uhcrun.game.manager.RegisterManager;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.utils.PluginMessage;
import fr.niware.uhcrun.utils.scoreboard.FastMain;
import fr.niware.uhcrun.utils.structure.StructureLoader;
import fr.niware.uhcrun.world.WorldManager;
import fr.niware.uhcrun.world.listeners.WorldInitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class UHCRun extends JavaPlugin {

    public static UHCRun instance;

    private Game game;
    private FastMain fastMain;

    private GameManager gameManager;
    private WorldManager worldManager;
    private GameDatabase accountManager;
    private PlayerManager playerManager;
    private StructureLoader structureLoader;
    private SQLManager sqlManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        this.getServer().getPluginManager().registerEvents(new WorldInitListener(this), this);

        getServer().getScheduler().runTaskLater(this, () -> {
            this.sqlManager = new SQLManager(this);
            this.game = new Game(this);
            this.fastMain = new FastMain(this);
            this.structureLoader = new StructureLoader(this);
            this.accountManager = new GameDatabase(this);
            this.playerManager = new PlayerManager(this);
            this.worldManager = new WorldManager(this);
            this.gameManager = new GameManager(this);

            new RegisterManager(this);
            new PluginMessage(this);
        }, 40);

        this.log("§6Running with " + Runtime.getRuntime().availableProcessors() + " threads and " + Runtime.getRuntime().maxMemory() / 1024L / 1024L + " Mo.");
        this.log("Plugin successfully enabled in " + (System.currentTimeMillis() - start) + " ms");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        worldManager.onDisable();
        super.onDisable();
    }

    public static UHCRun getInstance() {
        return UHCRun.instance;
    }

    public Game getGame() {
        return game;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public SQLManager getSQLManager() {
        return sqlManager;
    }

    public GameDatabase getAccountManager() {
        return accountManager;
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

    public FastMain getFastMain() {
        return fastMain;
    }
}