package com.wdp.progress.api;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.DatabaseManager;
import com.wdp.progress.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Public API for other plugins to interact with the progress system.
 * 
 * Usage example:
 * <pre>
 * ProgressAPI api = ((WDPProgressPlugin) Bukkit.getPluginManager().getPlugin("WDPProgress")).getProgressAPI();
 * double progress = api.getPlayerProgress(player);
 * </pre>
 */
public class ProgressAPI {
    
    private final WDPProgressPlugin plugin;
    
    public ProgressAPI(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Get a player's current progress score (1-100)
     * 
     * @param player The player to check
     * @return Progress score from 1-100
     */
    public double getPlayerProgress(Player player) {
        return getPlayerProgress(player.getUniqueId());
    }
    
    /**
     * Get a player's current progress score by UUID
     * 
     * @param uuid Player UUID
     * @return Progress score from 1-100
     */
    public double getPlayerProgress(UUID uuid) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);
        return data != null ? data.getCurrentProgress() : plugin.getConfigManager().getMinProgress();
    }
    
    /**
     * Get a player's progress as a percentage (0.0 - 1.0)
     * 
     * @param player The player to check
     * @return Progress as decimal percentage
     */
    public double getPlayerProgressPercentage(Player player) {
        double progress = getPlayerProgress(player);
        return progress / 100.0;
    }
    
    /**
     * Check if a player has reached a certain progress threshold
     * 
     * @param player The player to check
     * @param threshold The minimum progress required
     * @return true if player's progress >= threshold
     */
    public boolean hasProgress(Player player, double threshold) {
        return getPlayerProgress(player) >= threshold;
    }
    
    /**
     * Force recalculation of a player's progress
     * 
     * @param player The player to recalculate
     * @return The new progress value
     */
    public double recalculateProgress(Player player) {
        return plugin.getPlayerDataManager().forceRecalculate(player.getUniqueId());
    }
    
    /**
     * Grant a custom achievement to a player
     * 
     * @param player The player to grant achievement to
     * @param achievementId The achievement identifier (must be defined in config)
     * @return true if successful, false if already had it or invalid
     */
    public boolean grantAchievement(Player player, String achievementId) {
        if (!plugin.getConfigManager().allowExternalModifications()) {
            return false;
        }
        return plugin.getPlayerDataManager().grantAchievement(player.getUniqueId(), achievementId);
    }
    
    /**
     * Remove a custom achievement from a player
     * 
     * @param player The player to remove achievement from
     * @param achievementId The achievement identifier
     * @return true if successful, false if didn't have it
     */
    public boolean removeAchievement(Player player, String achievementId) {
        if (!plugin.getConfigManager().allowExternalModifications()) {
            return false;
        }
        return plugin.getPlayerDataManager().removeAchievement(player.getUniqueId(), achievementId);
    }
    
    /**
     * Check if a player has a custom achievement
     * 
     * @param player The player to check
     * @param achievementId The achievement identifier
     * @return true if player has the achievement
     */
    public boolean hasAchievement(Player player, String achievementId) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        return data != null && data.hasAchievement(achievementId);
    }
    
    /**
     * Manually set a player's progress (requires external modification permission)
     * 
     * @param player The player to modify
     * @param progress The new progress value (will be clamped to min/max)
     * @return true if successful
     */
    public boolean setProgress(Player player, double progress) {
        if (!plugin.getConfigManager().allowExternalModifications()) {
            return false;
        }
        return plugin.getPlayerDataManager().setProgress(player.getUniqueId(), progress);
    }
    
    /**
     * Get the top players by progress
     * 
     * @param limit Maximum number of players to return
     * @return List of UUID-Progress pairs, sorted by progress descending
     */
    public List<Map.Entry<UUID, Double>> getTopPlayers(int limit) {
        return plugin.getDatabaseManager().getTopPlayers(limit);
    }
    
    /**
     * Get progress history for a player
     * 
     * @param player The player to check
     * @param limit Maximum number of history entries
     * @return List of progress history entries
     */
    public List<DatabaseManager.ProgressHistoryEntry> getProgressHistory(Player player, int limit) {
        return plugin.getDatabaseManager().getProgressHistory(player.getUniqueId(), limit);
    }
    
    /**
     * Get the configured minimum progress value
     * 
     * @return Minimum progress (usually 1)
     */
    public int getMinProgress() {
        return plugin.getConfigManager().getMinProgress();
    }
    
    /**
     * Get the configured maximum progress value
     * 
     * @return Maximum progress (usually 100)
     */
    public int getMaxProgress() {
        return plugin.getConfigManager().getMaxProgress();
    }
    
    /**
     * Check if the plugin is properly loaded and enabled
     * 
     * @return true if plugin is enabled and functional
     */
    public boolean isEnabled() {
        return plugin.isEnabled();
    }
}
