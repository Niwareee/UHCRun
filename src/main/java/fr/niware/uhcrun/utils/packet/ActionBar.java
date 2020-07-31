package fr.niware.uhcrun.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar {  
    private final PacketPlayOutChat packet;

    public ActionBar(String text) {
        this.packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
    }
    
    public void sendToPlayer(Player p) {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    } 
    
    public void sendToAll() {
        Bukkit.getServer().getOnlinePlayers().forEach(all -> ((CraftPlayer)all).getHandle().playerConnection.sendPacket(packet));
    }
}