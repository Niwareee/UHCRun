package fr.niware.uhcrun.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class AutoLapisListener implements Listener {

    private final ItemStack lapis;

    public AutoLapisListener() {
        this.lapis = new ItemStack(Material.INK_SACK, 3, (short) 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            event.getInventory().setItem(1, this.lapis);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.INK_SACK) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            event.getInventory().setItem(1, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(EnchantItemEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            event.getInventory().setItem(1, this.lapis);
        }
    }
}
