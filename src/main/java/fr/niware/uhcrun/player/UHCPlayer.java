package fr.niware.uhcrun.player;

import fr.niware.uhcrun.database.Rank;
import fr.niware.uhcrun.player.state.PlayerState;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UHCPlayer {

    private CraftPlayer player;
    private PlayerState playerState;
    private final Rank rank;
    private int killsGame;
    private final int killsAll;
    private final int wins;

    public UHCPlayer(PlayerState playerState, Rank rank, int killsAll, int wins){
        this.playerState = playerState;
        this.rank = rank;
        this.killsGame = 0;
        this.killsAll = killsAll;
        this.wins = wins;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public void setPlayer(CraftPlayer player) {
        this.player = player;
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public int getKillsGame() {
        return killsGame;
    }

    public void setKillsGame(int killsGame) {
        this.killsGame = killsGame;
    }

    public int getKillsAll() {
        return killsAll;
    }

    public Rank getRank() {
        return rank;
    }

    public int getWins() {
        return wins;
    }

    public void sendActionBar(String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        player.getHandle().playerConnection.sendPacket(packet);
    }

    public void sendTitle(int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        PlayerConnection connection = player.getHandle().playerConnection;

        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetPlayOutTimes);
        if (subtitle != null) {
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }
        if (title != null) {
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }
    }

    public void joinEffects() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setWalkSpeed(0.2f);
        player.getActivePotionEffects().forEach(potionEffects -> player.removePotionEffect(potionEffects.getType()));
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setFireTicks(0);
    }

    public void setSpectator() {
        player.setGameMode(GameMode.SPECTATOR);
    }
}
