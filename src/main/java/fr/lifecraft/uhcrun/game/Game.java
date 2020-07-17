package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class Game {

	// PREGAME
    private boolean start = false;

    // IMAGE
    private World world;
    private List<UUID> alivePlayers;
	private List<UUID> decoPlayers;
    
    // CONFIGURATION

    private int pvp = 20;
    private int border = 20;
    private int bordermovetime = 10;
    private int timer = 30;

    private int tpborder = 300;
    private int finalborder = 50;
    
    private int featherrate = 1;
    private int stringrate = 1;
    
    private boolean invincibility = true;
    private boolean isBorder = true;
    
    private boolean forcestart = false;
    
    private Location spawn;

	private int slot;
	private int autoStart;
	private int autoStartTime;
	private boolean runnable;

	private int size;
	private int sizeNether;
	private int preLoad;
	private int preLoadNether;

	public Game(Main main){

		State.setState(State.PREGEN);
		MinecraftServer.getServer().setMotd("§eChargement...");

		FileConfiguration config = main.getConfig();
    	this.size = config.getInt("world.sizeMap");
		this.preLoad = config.getInt("world.preload.sizePreload");
		this.sizeNether = config.getInt("nether.sizeMap");
		this.preLoadNether = config.getInt("nether.sizePreload");

		this.slot = config.getInt("game.slot");
		this.autoStart = config.getInt("game.autostart");
		this.autoStartTime = config.getInt("game.autostarttime");

		this.alivePlayers = new ArrayList<>();
		this.decoPlayers = new ArrayList<>();
	}

    //---------------------------------------------------//

	public World getWorld() {
		return world;
	}
	
	public World setWorld(World world) {
		this.world = world;
		return world;
	}

	public List<UUID> getAlivePlayers(){
		return alivePlayers;
	}

	public int getBorder() {
		return border;
	}

	public int getPvPTime() {
		return pvp;
	}
	
	public int getBorderTime() {
		return border;
	}
	
	public int getFeatherRate() {
		return featherrate;
	}
	
	public int getStringRate() {
		return stringrate;
	}

	public boolean isInvincibility() {
		return invincibility;
	}

	public void setInvincibility(boolean invincibility) {
		this.invincibility = invincibility;
	}

	public boolean isBorder() {
		return isBorder;
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public int getFinalBorderSize() {
		return finalborder;
	}

	public int getBorderMoveTime() {
		return bordermovetime;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public boolean isStarting() {
		return start;
	}
	
	public void setStart(boolean start) {
		this.start = start;
	}

	public Location getSpawn() {
		return spawn;
	}

	public Location setSpawn(Location spawn) {
		this.spawn = spawn;
		return spawn;
	}

	public int getTPBorder() {
		return tpborder;
	}

	public void setTPBorder(int tpborder) {
		this.tpborder = tpborder;
	}

	public boolean isForcestart() {
		return forcestart;
	}

	public void setForcestart(boolean forcestart) {
		this.forcestart = forcestart;
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

    public void setRunnable(boolean b) {
    	this.runnable = b;
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
}