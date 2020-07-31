package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result.getType() == Material.GOLDEN_APPLE && result.getDurability() == 1 || result.getType() == Material.BEACON) {
            Player player = (Player) event.getWhoClicked();

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
