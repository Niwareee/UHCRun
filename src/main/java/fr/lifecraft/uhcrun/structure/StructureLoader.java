package fr.lifecraft.uhcrun.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.InventoryString;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StructureLoader {

    private final Main main;

    private final Map<String, Structure> structures;
    private int changes = 0;
    private Thread thread;

    public StructureLoader(Main main) {
        this.main = main;
        this.structures = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
	public boolean save(Location location1, Location location2, Location anchor, String fileName) {

        if (location1.getWorld() != location2.getWorld() || location1.getWorld() != anchor.getWorld())
            return false;

        long start = System.currentTimeMillis();

        File schematic = new File(Main.getInstance().getDataFolder().getPath() + "/schematics/" + fileName + ".yml");
        if (!schematic.exists()) {
            try {
                schematic.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        int xMin, xMax, yMin, yMax, zMin, zMax;

        if (location1.getX() < location2.getX()) {
            xMin = (int) location1.getX();
            xMax = (int) location2.getX();
        } else {
            xMin = (int) location2.getX();
            xMax = (int) location1.getX();
        }

        if (location1.getY() < location2.getY()) {
            yMin = (int) location1.getY();
            yMax = (int) location2.getY();
        } else {
            yMin = (int) location2.getY();
            yMax = (int) location1.getY();
        }

        if (location1.getZ() < location2.getZ()) {
            zMin = (int) location1.getZ();
            zMax = (int) location2.getZ();
        } else {
            zMin = (int) location2.getZ();
            zMax = (int) location1.getZ();
        }

        /*Location min = new Location(location1.getWorld(), xMin, yMin, zMin);
        Location max = new Location(location1.getWorld(), xMax, yMax, zMax);*/

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(schematic);

        configuration.set("anchor.x", (int) anchor.getX() - xMin);
        configuration.set("anchor.y", (int) anchor.getY() - yMin);
        configuration.set("anchor.z", (int) anchor.getZ() - zMin);

        configuration.set("size.x", xMax - xMin + 1);
        configuration.set("size.y", yMax - yMin + 1);
        configuration.set("size.z", zMax - zMin + 1);

        int i = 0;

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block block = new Location(location1.getWorld(), x, y, z).getBlock();
                    Material material = block.getType();
                    if (material == Material.AIR) continue;
                    configuration.set("keys." + i, (x - xMin) + "/" + (y - yMin) + "/" + (z - zMin) + "/" + material.toString() + "/" + block.getData());
                    if (material == Material.CHEST || material == Material.TRAPPED_CHEST) {
                        Inventory inv = ((Chest) block.getState()).getBlockInventory();
                        if (inv.getContents() == null) continue;
                        configuration.set("inv." + i + ".content", InventoryString.InventoryToString(inv));
                    }
                    i++;
                }
            }
        }

        try {
            configuration.save(schematic);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        main.log("§eFile " + fileName + ".yml saved in " + (stop - start) + "ms.");

        return true;
    }

    public boolean load(String fileName) {

        long start = System.currentTimeMillis();

        structures.remove(fileName);

        File schematic = new File(Main.getInstance().getDataFolder().getPath() + "/schematics/" + fileName + ".yml");

        if (!schematic.exists()) return false;

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(schematic);
        ConfigurationSection keys = configuration.getConfigurationSection("keys");

        int[] size = new int[]{configuration.getInt("size.x"),
                configuration.getInt("size.y"), configuration.getInt("size.z")};
        int[] anchor = new int[]{configuration.getInt("anchor.x"),
                configuration.getInt("anchor.y"),
                configuration.getInt("anchor.z")};

        Structure structure = new Structure(fileName, size, anchor);

        for (String key : keys.getKeys(false)) {
            String value = keys.getString(key);
            String[] values = value.split("/");

            Inventory inventory = null;
            if (configuration.isSet("inv." + key + ".content")) {
                inventory = InventoryString.StringToInventory(configuration.getString("inv." + key + ".content"));
            }
            structure.addBlock(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Material.getMaterial(values[3]), Byte.parseByte(values[4]), inventory);
        }

        structures.put(fileName, structure);

        long stop = System.currentTimeMillis();
        main.log("§eStructure " + fileName + " loaded in " + (stop - start) + "ms.");

        return true;
    }

    public boolean paste(Location location, String fileName, boolean noAir) {

        changes = 0;

        if (!structures.containsKey(fileName)) load(fileName);

        long start = System.currentTimeMillis();

        Structure structure = structures.get(fileName);

        BlockData[][][] blocks = structure.getBlocks();

        World world = location.getWorld();
        int xStart = (int) location.getX() - structure.getXAnchor();
        int yStart = (int) location.getY() - structure.getYAnchor();
        int zStart = (int) location.getZ() - structure.getZAnchor();

        
        (thread = new Thread()).start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        synchronized (thread) {
				
	        for (int x = 0; x < structure.getXSize(); x++) {
	            for (int y = 0; y < structure.getYSize(); y++) {
	                for (int z = 0; z < structure.getZSize(); z++) {
	                    BlockData data = blocks[x][y][z];
	                    if (data == null) {
	                        if (!noAir) {
	                            Block block = new Location(world, xStart + x, yStart + y, zStart + z).getBlock();
	                            changeBlock(block, new BlockData(Material.AIR, (byte) 0, null));
	                        }
	                        continue;
	                    }
	                    Block block = new Location(world, xStart + x, yStart + y, zStart + z).getBlock();
	                    changeBlock(block, data);
	                }
	            }
	        }
	
	        long stop = System.currentTimeMillis();
	        main.log("§eStructure " + fileName + " pasted in " + (stop - start) + "ms. (" + changes + " blocks changed).");
	        return true;
		}
    }

    @SuppressWarnings("deprecation")
    private void changeBlock(Block block, BlockData data) {
        block.setType(data.getMaterial());
        block.setData(data.getData());
        if (data.hasInv()) {
            Chest chest = (Chest) block.getState();
            chest.getBlockInventory().setContents(data.getInventory().getContents());
        }
        changes++;
    }

}
