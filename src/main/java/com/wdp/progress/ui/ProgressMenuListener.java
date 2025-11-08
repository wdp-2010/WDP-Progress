package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles clicks in progress menu inventories
 */
public class ProgressMenuListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    
    public ProgressMenuListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if it's a progress menu
        String title = event.getView().getTitle();
        if (!title.contains("Progress") && !title.contains("Statistics") && 
            !title.contains("Advancements") && !title.contains("Equipment") &&
            !title.contains("Economy") && !title.contains("Experience") &&
            !title.contains("Death Penalty")) {
            return;
        }
        
        // Cancel all clicks in the menu
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player viewer = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getItemMeta() == null) {
            return;
        }
        
        String displayName = clicked.getItemMeta().getDisplayName();
        
        // Extract target player name from title
        String targetName = extractPlayerName(title);
        Player target = targetName != null ? plugin.getServer().getPlayer(targetName) : viewer;
        if (target == null) target = viewer;
        
        // Handle main menu clicks
        if (title.contains("'s Progress")) {
            handleMainMenuClick(viewer, target, displayName);
        }
        // Handle detail menu clicks
        else {
            handleDetailMenuClick(viewer, target, title, clicked, event.getSlot());
        }
    }
    
    private void handleMainMenuClick(Player viewer, Player target, String displayName) {
        ProgressMenu menu = plugin.getProgressMenu();
        
        // Check for close button
        if (displayName.contains("Close")) {
            viewer.closeInventory();
            return;
        }
        
        // Open detail menus based on category clicked
        if (displayName.contains("Advancements")) {
            menu.getAdvancementsDetailMenu().open(viewer, target);
        } else if (displayName.contains("Equipment")) {
            menu.getEquipmentDetailMenu().open(viewer, target);
        } else if (displayName.contains("Statistics")) {
            menu.getStatisticsDetailMenu().open(viewer, target);
        } else if (displayName.contains("Economy") || displayName.contains("Experience")) {
            menu.getEconomyExperienceDetailMenu().open(viewer, target);
        } else if (displayName.contains("Death Penalties")) {
            menu.getDeathPenaltyDetailMenu().open(viewer, target);
        }
    }
    
    private void handleDetailMenuClick(Player viewer, Player target, String title, ItemStack clicked, int slot) {
        ProgressMenu menu = plugin.getProgressMenu();
        
        // Determine which detail menu we're in and delegate
        if (title.contains("Advancements")) {
            menu.getAdvancementsDetailMenu().handleClick(viewer, target, clicked, slot);
        } else if (title.contains("Equipment")) {
            menu.getEquipmentDetailMenu().handleClick(viewer, target, clicked, slot);
        } else if (title.contains("Statistics")) {
            menu.getStatisticsDetailMenu().handleClick(viewer, target, clicked, slot);
        } else if (title.contains("Economy") || title.contains("Experience")) {
            menu.getEconomyExperienceDetailMenu().handleClick(viewer, target, clicked, slot);
        } else if (title.contains("Death Penalty")) {
            menu.getDeathPenaltyDetailMenu().handleClick(viewer, target, clicked, slot);
        }
    }
    
    private String extractPlayerName(String title) {
        // Extract player name from title like "⚡ PlayerName's Progress ⚡"
        if (title.contains("'s Progress")) {
            int start = title.indexOf("⚡ ") + 2;
            int end = title.indexOf("'s Progress");
            if (start > 1 && end > start) {
                return ChatColor.stripColor(title.substring(start, end).trim());
            }
        }
        // Extract from detail menus like "📖 Advancements - PlayerName"
        else if (title.contains(" - ")) {
            String[] parts = title.split(" - ");
            if (parts.length == 2) {
                return ChatColor.stripColor(parts[1].trim());
            }
        }
        return null;
    }
}

