package fr.niware.uhcrun.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class UHCRunListener implements Listener {

    private final ItemStack lapis;

    public UHCRunListener() {
        this.lapis = new ItemStack(Material.INK_SACK, 3, (short) 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {

        if (!(event.getInventory() instanceof EnchantingInventory)) {
            return;
        }
        ((EnchantingInventory) event.getInventory()).setSecondary(this.lapis);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getInventory() instanceof EnchantingInventory)) {
            return;
        }
        if ((event.getCurrentItem() != null) && (event.getCurrentItem().getType() != Material.INK_SACK) && (event.getCurrentItem().getDurability() != 4)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getInventory() instanceof EnchantingInventory)) {
            return;
        }
        ((EnchantingInventory) event.getInventory()).setSecondary(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(EnchantItemEvent event) {

        if (!(event.getInventory() instanceof EnchantingInventory)) {
            return;
        }
        ((EnchantingInventory) event.getInventory()).setSecondary(this.lapis);
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        if (event.getContents().getIngredient().getType() == Material.GLOWSTONE_DUST) {
            event.setCancelled(true);
        }
    }
}
