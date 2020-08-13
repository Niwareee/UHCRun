package fr.niware.uhcrun.game;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.player.DeadPlayer;
import fr.niware.uhcrun.structure.Structure;
import fr.niware.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Game {

    // INIT

    private long startMillis;
    private int sizePlayers;
    private Player winner;

    private World world;
    private Location spawn;
    private Location specSpawn;

    private final Map<String, Structure> structure;
    private final Map<UUID, DeadPlayer> deadPlayers;
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

    private boolean invincibility = true;

    private int slot;
    private boolean runnable;
    private final int autoStartSize;
    private final int autoStartTime;
    private final String hubServerName;

    private final int sizeMap;
    private final int sizeNether;
    private final int preLoad;
    private final int preLoadNether;

    // PLATFORMS BLOCS

    private final List<Block> blocks;
    private final Map<UUID, Location> stayLocs;

    // POTION EFFECTS

    private final List<PotionEffect> deathPotionEffects;
    private final List<PotionEffect> startPotionEffects;


    public Game(Main main) {

        State.setState(State.PREGEN);
        MinecraftServer.getServer().setMotd("§eChargement...");

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

        this.structure = new HashMap<>();
        this.deadPlayers = new HashMap<>();
        this.alivePlayers = new ArrayList<>();
        this.decoPlayers = new ArrayList<>();

        this.blocks = new ArrayList<>();
        this.stayLocs = new HashMap<>();

        this.deathPotionEffects = new ArrayList<>();
        this.deathPotionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0, false, false));
        this.deathPotionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, false));

        this.startPotionEffects = new ArrayList<>();
        this.startPotionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 3, 1, false, false));
        this.startPotionEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 0, false, false));
        this.startPotionEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, false, false));
    }

    //---------------------------------------------------//

    public long getStartMillis(){
        return startMillis;
    }

    public void setStartMillis(long startMillis){
        this.startMillis = startMillis;
    }

    public int getSizePlayers(){
        return sizePlayers;
    }

    public void setSizePlayers(int sizePlayers){
        this.sizePlayers = sizePlayers;
    }

    public Player getWinner(){
        return winner;
    }

    public boolean isWinner(UUID uuid){
        return uuid == winner.getUniqueId();
    }

    public void setWinner(Player winner){
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

    public int getBorderTime() {
        return borderTime;
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

    public void resetCountdownStart(int task) {
        this.countdownStart = autoStartTime;
        Bukkit.broadcastMessage("§dUHCRun §7» §cIl n'y a pas assez de joueurs pour démarrer.");
        Bukkit.getScheduler().cancelTask(task);
        State.setState(State.WAITING);
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

    public Location getSpecSpawn() {
        return specSpawn;
    }

    public void setSpecSpawn(Location specSpawn) {
        this.specSpawn = specSpawn;
    }

    public int getTPBorder() {
        return tpBorder;
    }

    public void setTPBorder(int tpBorder) {
        this.tpBorder = tpBorder;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getAutoStartSize() {
        return autoStartSize;
    }

    public int getAutoStartTime() {
        return autoStartTime;
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
}