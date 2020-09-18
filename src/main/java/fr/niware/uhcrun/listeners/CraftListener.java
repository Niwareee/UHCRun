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
        if (event.getInventory() == null) {
            return;
        }

        Material type = event.getInventory().getResult().getType();

        if (type.name().contains("AXE") || type.name().contains("PICKAXE") || type.name().contains("SPADE") || type.name().contains("HOE")) {
            event.getInventory().setResult(new ItemBuilder(type).addEnchant(Enchantment.DIG_SPEED, 3).addEnchant(Enchantment.DURABILITY, 3).toItemStack());
            return;
        }

        if (type == Material.GOLDEN_APPLE && event.getInventory().getResult().getDurability() == 1 || type == Material.BEACON) {
            event.getRecipe().getResult().setType(Material.AIR);
        }
    }
}
