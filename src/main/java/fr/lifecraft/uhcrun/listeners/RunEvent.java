package fr.lifecraft.uhcrun.listeners;

import fr.lifecraft.uhcrun.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RunEvent implements Listener {

	private final ItemStack lapis;

	public RunEvent(){
		this.lapis = new ItemStack(Material.INK_SACK, 3, (short) 4);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryOpen(InventoryOpenEvent e) {

		if (!(e.getInventory() instanceof EnchantingInventory)) {
			return;
		}
		((EnchantingInventory) e.getInventory()).setSecondary(this.lapis);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {

		if (!(e.getInventory() instanceof EnchantingInventory)) {
			return;
		}
		if ((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.INK_SACK)
				&& (e.getCurrentItem().getDurability() != 4)) {
			return;
		}
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent e) {

		if (!(e.getInventory() instanceof EnchantingInventory)) {
			return;
		}
		((EnchantingInventory) e.getInventory()).setSecondary(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantItem(EnchantItemEvent e) {

		if (!(e.getInventory() instanceof EnchantingInventory)) {
			return;
		}
		((EnchantingInventory) e.getInventory()).setSecondary(this.lapis);
	}

	@EventHandler
	public void onBrew(BrewEvent event) {
		if (event.getContents().getIngredient().getType() == Material.GLOWSTONE_DUST) {
			event.setCancelled(true);
		}
	}

	/*@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();

		Material itemType = e.getRecipe().getResult().getType();

		if (itemType == Material.GOLDEN_APPLE && e.getRecipe().getResult().getDurability() == 1) {
			e.setCancelled(true);
			p.sendMessage("§cErreur: Ce craft est désactivé.");
		}

	}*/

	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent e) {
		if (e.getInventory() != null && e.getInventory() != null) {
			Material itemType = e.getInventory().getResult().getType();
			if (itemType.name().contains("AXE") || itemType.name().contains("PICKAXE") || itemType.name().contains("SPADE") || itemType.name().contains("HOE"))
				e.getInventory().setResult(new ItemBuilder(itemType).addEnchant(Enchantment.DIG_SPEED, 3).addEnchant(Enchantment.DURABILITY, 3).toItemStack());
		}
	}
}
