package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced administrative menu for managing player advancements.
 * 
 * Features:
 * - View all advancements for a specific player
 * - Filter by completed/incomplete status
 * - Reset individual advancements
 * - Bulk operations (reset all, complete all)
 * - Category-based viewing
 * 
 * OP-ONLY - This is a powerful tool for server administrators.
 */
public class AdvancementAdminMenu {
    
    private final WDPProgressPlugin plugin;
    
    public enum FilterMode {
        ALL,
        COMPLETED,
        INCOMPLETE
    }
    
    public enum Category {
        ALL("All", Material.COMPASS),
        STORY("Story", Material.GRASS_BLOCK),
        NETHER("Nether", Material.NETHERRACK),
        END("End", Material.END_STONE),
        ADVENTURE("Adventure", Material.MAP),
        HUSBANDRY("Husbandry", Material.WHEAT);
        
        private final String displayName;
        private final Material icon;
        
        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
    }
    
    public AdvancementAdminMenu(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open the advancement admin menu for a player
     * 
     * @param admin The admin viewing the menu (must be OP)
     * @param target The player whose advancements are being managed
     */
    public void openMenu(Player admin, Player target) {
        openMenu(admin, target, Category.ALL, FilterMode.ALL, 0);
    }
    
    /**
     * Open the advancement admin menu with filters
     */
    public void openMenu(Player admin, Player target, Category category, FilterMode filterMode, int page) {
        if (!admin.isOp()) {
            admin.sendMessage("§c§l✘ §cYou must be an OP to use the advancement admin menu!");
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 54, 
            "§8§l⚙ §6Advancements: §e" + target.getName());
        
        // Get all advancements for the player
        List<AdvancementData> advancements = getFilteredAdvancements(target, category, filterMode);
        
        // Pagination
        int maxPerPage = 45;
        int totalPages = (int) Math.ceil(advancements.size() / (double) maxPerPage);
        int startIndex = page * maxPerPage;
        int endIndex = Math.min(startIndex + maxPerPage, advancements.size());
        
        // Add advancement items
        for (int i = startIndex; i < endIndex; i++) {
            AdvancementData data = advancements.get(i);
            inv.setItem(i - startIndex, createAdvancementItem(data, target));
        }
        
        // Control panel at bottom
        inv.setItem(45, createCategoryItem(Category.ALL, category));
        inv.setItem(46, createCategoryItem(Category.STORY, category));
        inv.setItem(47, createCategoryItem(Category.NETHER, category));
        inv.setItem(48, createCategoryItem(Category.END, category));
        inv.setItem(49, createCategoryItem(Category.ADVENTURE, category));
        inv.setItem(50, createCategoryItem(Category.HUSBANDRY, category));
        
        inv.setItem(51, createFilterItem(filterMode));
        inv.setItem(52, createBulkActionItem(target));
        inv.setItem(53, createCloseItem());
        
        // Navigation
        if (page > 0) {
            inv.setItem(18, createNavigationItem("Previous Page", Material.ARROW, page - 1));
        }
        if (page < totalPages - 1) {
            inv.setItem(26, createNavigationItem("Next Page", Material.ARROW, page + 1));
        }
        
        // Store menu data in player metadata for click handling
        admin.setMetadata("wdp_adv_menu_target", new org.bukkit.metadata.FixedMetadataValue(plugin, target.getName()));
        admin.setMetadata("wdp_adv_menu_category", new org.bukkit.metadata.FixedMetadataValue(plugin, category.name()));
        admin.setMetadata("wdp_adv_menu_filter", new org.bukkit.metadata.FixedMetadataValue(plugin, filterMode.name()));
        admin.setMetadata("wdp_adv_menu_page", new org.bukkit.metadata.FixedMetadataValue(plugin, page));
        
        admin.openInventory(inv);
    }
    
    /**
     * Get filtered advancements based on category and completion status
     */
    private List<AdvancementData> getFilteredAdvancements(Player target, Category category, FilterMode filterMode) {
        List<AdvancementData> result = new ArrayList<>();
        
        Iterator<Advancement> advIterator = Bukkit.getServer().advancementIterator();
        while (advIterator.hasNext()) {
            Advancement advancement = advIterator.next();
            String key = advancement.getKey().toString();
            
            // Skip recipes
            if (key.contains("recipes/")) {
                continue;
            }
            
            // Category filter
            if (category != Category.ALL) {
                String advCategory = determineCategory(key);
                if (!advCategory.equalsIgnoreCase(category.name())) {
                    continue;
                }
            }
            
            // Completion filter
            AdvancementProgress progress = target.getAdvancementProgress(advancement);
            boolean completed = progress.isDone();
            
            if (filterMode == FilterMode.COMPLETED && !completed) {
                continue;
            }
            if (filterMode == FilterMode.INCOMPLETE && completed) {
                continue;
            }
            
            result.add(new AdvancementData(advancement, completed));
        }
        
        // Sort: incomplete first, then alphabetically
        result.sort((a, b) -> {
            if (a.completed != b.completed) {
                return a.completed ? 1 : -1;
            }
            return a.advancement.getKey().toString().compareTo(b.advancement.getKey().toString());
        });
        
        return result;
    }
    
    /**
     * Create an item representing an advancement
     */
    private ItemStack createAdvancementItem(AdvancementData data, Player target) {
        Material material = data.completed ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        String key = data.advancement.getKey().getKey();
        String displayName = formatAdvancementName(key);
        
        meta.setDisplayName((data.completed ? "§a§l✓ " : "§c§l✗ ") + "§6" + displayName);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Key: §f" + data.advancement.getKey());
        lore.add("§7Category: §f" + determineCategory(data.advancement.getKey().toString()));
        lore.add("§7Status: " + (data.completed ? "§a§lCOMPLETED" : "§c§lINCOMPLETE"));
        
        if (data.completed) {
            AdvancementProgress progress = target.getAdvancementProgress(data.advancement);
            lore.add("");
            lore.add("§e§lClick to REVOKE this advancement");
        } else {
            lore.add("");
            lore.add("§a§lClick to GRANT this advancement");
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create a category filter item
     */
    private ItemStack createCategoryItem(Category cat, Category currentCategory) {
        ItemStack item = new ItemStack(cat.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        boolean selected = cat == currentCategory;
        meta.setDisplayName((selected ? "§a§l▶ " : "§7") + cat.getDisplayName());
        
        if (selected) {
            meta.setLore(Arrays.asList("", "§a§lCurrently Selected"));
        } else {
            meta.setLore(Arrays.asList("", "§eClick to filter by this category"));
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create filter mode item
     */
    private ItemStack createFilterItem(FilterMode mode) {
        Material material = switch (mode) {
            case ALL -> Material.HOPPER;
            case COMPLETED -> Material.LIME_DYE;
            case INCOMPLETE -> Material.RED_DYE;
        };
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§b§lFilter: §f" + mode.name());
        meta.setLore(Arrays.asList(
            "",
            "§7Currently showing: §e" + mode.name(),
            "",
            "§eClick to cycle filter mode"
        ));
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create bulk action item
     */
    private ItemStack createBulkActionItem(Player target) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§c§l⚠ §6Bulk Actions");
        meta.setLore(Arrays.asList(
            "",
            "§e§lLeft Click: §cReset ALL advancements",
            "§e§lRight Click: §aGrant ALL advancements",
            "",
            "§c§lWARNING: This affects §e" + target.getName(),
            "§c§lThis action cannot be undone!"
        ));
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create close button
     */
    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lClose Menu");
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create navigation item
     */
    private ItemStack createNavigationItem(String name, Material material, int page) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§l" + name);
        meta.setLore(Arrays.asList("", "§7Page " + (page + 1)));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Format advancement key into readable name
     */
    private String formatAdvancementName(String key) {
        return Arrays.stream(key.replace("minecraft:", "").split("[/_]"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
    
    /**
     * Determine advancement category from key
     */
    private String determineCategory(String key) {
        if (key.contains("story/")) return "STORY";
        if (key.contains("nether/")) return "NETHER";
        if (key.contains("end/")) return "END";
        if (key.contains("adventure/")) return "ADVENTURE";
        if (key.contains("husbandry/")) return "HUSBANDRY";
        return "OTHER";
    }
    
    /**
     * Grant an advancement to a player
     */
    public void grantAdvancement(Player target, Advancement advancement, Player admin) {
        AdvancementProgress progress = target.getAdvancementProgress(advancement);
        
        if (progress.isDone()) {
            admin.sendMessage("§c§l✘ §e" + target.getName() + " §calready has this advancement!");
            return;
        }
        
        // Grant all criteria
        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }
        
        admin.sendMessage("§a§l✓ §aGranted advancement §e" + advancement.getKey().getKey() + " §ato " + target.getName());
        plugin.getLogger().info(admin.getName() + " granted advancement " + advancement.getKey() + " to " + target.getName());
        
        // Recalculate progress
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlayerDataManager().recalculateProgress(target.getUniqueId());
        });
    }
    
    /**
     * Revoke an advancement from a player
     */
    public void revokeAdvancement(Player target, Advancement advancement, Player admin) {
        AdvancementProgress progress = target.getAdvancementProgress(advancement);
        
        if (!progress.isDone()) {
            admin.sendMessage("§c§l✘ §e" + target.getName() + " §cdoesn't have this advancement!");
            return;
        }
        
        // Revoke all criteria
        for (String criteria : progress.getAwardedCriteria()) {
            progress.revokeCriteria(criteria);
        }
        
        admin.sendMessage("§c§l✓ §cRevoked advancement §e" + advancement.getKey().getKey() + " §cfrom " + target.getName());
        plugin.getLogger().info(admin.getName() + " revoked advancement " + advancement.getKey() + " from " + target.getName());
        
        // Recalculate progress
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlayerDataManager().recalculateProgress(target.getUniqueId());
        });
    }
    
    /**
     * Reset ALL advancements for a player
     */
    public void resetAllAdvancements(Player target, Player admin) {
        int count = 0;
        
        Iterator<Advancement> advIterator = Bukkit.getServer().advancementIterator();
        while (advIterator.hasNext()) {
            Advancement advancement = advIterator.next();
            
            if (advancement.getKey().toString().contains("recipes/")) {
                continue;
            }
            
            AdvancementProgress progress = target.getAdvancementProgress(advancement);
            if (progress.isDone()) {
                for (String criteria : progress.getAwardedCriteria()) {
                    progress.revokeCriteria(criteria);
                }
                count++;
            }
        }
        
        admin.sendMessage("§c§l✓ §cReset §e" + count + " §cadvancements for " + target.getName());
        plugin.getLogger().warning(admin.getName() + " reset ALL advancements for " + target.getName() + " (count: " + count + ")");
        
        // Recalculate progress
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlayerDataManager().recalculateProgress(target.getUniqueId());
        });
    }
    
    /**
     * Grant ALL advancements to a player
     */
    public void grantAllAdvancements(Player target, Player admin) {
        int count = 0;
        
        Iterator<Advancement> advIterator = Bukkit.getServer().advancementIterator();
        while (advIterator.hasNext()) {
            Advancement advancement = advIterator.next();
            
            if (advancement.getKey().toString().contains("recipes/")) {
                continue;
            }
            
            AdvancementProgress progress = target.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    progress.awardCriteria(criteria);
                }
                count++;
            }
        }
        
        admin.sendMessage("§a§l✓ §aGranted §e" + count + " §aadvancements to " + target.getName());
        plugin.getLogger().warning(admin.getName() + " granted ALL advancements to " + target.getName() + " (count: " + count + ")");
        
        // Recalculate progress
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlayerDataManager().recalculateProgress(target.getUniqueId());
        });
    }
    
    /**
     * Helper class to store advancement data
     */
    private static class AdvancementData {
        final Advancement advancement;
        final boolean completed;
        
        AdvancementData(Advancement advancement, boolean completed) {
            this.advancement = advancement;
            this.completed = completed;
        }
    }
}
