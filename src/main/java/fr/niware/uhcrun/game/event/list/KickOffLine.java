package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KickOffLine extends UHCEvent {

    public KickOffLine(UHCRun main, Game game) {
        super(main, game);
    }

    @Override
    public void activate() {
        List<UUID> uuids = new ArrayList<>(main.getGame().getAlivePlayers());

        uuids.stream().filter(uuid -> Bukkit.getPlayer(uuid) == null).forEach(uuid -> main.getGame().getAlivePlayers().remove(uuid));
        Bukkit.broadcastMessage("§dUHCRun §7» §cLes joueurs déconnectés ont été éliminés.");
        main.getPlayerManager().checkIsEnd();
    }
}
