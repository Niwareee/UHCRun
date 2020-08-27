package fr.niware.uhcrun;

import fr.niware.uhcrun.account.AccountManager;
import fr.niware.uhcrun.database.SQLManager;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.manager.PlayerManager;
import fr.niware.uhcrun.game.manager.WinManager;
import fr.niware.uhcrun.listeners.WorldListener;
import fr.niware.uhcrun.register.RegisterManager;
import fr.niware.uhcrun.utils.scoreboard.FastMain;
import fr.niware.uhcrun.structure.StructureLoader;
import fr.niware.uhcrun.utils.PluginMessage;
import fr.niware.uhcrun.utils.packet.Title;
import fr.niware.uhcrun.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main instance;

    private Game game;
    private Title title;

    private WorldManager worldManager;
    private WinManager winManager;
    private AccountManager accountManager;
    private PlayerManager playerManager;
    private StructureLoader structureLoader;
    private SQLManager sqlManager;
    private FastMain fastMain;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);

        getServer().getScheduler().runTaskLater(this, () -> {
            this.sqlManager = new SQLManager(this);
            this.game = new Game(this);
            this.fastMain = new FastMain(this);
            this.structureLoader = new StructureLoader(this);
            this.accountManager = new AccountManager(this);
            this.playerManager = new PlayerManager(this);
            this.winManager = new WinManager(this);

            new RegisterManager(this);
            this.worldManager = new WorldManager(this);

            new PluginMessage(this);
        }, 40);

        this.title = new Title();

        this.log("Plugin successfully enabled in " + (System.currentTimeMillis() - start) + " ms");

        super.onEnable();
    }

    @Override
    public void onDisable() {
        worldManager.onDisable();

        super.onDisable();
    }

    public static Main getInstance() {
        return Main.instance;
    }

    public Game getGame() {
        return game;
    }

    public WorldManager getWorldManager() {
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

    public FastMain getFastMain() {
        return fastMain;
    }
}