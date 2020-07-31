package fr.niware.uhcrun.structure;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class BlockData {

    private Material material;
    private byte data;

    private Inventory inventory;
    private boolean inv;

    public BlockData(Material material, byte data, Inventory inventory) {
        this.material = material;
        this.data = data;
        this.inventory = inventory;
        inv = inventory != null;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean hasInv() {
        return inv;
    }
}
