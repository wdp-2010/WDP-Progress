package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Handles player death events for progress penalties
 */
public class PlayerDeathListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    
    public PlayerDeathListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfigManager().isDeathPenaltyEnabled()) {
            return;
        }
        
        // Increment death counter
        plugin.getPlayerDataManager().getPlayerData(event.getEntity().getUniqueId()).incrementDeaths();
        
        // Record death time and trigger progress recalculation
        plugin.getPlayerDataManager().handlePlayerDeath(event.getEntity().getUniqueId());
        
        // Grant first death achievement if enabled
        if (plugin.getConfigManager().isAchievementsEnabled()) {
            if (!plugin.getPlayerDataManager().getPlayerData(event.getEntity().getUniqueId()).hasAchievement("first_death")) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getPlayerDataManager().grantAchievement(event.getEntity().getUniqueId(), "first_death");
                }, 20L);
            }
        }
    }
}
