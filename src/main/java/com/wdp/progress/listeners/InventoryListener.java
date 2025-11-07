package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

/**
 * Handles inventory and item events that may affect equipment score
 */
public class InventoryListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    private long lastUpdate = 0;
    private static final long UPDATE_COOLDOWN = 5000; // 5 seconds cooldown
    
    public InventoryListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!plugin.getConfigManager().isEquipmentEnabled()) {
            return;
        }
        
        // Throttle updates to prevent spam
        long now = System.currentTimeMillis();
        if (now - lastUpdate < UPDATE_COOLDOWN) {
            return;
        }
        lastUpdate = now;
        
        // Trigger progress recalculation after inventory changes
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer() instanceof org.bukkit.entity.Player) {
                org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getPlayer();
                if (player.isOnline()) {
                    plugin.getPlayerDataManager().updatePlayerProgress(player.getUniqueId(), true);
                }
            }
        }, 20L);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemBreak(PlayerItemBreakEvent event) {
        if (!plugin.getConfigManager().isEquipmentEnabled()) {
            return;
        }
        
        // Item broke - recalculate equipment score
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getPlayerDataManager().updatePlayerProgress(event.getPlayer().getUniqueId(), true);
        }, 10L);
    }
}
