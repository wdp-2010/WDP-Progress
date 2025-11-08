package com.wdp.progress.data;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.api.events.ProgressChangeEvent;
import com.wdp.progress.progress.ProgressCalculator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Manages player data loading, saving, caching, and progress updates.
 * Provides a high-level interface for interacting with player progress data.
 */
public class PlayerDataManager {
    
    private final WDPProgressPlugin plugin;
    private final Map<UUID, PlayerData> playerDataCache;
    
    public PlayerDataManager(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        this.playerDataCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Load player data from database or cache
     */
    public PlayerData loadPlayerData(UUID uuid) {
        // Check cache first
        if (playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }
        
        // Load from database
        PlayerData data = plugin.getDatabaseManager().loadPlayerData(uuid);
        
        // Cache it
        if (plugin.getConfigManager().isCachingEnabled()) {
            playerDataCache.put(uuid, data);
        }
        
        return data;
    }
    
    /**
     * Save player data to database
     */
    public boolean savePlayerData(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) {
            return false;
        }
        
        data.setLastSeen(System.currentTimeMillis());
        
        return plugin.getDatabaseManager().savePlayerData(data);
    }
    
    /**
     * Get player data from cache (load if not cached)
     */
    public PlayerData getPlayerData(UUID uuid) {
        return loadPlayerData(uuid);
    }
    
    /**
     * Update player progress and recalculate
     */
    public void updatePlayerProgress(UUID uuid, boolean async) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return;
        }
        
        if (async && plugin.getConfigManager().isAsyncCalculationEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                performProgressUpdate(player);
            });
        } else {
            performProgressUpdate(player);
        }
    }
    
    /**
     * Perform the actual progress update
     */
    private void performProgressUpdate(Player player) {
        try {
            PlayerData data = getPlayerData(player.getUniqueId());
            double oldProgress = data.getCurrentProgress();
            
            // Calculate new progress
            ProgressCalculator calculator = plugin.getProgressCalculator();
            ProgressCalculator.ProgressResult result = calculator.calculateProgress(player, data);
            
            double newProgress = result.getFinalScore();
            data.setCurrentProgress(newProgress);
            
            // Check if change is significant enough to fire event
            double threshold = plugin.getConfigManager().getEventThreshold();
            if (Math.abs(newProgress - oldProgress) >= threshold) {
                // Fire event on main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (plugin.getConfigManager().fireProgressEvents()) {
                        ProgressChangeEvent event = new ProgressChangeEvent(player, oldProgress, newProgress, result);
                        Bukkit.getPluginManager().callEvent(event);
                    }
                });
                
                // Record in history
                plugin.getDatabaseManager().recordProgressHistory(player.getUniqueId(), newProgress);
            }
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info(String.format("Updated progress for %s: %.2f -> %.2f (delta: %.2f)",
                    player.getName(), oldProgress, newProgress, newProgress - oldProgress));
            }
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating progress for " + player.getName(), e);
        }
    }
    
    /**
     * Force recalculate progress for a player
     */
    public double forceRecalculate(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return -1;
        }
        
        performProgressUpdate(player);
        PlayerData data = getPlayerData(uuid);
        return data.getCurrentProgress();
    }
    
    /**
     * Recalculate progress for a player (convenience method)
     */
    public void recalculateProgress(UUID uuid) {
        forceRecalculate(uuid);
    }
    
    /**
     * Manually set player progress (admin command)
     */
    public boolean setProgress(UUID uuid, double progress) {
        PlayerData data = getPlayerData(uuid);
        if (data == null) {
            return false;
        }
        
        progress = Math.max(plugin.getConfigManager().getMinProgress(),
                   Math.min(plugin.getConfigManager().getMaxProgress(), progress));
        
        data.setCurrentProgress(progress);
        savePlayerData(uuid);
        
        return true;
    }
    
    /**
     * Reset player progress
     */
    public boolean resetProgress(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        playerDataCache.put(uuid, data);
        return plugin.getDatabaseManager().savePlayerData(data);
    }
    
    /**
     * Grant custom achievement to player
     */
    public boolean grantAchievement(UUID uuid, String achievementId) {
        PlayerData data = getPlayerData(uuid);
        if (data == null || data.hasAchievement(achievementId)) {
            return false;
        }
        
        data.addAchievement(achievementId);
        updatePlayerProgress(uuid, false);
        savePlayerData(uuid);
        
        return true;
    }
    
    /**
     * Remove custom achievement from player
     */
    public boolean removeAchievement(UUID uuid, String achievementId) {
        PlayerData data = getPlayerData(uuid);
        if (data == null || !data.hasAchievement(achievementId)) {
            return false;
        }
        
        data.removeAchievement(achievementId);
        updatePlayerProgress(uuid, false);
        savePlayerData(uuid);
        
        return true;
    }
    
    /**
     * Handle player death - update death time and apply penalties
     */
    public void handlePlayerDeath(UUID uuid) {
        PlayerData data = getPlayerData(uuid);
        if (data == null) {
            return;
        }
        
        data.setLastDeathTime(System.currentTimeMillis());
        
        // Trigger progress recalculation to apply death penalties
        updatePlayerProgress(uuid, false);
    }
    
    /**
     * Unload player data from cache
     */
    public void unloadPlayerData(UUID uuid) {
        // Save before unloading
        savePlayerData(uuid);
        playerDataCache.remove(uuid);
    }
    
    /**
     * Get cached player count
     */
    public int getCachedPlayerCount() {
        return playerDataCache.size();
    }
    
    /**
     * Clear the entire cache (use with caution)
     */
    public void clearCache() {
        // Save all cached data first
        for (UUID uuid : playerDataCache.keySet()) {
            savePlayerData(uuid);
        }
        playerDataCache.clear();
    }
    
    /**
     * Get progress for offline player
     */
    public double getOfflineProgress(UUID uuid) {
        PlayerData data = plugin.getDatabaseManager().loadPlayerData(uuid);
        return data.getCurrentProgress();
    }
}
