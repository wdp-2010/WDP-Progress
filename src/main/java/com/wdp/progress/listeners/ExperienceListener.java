package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 * Handles experience and level change events
 */
public class ExperienceListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    
    public ExperienceListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLevelChange(PlayerLevelChangeEvent event) {
        if (!plugin.getConfigManager().isExperienceEnabled()) {
            return;
        }
        
        // Only recalculate on level ups (not level downs from death)
        if (event.getNewLevel() > event.getOldLevel()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getPlayerDataManager().updatePlayerProgress(event.getPlayer().getUniqueId(), true);
            }, 5L);
        }
    }
}
