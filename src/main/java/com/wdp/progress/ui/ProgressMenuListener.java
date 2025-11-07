package com.wdp.progress.ui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Handles clicks in progress menu inventories
 */
public class ProgressMenuListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if it's a progress menu
        String title = event.getView().getTitle();
        if (!title.contains("Progress")) {
            return;
        }
        
        // Cancel all clicks in the menu
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        // Check if clicked item is the close button
        if (event.getCurrentItem() != null && 
            event.getCurrentItem().getItemMeta() != null &&
            event.getCurrentItem().getItemMeta().getDisplayName().contains("Close")) {
            player.closeInventory();
        }
    }
}
