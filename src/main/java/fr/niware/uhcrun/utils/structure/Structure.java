package fr.niware.uhcrun.utils.structure;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class Structure {

    private String name;
    private BlockData[][][] blocks;

    private int xSize, ySize, zSize;
    private int xAnchor, yAnchor, zAnchor;

    public Structure(String name, BlockData[][][] blocks, int[] size, int[] anchor) {
        this.name = name;
        this.blocks = blocks;
        this.xSize = size[0];
        this.ySize = size[1];
        this.zSize = size[2];
        this.xAnchor = anchor[0];
        this.yAnchor = anchor[1];
        this.zAnchor = anchor[2];
    }

    public Structure(String name, int[] size, int[] anchor) {
        this.name = name;
        this.blocks = new BlockData[size[0]][size[1]][size[2]];
        this.xSize = size[0];
        this.ySize = size[1];
        this.zSize = size[2];
        this.xAnchor = anchor[0];
        this.yAnchor = anchor[1];
        this.zAnchor = anchor[2];
    }

    public BlockData[][][] getBlocks() {
        return blocks;
    }

    public void setBlocks(BlockData[][][] blocks) {
        this.blocks = blocks;
    }

    public void addBlock(int x, int y, int z, Material material, byte data, Inventory inventory) {
        this.blocks[x][y][z] = new BlockData(material, data, inventory);
    }

    public String getName() {
        return name;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getZSize() {
        return zSize;
    }

    public int getXAnchor() {
        return xAnchor;
    }

    public int getYAnchor() {
        return yAnchor;
    }

    public int getZAnchor() {
        return zAnchor;
    }
}
