package fr.niware.uhcrun.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class AutoLapisListener implements Listener {

    private final ItemStack lapis;

    public AutoLapisListener() {
        this.lapis = new ItemStack(Material.INK_SACK, 3, (short) 4);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING){
            event.getInventory().setItem(1, this.lapis);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING){
            event.getInventory().setItem(1, null);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.ENCHANTING && event.getSlot() == 1){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        event.getInventory().setItem(1, this.lapis);
    }
}
