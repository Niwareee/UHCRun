package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.*;
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
        if ((event.getCurrentItem() != null) && (event.getCurrentItem().getType() != Material.INK_SACK)
                && (event.getCurrentItem().getDurability() != 4)) {
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

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack result = event.getRecipe().getResult();

        if (result.getType() == Material.GOLDEN_APPLE && result.getDurability() == 1) {
            event.setCancelled(true);
            player.sendMessage("§cErreur: Ce craft est désactivé.");
        }

    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getInventory() != null) {
            Material itemType = event.getInventory().getResult().getType();
            if (itemType.name().contains("AXE") || itemType.name().contains("PICKAXE") || itemType.name().contains("SPADE") || itemType.name().contains("HOE"))
                event.getInventory().setResult(new ItemBuilder(itemType).addEnchant(Enchantment.DIG_SPEED, 3).addEnchant(Enchantment.DURABILITY, 3).toItemStack());
        }
    }
}
