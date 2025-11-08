package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all detail sub-menus
 * Provides common functionality like pagination, back button, and navigation
 */
public abstract class DetailMenu {
    
    protected final WDPProgressPlugin plugin;
    protected final ProgressMenu mainMenu;
    protected int page = 0;
    
    public DetailMenu(WDPProgressPlugin plugin, ProgressMenu mainMenu) {
        this.plugin = plugin;
        this.mainMenu = mainMenu;
    }
    
    /**
     * Open this detail menu for a player
     * @param viewer The player viewing the menu
     * @param target The player whose progress is being viewed
     */
    public void open(Player viewer, Player target) {
        open(viewer, target, 0);
    }
    
    /**
     * Open this detail menu at a specific page
     */
    public void open(Player viewer, Player target, int page) {
        this.page = page;
        Inventory inv = createInventory(viewer, target, page);
        viewer.openInventory(inv);
    }
    
    /**
     * Create the inventory for this detail menu
     */
    protected abstract Inventory createInventory(Player viewer, Player target, int page);
    
    /**
     * Get the title for this detail menu
     */
    protected abstract String getMenuTitle(Player target);
    
    /**
     * Get the items to display in the scrollable area
     */
    protected abstract List<ItemStack> getDisplayItems(Player target, PlayerData data);
    
    /**
     * Get the category color for this menu
     */
    protected abstract ChatColor getCategoryColor();
    
    /**
     * Create a standard 54-slot inventory with pagination and navigation
     */
    protected Inventory createStandardInventory(Player viewer, Player target, int page) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        List<ItemStack> items = getDisplayItems(target, data);
        
        String title = getMenuTitle(target);
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        // Calculate pagination
        int itemsPerPage = 28; // 4 rows of 7 items
        int maxPages = (int) Math.ceil(items.size() / (double) itemsPerPage);
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());
        
        // Add items to scrollable area (slots 10-16, 19-25, 28-34, 37-43)
        int[] slots = {10, 11, 12, 13, 14, 15, 16,
                       19, 20, 21, 22, 23, 24, 25,
                       28, 29, 30, 31, 32, 33, 34,
                       37, 38, 39, 40, 41, 42, 43};
        
        int itemIndex = startIndex;
        for (int i = 0; i < slots.length && itemIndex < endIndex; i++) {
            inv.setItem(slots[i], items.get(itemIndex));
            itemIndex++;
        }
        
        // Add navigation buttons
        if (page > 0) {
            inv.setItem(45, createPreviousPageButton());
        }
        if (page < maxPages - 1) {
            inv.setItem(53, createNextPageButton());
        }
        
        // Add page indicator
        inv.setItem(49, createPageIndicator(page + 1, maxPages, items.size()));
        
        // Add back button
        inv.setItem(48, createBackButton());
        
        // Fill borders with glass panes
        fillBorders(inv);
        
        return inv;
    }
    
    /**
     * Create a back button to return to main menu
     */
    protected ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "← Back to Main Menu");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to return to the");
        lore.add(ChatColor.GRAY + "main progress menu");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create previous page button
     */
    protected ItemStack createPreviousPageButton() {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "← Previous Page");
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create next page button
     */
    protected ItemStack createNextPageButton() {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Next Page →");
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create page indicator
     */
    protected ItemStack createPageIndicator(int currentPage, int totalPages, int totalItems) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Page " + currentPage + "/" + totalPages);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Total Items: " + ChatColor.WHITE + totalItems);
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Fill borders with decorative glass panes
     */
    protected void fillBorders(Inventory inv) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.setDisplayName(" ");
        border.setItemMeta(meta);
        
        // Top row
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
        // Bottom row
        for (int i = 45; i < 54; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
        // Left and right borders
        for (int i = 9; i < 45; i += 9) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
        for (int i = 17; i < 45; i += 9) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
    }
    
    /**
     * Handle click in detail menu
     */
    public boolean handleClick(Player viewer, Player target, ItemStack clicked, int slot) {
        if (clicked == null || clicked.getItemMeta() == null) {
            return false;
        }
        
        String displayName = clicked.getItemMeta().getDisplayName();
        
        // Handle back button
        if (displayName.contains("Back to Main Menu")) {
            mainMenu.openProgressMenu(viewer, target);
            return true;
        }
        
        // Handle pagination
        if (displayName.contains("Previous Page")) {
            open(viewer, target, page - 1);
            return true;
        }
        if (displayName.contains("Next Page")) {
            open(viewer, target, page + 1);
            return true;
        }
        
        return false;
    }
}
