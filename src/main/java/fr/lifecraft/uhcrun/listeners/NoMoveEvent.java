package fr.lifecraft.uhcrun.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.lifecraft.uhcrun.utils.Scatter;
import fr.lifecraft.uhcrun.utils.State;

public class NoMoveEvent implements Listener {

	private final Scatter scatter;

	public NoMoveEvent(){
		scatter = new Scatter(false, 1);
	}

    public final Listener listener = new Listener() {

	    @EventHandler
	    public void onMove(PlayerMoveEvent event) {
	        if (State.isState(State.TELEPORT)) {
	            if (scatter.getStayLocs().containsKey(event.getPlayer().getUniqueId())) {
	                if (event.getTo().distanceSquared(scatter.getStayLocs().get(event.getPlayer().getUniqueId())) > 40) {
	                    event.getPlayer().teleport(scatter.getStayLocs().get(event.getPlayer().getUniqueId()));
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
