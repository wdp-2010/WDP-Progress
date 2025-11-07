package com.wdp.progress.listeners;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * Handles advancement completion events
 */
public class AdvancementListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    
    public AdvancementListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        if (!plugin.getConfigManager().isAdvancementsEnabled()) {
            return;
        }
        
        // Skip recipe advancements
        if (event.getAdvancement().getKey().toString().contains("recipes/")) {
            return;
        }
        
        // Trigger progress recalculation
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getPlayerDataManager().updatePlayerProgress(event.getPlayer().getUniqueId(), true);
        }, 10L);
    }
}
