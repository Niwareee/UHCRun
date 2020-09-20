package fr.niware.uhcrun.player.listeners;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class MoveListener implements Listener {

    private final Game game;

    public MoveListener(Main main) {
        this.game = main.getGame();
    }

    public final Listener moveListener = new Listener() {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            if (GameState.isState(GameState.TELEPORT)) {
                UUID uuid = event.getPlayer().getUniqueId();
                if (!game.getStayLocs().containsKey(uuid)) {
                    return;
                }

                if (event.getTo().distanceSquared(game.getStayLocs().get(uuid)) <= 30) {
                    return;
                }

                event.getPlayer().teleport(game.getStayLocs().get(uuid));
            }

            if (GameState.isState(GameState.MINING)) {
                event.getHandlers().unregister(moveListener);
            }
        }
    };
}
