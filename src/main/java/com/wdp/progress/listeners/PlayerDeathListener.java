package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Handles player death events for progress tracking.
 * 
 * NOTE: Death penalty calculation is now handled by GravesXIntegration
 * if the Graves plugin is present. This listener only handles:
 * - Basic death counting
 * - Achievement granting
 * - Fallback death tracking if GravesX is not available
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
        
        // Record death time - used for fallback penalty if GravesX is not available
        plugin.getPlayerDataManager().handlePlayerDeath(event.getEntity().getUniqueId());
        
        // Grant first death achievement if enabled
        if (plugin.getConfigManager().isAchievementsEnabled()) {
            if (!plugin.getPlayerDataManager().getPlayerData(event.getEntity().getUniqueId()).hasAchievement("first_death")) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getPlayerDataManager().grantAchievement(event.getEntity().getUniqueId(), "first_death");
                }, 20L);
            }
        }
        
        // If GravesX is not available, apply a basic penalty
        if (plugin.getGravesXIntegration() == null || !plugin.getGravesXIntegration().isEnabled()) {
            // Fallback: small temporary penalty
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getPlayerDataManager().recalculateProgress(event.getEntity().getUniqueId());
            });
        }
        // Otherwise, GravesXIntegration will handle the smart penalty calculation
    }
}

