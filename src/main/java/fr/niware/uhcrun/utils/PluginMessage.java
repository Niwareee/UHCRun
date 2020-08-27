package fr.niware.uhcrun.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.account.AccountManager;
import fr.niware.uhcrun.account.Rank;
import fr.niware.uhcrun.game.manager.PlayerManager;
import fr.niware.uhcrun.game.player.UHCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PluginMessage implements PluginMessageListener {

    private final AccountManager accountManager;
    private final PlayerManager playerManager;

    public PluginMessage(Main main) {
        this.accountManager = main.getAccountManager();
        this.playerManager = main.getPlayerManager();

        main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
        main.getServer().getMessenger().registerIncomingPluginChannel(main, "BungeeCord", this);

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
        int rankId = in.readInt();
        Rank rank = accountManager.getFromPower(rankId);

        playerManager.put(new UHCPlayer(UUID.fromString(uuid), rank, 0, 0));
        System.out.print("Action in " + (System.currentTimeMillis() - start) + " ms");
    }
}