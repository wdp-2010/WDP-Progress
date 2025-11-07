package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

/**
 * Handles statistic events for progress tracking
 */
public class StatisticsListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    private long lastUpdate = 0;
    private static final long UPDATE_COOLDOWN = 10000; // 10 seconds cooldown
    
    public StatisticsListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (!plugin.getConfigManager().isStatisticsEnabled()) {
            return;
        }
        
        // Throttle updates to prevent spam from rapid statistic changes
        long now = System.currentTimeMillis();
        if (now - lastUpdate < UPDATE_COOLDOWN) {
            return;
        }
        lastUpdate = now;
        
        // Trigger progress recalculation
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline()) {
                plugin.getPlayerDataManager().updatePlayerProgress(event.getPlayer().getUniqueId(), true);
            }
        }, 100L); // 5 second delay to batch multiple stat changes
    }
}
