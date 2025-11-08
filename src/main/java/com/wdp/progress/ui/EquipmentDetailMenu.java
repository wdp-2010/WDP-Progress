package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Detailed menu showing all player equipment
 */
public class EquipmentDetailMenu extends DetailMenu {
    
    private final DecimalFormat df = new DecimalFormat("#.#");
    
    public EquipmentDetailMenu(WDPProgressPlugin plugin, ProgressMenu mainMenu) {
        super(plugin, mainMenu);
    }
    
    @Override
    protected String getMenuTitle(Player target) {
        return ChatColor.AQUA + "⚔ Equipment - " + target.getName();
    }
    
    @Override
    protected ChatColor getCategoryColor() {
        return ChatColor.AQUA;
    }
    
    @Override
    protected Inventory createInventory(Player viewer, Player target, int page) {
        return createStandardInventory(viewer, target, page);
    }
    
    @Override
    protected List<ItemStack> getDisplayItems(Player target, PlayerData data) {
        List<ItemStack> items = new ArrayList<>();
        
        // Add armor
        items.add(createSectionHeader("Armor"));
        ItemStack helmet = target.getInventory().getHelmet();
        items.add(createEquipmentDisplay(helmet != null ? helmet : new ItemStack(Material.AIR), "Helmet Slot"));
        
        ItemStack chestplate = target.getInventory().getChestplate();
        items.add(createEquipmentDisplay(chestplate != null ? chestplate : new ItemStack(Material.AIR), "Chestplate Slot"));
        
        ItemStack leggings = target.getInventory().getLeggings();
        items.add(createEquipmentDisplay(leggings != null ? leggings : new ItemStack(Material.AIR), "Leggings Slot"));
        
        ItemStack boots = target.getInventory().getBoots();
        items.add(createEquipmentDisplay(boots != null ? boots : new ItemStack(Material.AIR), "Boots Slot"));
        
        // Check for full set bonus
        if (hasFullArmorSet(target)) {
            items.add(createBonusItem());
        }
        
        // Add held items
        items.add(createSectionHeader("Main Hand & Off Hand"));
        ItemStack mainHand = target.getInventory().getItemInMainHand();
        items.add(createEquipmentDisplay(mainHand, "Main Hand"));
        
        ItemStack offHand = target.getInventory().getItemInOffHand();
        items.add(createEquipmentDisplay(offHand, "Off Hand"));
        
        // Add notable inventory items
        items.add(createSectionHeader("Notable Items in Inventory"));
        
        List<ItemStack> notableItems = findNotableItems(target);
        if (notableItems.isEmpty()) {
            items.add(createEmptyMessage("No notable items found"));
        } else {
            for (ItemStack item : notableItems) {
                items.add(createEquipmentDisplay(item, null));
            }
        }
        
        return items;
    }
    
