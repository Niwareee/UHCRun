package fr.niware.uhcrun.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.database.GameDatabase;
import fr.niware.uhcrun.database.Rank;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.player.UHCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PluginMessage implements PluginMessageListener {

    private final Game game;
    private final GameDatabase accountManager;
    private final PlayerManager playerManager;

    public PluginMessage(UHCRun main) {
        this.game = main.getGame();
        this.accountManager = main.getAccountManager();
        this.playerManager = main.getPlayerManager();

        main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
        // main.getServer().getMessenger().registerIncomingPluginChannel(main, "BungeeCord", this);

        main.log("Messenger system successfully load.");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        long start = System.currentTimeMillis();
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();

        if (!subChannel.equals("rankChannel")) {
            return;
        }

        String uuid = in.readUTF();
        int rankID = in.readInt();
        Rank rank = accountManager.getFromPower(rankID);

        playerManager.put(new UHCPlayer(UUID.fromString(uuid), game.getPlayerState(), rank, 0, 0));
        System.out.print("Action in " + (System.currentTimeMillis() - start) + " ms");
    }
}