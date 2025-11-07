package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join and quit events for data management
 */
public class PlayerJoinQuitListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    
    public PlayerJoinQuitListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load player data
        PlayerData data = plugin.getPlayerDataManager().loadPlayerData(event.getPlayer().getUniqueId());
        
        // Set first join if needed
        if (data.getFirstJoin() == 0 || data.getFirstJoin() > System.currentTimeMillis()) {
            data.setFirstJoin(System.currentTimeMillis());
        }
        
        // Update last seen
        data.setLastSeen(System.currentTimeMillis());
        
        // Grant first join achievement if enabled
        if (plugin.getConfigManager().isAchievementsEnabled()) {
            if (!data.hasAchievement("first_join")) {
                plugin.getPlayerDataManager().grantAchievement(event.getPlayer().getUniqueId(), "first_join");
            }
        }
        
        // Trigger initial progress calculation after a short delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getPlayerDataManager().updatePlayerProgress(event.getPlayer().getUniqueId(), true);
        }, 20L); // 1 second delay
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save and unload player data
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer().getUniqueId());
    }
}
