package fr.niware.uhcrun.listeners;

import fr.niware.uhcrun.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class CraftListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getInventory() != null) {
            Material itemType = event.getInventory().getResult().getType();

            if (itemType.name().contains("AXE") || itemType.name().contains("PICKAXE") || itemType.name().contains("SPADE") || itemType.name().contains("HOE")) {
                event.getInventory().setResult(new ItemBuilder(itemType).addEnchant(Enchantment.DIG_SPEED, 3).addEnchant(Enchantment.DURABILITY, 3).toItemStack());
                return;
            }

            if (itemType == Material.GOLDEN_APPLE && event.getInventory().getResult().getDurability() == 1 || itemType == Material.BEACON) {
                event.getRecipe().getResult().setType(Material.AIR);
            }
        }
    }
}
