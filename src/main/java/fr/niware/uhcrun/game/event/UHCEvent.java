package fr.niware.uhcrun.game.event;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.world.WorldManager;

public abstract class UHCEvent {

    public UHCRun main;
    public Game game;
    public WorldManager worldManager;
    public PlayerManager playerManager;

    public UHCEvent(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
        this.worldManager = main.getWorldManager();
        this.playerManager = main.getPlayerManager();
    }

    public UHCEvent(UHCRun main, Game game) {
        this.main = main;
        this.game = game;
    }

    public UHCEvent(Game game) {
        this.game = game;
    }

    public void executeEvent() {
        activate();
    }

    public abstract void activate();
}
