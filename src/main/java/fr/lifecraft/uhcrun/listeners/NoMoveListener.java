package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.lifecraft.uhcrun.utils.Scatter;
import fr.lifecraft.uhcrun.utils.State;

public class NoMoveListener implements Listener {

	private final Game game;

	public NoMoveListener(){
		this.game = Main.getInstance().getGame();
	}

    public final Listener listener = new Listener() {

	    @EventHandler
	    public void onMove(PlayerMoveEvent event) {
	        if (State.isState(State.TELEPORT)) {
	            if (game.getStayLocs().containsKey(event.getPlayer().getUniqueId())) {
	                if (event.getTo().distanceSquared(game.getStayLocs().get(event.getPlayer().getUniqueId())) > 40) {
	                    event.getPlayer().teleport(game.getStayLocs().get(event.getPlayer().getUniqueId()));
	                    return;
	                }
	            }
	        }
		    if (State.isState(State.MINING)) {
		    	event.getHandlers().unregister(listener);
	        }
	    }
    };
}
