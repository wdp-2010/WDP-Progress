package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Detailed menu showing all player advancements
 */
public class AdvancementsDetailMenu extends DetailMenu {
    
    public AdvancementsDetailMenu(WDPProgressPlugin plugin, ProgressMenu mainMenu) {
        super(plugin, mainMenu);
    }
    
    @Override
    protected String getMenuTitle(Player target) {
        return ChatColor.LIGHT_PURPLE + "📖 Advancements - " + target.getName();
    }
    
    @Override
    protected ChatColor getCategoryColor() {
        return ChatColor.LIGHT_PURPLE;
    }
    
    @Override
    protected Inventory createInventory(Player viewer, Player target, int page) {
        return createStandardInventory(viewer, target, page);
    }
    
    @Override
    protected List<ItemStack> getDisplayItems(Player target, PlayerData data) {
        List<ItemStack> items = new ArrayList<>();
        
        Iterator<Advancement> advIterator = Bukkit.advancementIterator();
        int completed = 0;
        int total = 0;
        
        while (advIterator.hasNext()) {
            Advancement advancement = advIterator.next();
            
            // Skip recipe advancements
            if (advancement.getKey().getKey().startsWith("recipes/")) {
                continue;
            }
            
            AdvancementProgress progress = target.getAdvancementProgress(advancement);
            boolean isDone = progress.isDone();
            
            total++;
            if (isDone) completed++;
            
            // Create item for this advancement
            ItemStack item = createAdvancementItem(advancement, progress, target);
            if (item != null) {
                items.add(item);
            }
        }
        
        // Add summary item at the top
        items.add(0, createSummaryItem(completed, total));
        
        return items;
    }
    
    private ItemStack createSummaryItem(int completed, int total) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + "⭐ Advancement Summary");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Completed: " + ChatColor.GREEN + completed + ChatColor.GRAY + "/" + ChatColor.WHITE + total);
        lore.add(ChatColor.GRAY + "Completion: " + ChatColor.AQUA + String.format("%.1f", (completed * 100.0 / total)) + "%");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Scroll down to see all advancements!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createAdvancementItem(Advancement advancement, AdvancementProgress progress, Player target) {
        // Get advancement display info
        String key = advancement.getKey().getKey();
        String namespace = advancement.getKey().getNamespace();
        
        // Skip non-minecraft advancements unless they're important
        if (!namespace.equals("minecraft")) {
            return null;
        }
        
        boolean isDone = progress.isDone();
        
        // Determine icon based on advancement
        Material icon = getAdvancementIcon(key);
        
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        
        // Format name
        String name = formatAdvancementName(key);
        ChatColor color = isDone ? ChatColor.GREEN : ChatColor.GRAY;
        meta.setDisplayName(color + (isDone ? "✓ " : "✗ ") + name);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: " + (isDone ? ChatColor.GREEN + "Completed ✓" : ChatColor.RED + "Not Completed ✗"));
        lore.add(ChatColor.GRAY + "Category: " + ChatColor.YELLOW + getCategory(key));
        
        // Add progress details
        if (!isDone && progress.getRemainingCriteria().size() > 0) {
            int remaining = progress.getRemainingCriteria().size();
            int total = advancement.getCriteria().size();
            int completed = total - remaining;
            lore.add(ChatColor.GRAY + "Progress: " + ChatColor.AQUA + completed + "/" + total);
        }
        
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private Material getAdvancementIcon(String key) {
        // Map advancement keys to appropriate icons
        if (key.contains("nether")) return Material.NETHERRACK;
        if (key.contains("end")) return Material.END_STONE;
        if (key.contains("dragon")) return Material.DRAGON_HEAD;
        if (key.contains("elytra")) return Material.ELYTRA;
        if (key.contains("diamond")) return Material.DIAMOND;
        if (key.contains("iron")) return Material.IRON_INGOT;
        if (key.contains("gold")) return Material.GOLD_INGOT;
        if (key.contains("netherite")) return Material.NETHERITE_INGOT;
        if (key.contains("enchant")) return Material.ENCHANTING_TABLE;
        if (key.contains("breed")) return Material.WHEAT;
        if (key.contains("fish")) return Material.FISHING_ROD;
        if (key.contains("adventure")) return Material.MAP;
        if (key.contains("husbandry")) return Material.WHEAT;
        if (key.contains("stone")) return Material.STONE_PICKAXE;
        if (key.contains("wood")) return Material.OAK_LOG;
        if (key.contains("shield")) return Material.SHIELD;
        if (key.contains("totem")) return Material.TOTEM_OF_UNDYING;
        if (key.contains("trident")) return Material.TRIDENT;
        if (key.contains("conduit")) return Material.CONDUIT;
        if (key.contains("beacon")) return Material.BEACON;
        
        return Material.PAPER; // Default
    }
    
    private String formatAdvancementName(String key) {
        // Convert snake_case to Title Case
        String[] parts = key.split("/");
        String name = parts[parts.length - 1];
        
        name = name.replace("_", " ");
        StringBuilder formatted = new StringBuilder();
        for (String word : name.split(" ")) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        return formatted.toString().trim();
    }
    
    private String getCategory(String key) {
        if (key.startsWith("story/")) return "Story";
        if (key.startsWith("nether/")) return "Nether";
        if (key.startsWith("end/")) return "The End";
        if (key.startsWith("adventure/")) return "Adventure";
        if (key.startsWith("husbandry/")) return "Husbandry";
        return "Minecraft";
    }
}
