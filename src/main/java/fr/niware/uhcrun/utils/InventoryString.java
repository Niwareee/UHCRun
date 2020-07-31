package fr.niware.uhcrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class InventoryString {

    @SuppressWarnings("deprecation")
	public static String InventoryToString(Inventory invInventory) {
        StringBuilder serialization = new StringBuilder(invInventory.getSize() + ";");
        for (int i = 0; i < invInventory.getSize(); i++) {
            ItemStack is = invInventory.getItem(i);
            if (is != null) {
                StringBuilder serializedItemStack = new StringBuilder();

                String isType = String.valueOf(is.getType().getId());
                serializedItemStack.append("t@").append(isType);

                if (is.getDurability() != 0) {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack.append(":d@").append(isDurability);
                }

                if (is.getAmount() != 1) {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack.append(":a@").append(isAmount);
                }

                Map<Enchantment, Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0) {
                    for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                        serializedItemStack.append(":e@").append(ench.getKey().getId()).append("@").append(ench.getValue());
                    }
                }

                if(is.getItemMeta().hasDisplayName()){
                    serializedItemStack.append(":n@").append(is.getItemMeta().getDisplayName()
                            .replace("@", "%a").replace(":", "%c").replace(";", "%s").replace("#", "%d"));
                }

                if(is.getItemMeta().spigot().isUnbreakable()){
                    serializedItemStack.append(":u@1");
                }

                if(!is.getItemMeta().getItemFlags().isEmpty()){
                    for (ItemFlag itemFlag : is.getItemMeta().getItemFlags()) {
                        if(itemFlagsId.containsKey(itemFlag)) serializedItemStack.append(":i@").append(itemFlagsId.get(itemFlag));
                    }
                }

                if (is.getType() == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
                    Map<Enchantment, Integer> storedEnchants = meta.getStoredEnchants();
                    if (storedEnchants.size() > 0) {
                        for (Entry<Enchantment, Integer> ench : storedEnchants.entrySet()) {
                            serializedItemStack.append(":s@").append(ench.getKey().getId()).append("@").append(ench.getValue());
                        }
                    }
                }

                serialization.append(i).append("#").append(serializedItemStack).append(";");
            }
        }
        return serialization.toString();
    }

    @SuppressWarnings("deprecation")
	public static Inventory StringToInventory(String invString) {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));

        for (int i = 1; i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.getSize()) {
                continue;
            }

            ItemStack is = null;
            boolean createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                } else if (itemAttribute[0].equals("d") && createdItemStack) {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("a") && createdItemStack) {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("e") && createdItemStack) {
                    is.addUnsafeEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                } else if (itemAttribute[0].equals("s") && createdItemStack) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
                    meta.addStoredEnchant(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]), true);
                    is.setItemMeta(meta);
                } else if (itemAttribute[0].equals("n") && createdItemStack) {
                    ItemMeta meta = is.getItemMeta();
                    meta.setDisplayName(itemAttribute[1].replace("%d", "#").replace("%s", ";").replace("%c", ":").replace("%a", "@"));
                    is.setItemMeta(meta);
                } else if (itemAttribute[0].equals("u") && createdItemStack) {
                    ItemMeta meta = is.getItemMeta();
                    meta.spigot().setUnbreakable((itemAttribute[1].equals("1")));
                    is.setItemMeta(meta);
                } else if (itemAttribute[0].equals("i") && createdItemStack) {
                    ItemMeta meta = is.getItemMeta();
                    for (int i1 = 1; i1 < itemAttribute.length; i1++) {
                        meta.addItemFlags(getItemFlag(i1));
                    }
                    is.setItemMeta(meta);
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }

        return deserializedInventory;
    }

    private static final Map<ItemFlag, Integer> itemFlagsId = new HashMap<>();

    static {
        itemFlagsId.put(ItemFlag.HIDE_ENCHANTS, 0);
        itemFlagsId.put(ItemFlag.HIDE_ATTRIBUTES, 1);
        itemFlagsId.put(ItemFlag.HIDE_UNBREAKABLE, 2);
        itemFlagsId.put(ItemFlag.HIDE_DESTROYS, 3);
        itemFlagsId.put(ItemFlag.HIDE_PLACED_ON, 4);
        itemFlagsId.put(ItemFlag.HIDE_POTION_EFFECTS, 5);
    }

    private static ItemFlag getItemFlag(int id){
        return itemFlagsId.keySet().stream().filter(f -> itemFlagsId.get(f) == id).findFirst().orElse(ItemFlag.HIDE_ATTRIBUTES);
    }

}
