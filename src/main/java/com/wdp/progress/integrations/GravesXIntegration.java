package com.wdp.progress.integrations;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

/**
 * Integration with GravesX plugin for intelligent death penalty calculation.
 * 
 * This integration:
 * - Tracks items lost on death
 * - Calculates value-based death penalties
 * - Reduces penalty as time passes (assumes player recovered items)
 * - Much smarter than the old system that just counted deaths!
 * 
 * Note: GravesX API is complex and changes between versions, so this uses
 * a simple approach: track what was dropped on death and apply a penalty
 * that decays over time (assuming recovery).
 */
public class GravesXIntegration implements Listener {
    
    private final WDPProgressPlugin plugin;
    private boolean enabled;
    
    public GravesXIntegration(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }
    
    /**
     * Initialize the GravesX integration
     */
    public boolean initialize() {
        try {
            Plugin gravesPluginInstance = plugin.getServer().getPluginManager().getPlugin("Graves");
            
            if (gravesPluginInstance == null || !gravesPluginInstance.isEnabled()) {
                plugin.getLogger().info("GravesX not found - death penalty will use basic calculation");
                return false;
            }
            
            this.enabled = true;
            
            // Register events
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            
            plugin.getLogger().info("Successfully hooked into GravesX - smart death tracking enabled!");
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to hook into GravesX", e);
            this.enabled = false;
            return false;
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Track death and calculate penalty based on items lost
     * 
     * We use PlayerDeathEvent because it's reliable and shows what was actually dropped.
     * GravesX will handle putting items in a grave, but we capture the value first.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        
        if (playerData == null) {
            return;
        }
        
        // Calculate value of all items that were dropped/lost
        double totalValue = 0.0;
        
        // Items from drops
        List<ItemStack> drops = event.getDrops();
        for (ItemStack item : drops) {
            totalValue += calculateItemValue(item);
        }
        
        // Items player is wearing (armor)
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                totalValue += calculateItemValue(item);
            }
        }
        
        if (totalValue <= 0) {
            // No items lost, no penalty
            return;
        }
        
        // Create a grave record with current timestamp
        String graveId = UUID.randomUUID().toString();
        PlayerData.GraveData graveData = new PlayerData.GraveData(graveId, totalValue);
        playerData.addGrave(graveId, graveData);
        
        // Calculate and apply death penalty
        updateDeathPenalty(playerData);
        
        // Schedule a task to reduce penalty over time (player likely recovered items)
        scheduleGraveDecay(player.getUniqueId(), graveId, totalValue);
        
        plugin.getLogger().info(String.format(
            "Player %s died - Grave value: %.2f (Initial penalty: %.2f points)",
            player.getName(),
            totalValue,
            playerData.getCurrentDeathPenalty()
        ));
    }
    
    /**
     * Schedule penalty reduction over time
     * Assumes player will recover most items within 5 minutes
     */
    private void scheduleGraveDecay(UUID playerUUID, String graveId, double initialValue) {
        // After 1 minute: assume 30% recovered
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(playerUUID);
            if (data != null) {
                PlayerData.GraveData grave = data.getGrave(graveId);
                if (grave != null) {
                    grave.addRecoveredValue(initialValue * 0.3);
                    updateDeathPenalty(data);
                }
            }
        }, 20L * 60); // 1 minute
        
        // After 3 minutes: assume 60% recovered
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(playerUUID);
            if (data != null) {
                PlayerData.GraveData grave = data.getGrave(graveId);
                if (grave != null) {
                    grave.addRecoveredValue(initialValue * 0.3); // Additional 30%
                    updateDeathPenalty(data);
                }
            }
        }, 20L * 60 * 3); // 3 minutes
        
        // After 5 minutes: assume 100% recovered, remove grave
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(playerUUID);
            if (data != null) {
                PlayerData.GraveData grave = data.getGrave(graveId);
                if (grave != null) {
                    grave.addRecoveredValue(initialValue * 0.4); // Final 40%
                    data.removeGrave(graveId);
                    updateDeathPenalty(data);
                    
                    Player player = plugin.getServer().getPlayer(playerUUID);
                    if (player != null && player.isOnline()) {
                        player.sendMessage("§a§l✓ §aGrave penalty fully removed - assumed items recovered!");
                    }
                }
            }
        }, 20L * 60 * 5); // 5 minutes
    }
    
    /**
     * Calculate the value of a single item
     */
    private double calculateItemValue(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0.0;
        }
        
        double value = 0.0;
        Material material = item.getType();
        
        // Base material values
        if (material.name().contains("NETHERITE")) {
            value += 100.0;
        } else if (material.name().contains("DIAMOND")) {
            value += 75.0;
        } else if (material.name().contains("GOLDEN")) {
            value += 50.0;
        } else if (material.name().contains("IRON")) {
            value += 30.0;
        } else if (material.name().contains("STONE") || material.name().contains("CHAINMAIL")) {
            value += 15.0;
        } else if (material.name().contains("LEATHER") || material.name().contains("WOODEN")) {
            value += 5.0;
        }
        
        // Special items
        if (material == Material.ELYTRA) {
            value += 150.0;
        } else if (material == Material.TRIDENT) {
            value += 120.0;
        } else if (material == Material.TOTEM_OF_UNDYING) {
            value += 200.0;
        } else if (material == Material.NETHER_STAR) {
            value += 250.0;
        }
        
        // Enchantment value
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            Map<Enchantment, Integer> enchants = item.getItemMeta().getEnchants();
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                value += entry.getValue() * 10.0; // Each enchant level = 10 points
            }
        }
        
        // Durability factor
        if (item.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) item.getItemMeta();
            if (item.getType().getMaxDurability() > 0) {
                int maxDurability = item.getType().getMaxDurability();
                int damage = damageable.getDamage();
                double durabilityPercent = (maxDurability - damage) / (double) maxDurability;
                value *= durabilityPercent; // Reduce value for damaged items
            }
        }
        
        // Multiply by stack size for stackable items
        value *= item.getAmount();
        
        return value;
    }
    
    /**
     * Update the total death penalty based on all active graves
     */
    private void updateDeathPenalty(PlayerData playerData) {
        // Clear old graves first (older than 1 hour)
        playerData.clearOldGraves();
        
        double totalPenalty = 0.0;
        
        for (PlayerData.GraveData graveData : playerData.getActiveGraves().values()) {
            totalPenalty += graveData.getPenalty();
        }
        
        playerData.setCurrentDeathPenalty(totalPenalty);
        
        // Trigger progress recalculation
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlayerDataManager().recalculateProgress(playerData.getUUID());
        });
    }
}
