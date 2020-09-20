package fr.niware.uhcrun.world.listeners;

import fr.niware.uhcrun.game.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    public final Listener chunkListener = new Listener() {
        @EventHandler(priority = EventPriority.LOW)
        public void onChunkUnload(ChunkUnloadEvent event) {
            event.getChunk().unload(true);
            if (GameState.isState(GameState.WAITING)) {
                event.getHandlers().unregister(chunkListener);
            }
        }
    };
}
