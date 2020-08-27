package fr.niware.uhcrun.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.niware.uhcrun.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessage implements PluginMessageListener {

    public PluginMessage(Main main) {
        main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
        main.getServer().getMessenger().registerIncomingPluginChannel(main, "BungeeCord", this);

        System.out.print("REGISTER");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
        try {
            String somedata = msgin.readUTF();
            short somenumber = msgin.readShort();

            Bukkit.broadcastMessage("SomeData: " + somedata);
            Bukkit.broadcastMessage("Number: " + somenumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (subChannel.equals("SubchannelPlayerCount")) {
            String server = in.readUTF();
            int playercount = in.readInt();
            System.out.print("There is " + playercount + " in the server " + server);
        }
    }
}