package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.utils.State;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.UUID;

public class TempListener implements Listener {

    private final Game game;

    public TempListener(Main main) {
        this.game = main.getGame();
    }

    public final Listener moveListener = new Listener() {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            if (State.isState(State.TELEPORT)) {
                UUID uuid = event.getPlayer().getUniqueId();
                if (!game.getStayLocs().containsKey(uuid)) {
                    return;
                }

                if (event.getTo().distanceSquared(game.getStayLocs().get(uuid)) <= 30) {
                    return;
                }

                event.getPlayer().teleport(game.getStayLocs().get(uuid));
            }

            if (State.isState(State.MINING)) {
                event.getHandlers().unregister(moveListener);
            }
        }
    };

    public final Listener chunkListener = new Listener() {
        @EventHandler(priority = EventPriority.LOW)
        public void onChunkUnload(ChunkUnloadEvent event) {
            event.getChunk().unload(true);
            if (State.isState(State.WAITING)) {
                event.getHandlers().unregister(chunkListener);
            }
        }
    };
}
