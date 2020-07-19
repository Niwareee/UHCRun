package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class Game {

    // INIT

    private World world;
    private final List<UUID> alivePlayers;
    private final List<UUID> decoPlayers;

    // CONFIGURATION

    private int pvpTime = 1200;
    private int borderTime = 20;
    private int borderMoveTime = 10;

    private int countdownStart;
    private int timer = 0;

    private int tpBorder = 300;
    private int finalBorderSize = 50;

    private int featherRate = 1;
    private int stringRate = 1;

    private boolean invincibility = true;
    private boolean forceStart = false;

    private Location spawn;

    private int slot;
    private final int autoStart;
    private final int autoStartTime;
    private boolean runnable;

    private final int sizeMap;
    private final int sizeNether;
    private final int preLoad;
    private final int preLoadNether;

    // PRELOAD BLOCS

    private final List<Block> blocks;
    private final Map<UUID, Location> stayLocs;

    public Game(Main main) {

        State.setState(State.PREGEN);
        MinecraftServer.getServer().setMotd("§eChargement...");

        FileConfiguration config = main.getConfig();
        this.sizeMap = config.getInt("world.sizeMap");
        this.preLoad = config.getInt("world.preload.sizePreload");
        this.sizeNether = config.getInt("nether.sizeMap");
        this.preLoadNether = config.getInt("nether.sizePreload");

        this.slot = config.getInt("game.slot");
        this.autoStart = config.getInt("game.autostart");
        this.autoStartTime = config.getInt("game.autostarttime");
        this.countdownStart = autoStartTime;

        this.alivePlayers = new ArrayList<>();
        this.decoPlayers = new ArrayList<>();

        this.blocks = new ArrayList<>();
        this.stayLocs = new HashMap<>();
    }

    //---------------------------------------------------//

    public World getWorld() {
        return world;
    }

    public World setWorld(World world) {
        this.world = world;
        return world;
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public int getPvPTime() {
        return pvpTime;
    }

    public void removePvPTime() {
        this.pvpTime--;
    }

    public int getBorderTime() {
        return borderTime;
    }

    public int getFeatherRate() {
        return featherRate;
    }

    public int getStringRate() {
        return stringRate;
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

    public void setCountdownStart(int countdownStart) {
        this.countdownStart = countdownStart;
    }

    public void removeCountdownStart() {
        this.countdownStart--;
    }

    public void resetCountdownStart() {
        this.countdownStart = autoStartTime;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void addTimer() {
        this.timer++;
    }

    public int getFinalBorderSize() {
        return finalBorderSize;
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

    public int getTPBorder() {
        return tpBorder;
    }

    public void setTPBorder(int tpBorder) {
        this.tpBorder = tpBorder;
    }

    public boolean isForceStart() {
        return forceStart;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getAutoStart() {
        return autoStart;
    }

    public int getAutoStartTime() {
        return autoStartTime;
    }

    public List<UUID> getDecoPlayers() {
        return decoPlayers;
    }

    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

    public boolean getRunnable() {
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
}