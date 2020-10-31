package fr.niware.uhcrun.game;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.player.DeadPlayer;
import fr.niware.uhcrun.utils.structure.Structure;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Game {

    // INIT

    private Player winner;
    private World world;

    private long startMillis;
    private int sizePlayers;
    private Location spawn;
    private Location specSpawn;

    // CONFIGURATION

    private int pvpTime = 1200;
    private int borderMoveTime = 10;

    private int countdownStart;
    private int timer = 0;

    private int sizeTpBorder = 300;

    private boolean invincibility = true;

    private final int slot;
    private boolean runnable;
    private final int autoStartSize;
    private final int autoStartTime;
    private final String hubServerName;

    private final int sizeMap;
    private final int sizeNether;
    private final int preLoad;
    private final int preLoadNether;

    // LIST

    private final Map<String, Structure> structure = new HashMap<>();
    private final Map<UUID, DeadPlayer> deadPlayers = new HashMap<>();
    private final List<UUID> alivePlayers = new ArrayList<>();
    private final List<UUID> decoPlayers = new ArrayList<>();

    private final List<Block> blocks = new ArrayList<>();
    private final Map<UUID, Location> stayLocs = new HashMap<>();

    private final List<PotionEffect> deathPotionEffects = new ArrayList<>();
    private final List<PotionEffect> startPotionEffects = new ArrayList<>();


    public Game(UHCRun main) {
        GameState.setState(GameState.PRELOAD);

        FileConfiguration config = main.getConfig();
        this.sizeMap = config.getInt("world.sizeMap");
        this.preLoad = config.getInt("world.sizePreload");
        this.sizeNether = config.getInt("nether.sizeMap");
        this.preLoadNether = config.getInt("nether.sizePreload");

        this.slot = config.getInt("game.slot");
        this.autoStartSize = config.getInt("game.autoStartSize");
        this.autoStartTime = config.getInt("game.autoStartTime");
        this.hubServerName = config.getString("game.hubServerName");
        this.countdownStart = autoStartTime;

        this.deathPotionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0, false, false));
        this.deathPotionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, false));

        this.startPotionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2, 0, false, false));
        this.startPotionEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 0, false, false));
        this.startPotionEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, false, false));
    }

    //---------------------------------------------------//

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public int getSizePlayers() {
        return sizePlayers;
    }

    public void setSizePlayers(int sizePlayers) {
        this.sizePlayers = sizePlayers;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isWinner(UUID uuid) {
        return uuid == winner.getUniqueId();
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public World getWorld() {
        return world;
    }

    public World setWorld(World world) {
        this.world = world;
        return world;
    }

    public Map<String, Structure> getStructure() {
        return structure;
    }

    public Map<UUID, DeadPlayer> getDeadPlayers() {
        return deadPlayers;
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public List<UUID> getDecoPlayers() {
        return decoPlayers;
    }

    public int getPvPTime() {
        return pvpTime;
    }

    public void removePvPTime() {
        this.pvpTime--;
    }

    public boolean isInvincibility() {
        return invincibility;
    }

    public void setInvincibility(boolean invincibility) {
        this.invincibility = invincibility;
    }

    public int getCountdownStart() {
        return countdownStart;
    }

    public void removeCountdownStart() {
        this.countdownStart--;
    }

    public void resetCountdownStart() {
        this.countdownStart = autoStartTime;
        Bukkit.broadcastMessage("§dUHCRun §7» §cIl n'y a pas assez de joueurs pour démarrer.");
        GameState.setState(GameState.WAITING);
    }

    public int getTimer() {
        return timer;
    }

    public void addTimer() {
        this.timer++;
    }

    public int getFinalBorderSize() {
        return 50;
    }

    public int getBorderMoveTime() {
        return borderMoveTime;
    }

    public int getSizeMap() {
        return sizeMap;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location setSpawn(Location spawn) {
        this.spawn = spawn;
        return spawn;
    }

    public Location getSpecSpawn() {
        return specSpawn;
    }

    public void setSpecSpawn(Location specSpawn) {
        this.specSpawn = specSpawn;
    }

    public int getSizeTpBorder() {
        return sizeTpBorder;
    }

    public int getSlot() {
        return slot;
    }

    public int getAutoStartSize() {
        return autoStartSize;
    }

    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

    public boolean isRunnable() {
        return runnable;
    }

    public int getSizeNether() {
        return sizeNether;
    }

    public int getPreLoadNether() {
        return preLoadNether;
    }

    public int getPreLoad() {
        return preLoad;
    }

    public Map<UUID, Location> getStayLocs() {
        return stayLocs;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public String getHubServerName() {
        return hubServerName;
    }

    public Collection<PotionEffect> getDeathPotionEffects() {
        return deathPotionEffects;
    }

    public Collection<PotionEffect> getStartPotionEffects() {
        return startPotionEffects;
    }

    public void sendToAll(String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        Bukkit.getServer().getOnlinePlayers().forEach(all -> ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet));
    }
}