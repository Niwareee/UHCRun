package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import org.bukkit.WorldBorder;

public class MoveBorder extends UHCEvent {

    private final Game game;

    public MoveBorder(Game game){
        this.game = game;
    }

    @Override
    public void activate() {
        WorldBorder worldBorder = game.getWorld().getWorldBorder();
        worldBorder.setSize(game.getSizeTpBorder() * 2);
        worldBorder.setSize(game.getFinalBorderSize() * 2, game.getBorderMoveTime() * 60);
    }
}