    private ItemStack createSectionHeader(String name) {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "━━━ " + name + " ━━━");
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createEquipmentDisplay(ItemStack equipment, String slotName) {
        if (equipment == null || equipment.getType() == Material.AIR) {
            ItemStack empty = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = empty.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + (slotName != null ? slotName : "Empty") + ": " + ChatColor.RED + "Empty");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "No item equipped");
            lore.add("");
            meta.setLore(lore);
            empty.setItemMeta(meta);
            return empty;
        }
        
        ItemStack display = equipment.clone();
        ItemMeta meta = display.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        // Material tier
        String tier = getMaterialTier(equipment.getType());
        lore.add(ChatColor.GRAY + "Tier: " + getTierColor(tier) + tier);
        
        // Durability
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            int maxDurability = equipment.getType().getMaxDurability();
            if (maxDurability > 0) {
                int currentDurability = maxDurability - damageable.getDamage();
                double durabilityPercent = (currentDurability * 100.0) / maxDurability;
                ChatColor durColor = durabilityPercent > 75 ? ChatColor.GREEN : 
                                    durabilityPercent > 50 ? ChatColor.YELLOW :
                                    durabilityPercent > 25 ? ChatColor.GOLD : ChatColor.RED;
                lore.add(ChatColor.GRAY + "Durability: " + durColor + currentDurability + "/" + maxDurability + 
                        " (" + df.format(durabilityPercent) + "%)");
            }
        }
        
        // Enchantments
        Map<Enchantment, Integer> enchants = equipment.getEnchantments();
        if (!enchants.isEmpty()) {
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + "Enchantments:");
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                String enchantName = formatEnchantmentName(entry.getKey().getKey().getKey());
                lore.add(ChatColor.GRAY + "  • " + ChatColor.AQUA + enchantName + " " + 
                        getRomanNumeral(entry.getValue()));
            }
        }
        
        // Item value estimation
        double value = calculateItemValue(equipment);
        if (value > 0) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Est. Value: " + ChatColor.GOLD + df.format(value) + " points");
        }
        
        lore.add("");
        
        // Update display name if slotName is provided
        if (slotName != null) {
            String originalName = meta.hasDisplayName() ? meta.getDisplayName() : 
                                 ChatColor.WHITE + formatMaterialName(equipment.getType());
            meta.setDisplayName(ChatColor.YELLOW + slotName + ": " + originalName);
        }
        
        meta.setLore(lore);
        display.setItemMeta(meta);
        
        return display;
    }
    
    private ItemStack createBonusItem() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "⭐ Full Armor Set Bonus! +15%");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "You have a complete armor set!");
        lore.add(ChatColor.GRAY + "Equipment score bonus: " + ChatColor.AQUA + "+15%");
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createEmptyMessage(String message) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + message);
        item.setItemMeta(meta);
        return item;
    }
    
    private boolean hasFullArmorSet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chest = player.getInventory().getChestplate();
        ItemStack legs = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        
        if (helmet == null || chest == null || legs == null || boots == null) return false;
        
        String helmetMat = helmet.getType().name();
        String chestMat = chest.getType().name();
        String legsMat = legs.getType().name();
        String bootsMat = boots.getType().name();
        
        String[] materials = {"LEATHER", "CHAINMAIL", "IRON", "GOLDEN", "DIAMOND", "NETHERITE"};
        for (String mat : materials) {
            if (helmetMat.contains(mat) && chestMat.contains(mat) && 
                legsMat.contains(mat) && bootsMat.contains(mat)) {
                return true;
            }
        }
        return false;
    }
    
    private List<ItemStack> findNotableItems(Player target) {
        List<ItemStack> notable = new ArrayList<>();
        
        for (ItemStack item : target.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            
            if (isNotableItem(item)) {
                notable.add(item);
                if (notable.size() >= 10) break; // Limit to 10 notable items
            }
        }
        
        return notable;
    }
    
    private boolean isNotableItem(ItemStack item) {
        Material type = item.getType();
        
        // Special items
        if (type == Material.ELYTRA || type == Material.TRIDENT || type == Material.TOTEM_OF_UNDYING ||
            type == Material.NETHERITE_INGOT || type == Material.NETHER_STAR || type == Material.DRAGON_HEAD ||
            type == Material.ENCHANTED_GOLDEN_APPLE || type == Material.BEACON) {
            return true;
        }
        
        // Netherite items
        if (type.name().contains("NETHERITE")) return true;
        
        // Highly enchanted items
        if (item.getEnchantments().size() >= 3) return true;
        
        return false;
    }
    
    private String getMaterialTier(Material material) {
        String name = material.name();
        if (name.contains("NETHERITE")) return "Netherite";
        if (name.contains("DIAMOND")) return "Diamond";
        if (name.contains("IRON")) return "Iron";
        if (name.contains("GOLDEN")) return "Gold";
        if (name.contains("STONE")) return "Stone";
        if (name.contains("LEATHER") || name.contains("CHAINMAIL")) return "Basic";
        if (name.contains("WOOD")) return "Wood";
        return "Common";
    }
    
    private ChatColor getTierColor(String tier) {
        switch (tier) {
            case "Netherite": return ChatColor.DARK_PURPLE;
            case "Diamond": return ChatColor.AQUA;
            case "Iron": return ChatColor.WHITE;
            case "Gold": return ChatColor.GOLD;
            default: return ChatColor.GRAY;
        }
    }
    
    private double calculateItemValue(ItemStack item) {
        double value = 0;
        
        // Base material value
        String tier = getMaterialTier(item.getType());
        switch (tier) {
            case "Netherite": value += 50; break;
            case "Diamond": value += 30; break;
            case "Iron": value += 15; break;
            case "Gold": value += 10; break;
            default: value += 5; break;
        }
        
        // Enchantment value
        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            value += entry.getValue() * 5;
        }
        
        // Durability factor
        if (item.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) item.getItemMeta();
            int maxDurability = item.getType().getMaxDurability();
            if (maxDurability > 0) {
                double durabilityPercent = ((maxDurability - damageable.getDamage()) * 100.0) / maxDurability;
                value *= (durabilityPercent / 100.0);
            }
        }
        
        return value;
    }
    
    private String formatEnchantmentName(String key) {
        String[] parts = key.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                formatted.append(Character.toUpperCase(part.charAt(0)))
                         .append(part.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        return formatted.toString().trim();
    }
    
    private String formatMaterialName(Material material) {
        String[] parts = material.name().split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                formatted.append(Character.toUpperCase(part.charAt(0)))
                         .append(part.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        return formatted.toString().trim();
    }
    
    private String getRomanNumeral(int number) {
        if (number <= 0) return "";
        String[] romanNumerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return number < romanNumerals.length ? romanNumerals[number] : String.valueOf(number);
    }
}
