package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.utils.State;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class NoMoveListener implements Listener {

	private final Game game;

	public NoMoveListener(){
		this.game = Main.getInstance().getGame();
	}

    public final Listener moveListener = new Listener() {

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
		    	System.out.print("test");
		    	event.getHandlers().unregister(moveListener);
	        }
	    }
    };

	public final Listener chunkListener = new Listener() {
		@EventHandler(priority = EventPriority.LOW)
		public void onChunkUnload(ChunkUnloadEvent event) {
			event.getChunk().unload(true);
		}
	};
}