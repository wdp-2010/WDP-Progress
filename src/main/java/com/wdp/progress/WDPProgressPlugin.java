package com.wdp.progress;

import com.wdp.progress.api.ProgressAPI;
import com.wdp.progress.commands.ProgressAdminCommand;
import com.wdp.progress.commands.ProgressCommand;
import com.wdp.progress.config.ConfigManager;
import com.wdp.progress.data.DatabaseManager;
import com.wdp.progress.data.PlayerDataManager;
import com.wdp.progress.listeners.*;
import com.wdp.progress.progress.ProgressCalculator;
import com.wdp.progress.integrations.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * WDP Progress Plugin
 * 
 * An advanced player progress tracking system that evaluates player advancement
 * across multiple dimensions of gameplay to produce a comprehensive progress score (1-100).
 * 
 * The progress system considers:
 * - Minecraft Advancements (Story progression, exploration, achievements)
 * - Player Experience (Levels with diminishing returns)
 * - Equipment Quality (Armor, tools, weapons with enchantments and durability)
 * - Economy Status (Money/coins with logarithmic scaling)
 * - Statistics (Mob kills, blocks mined, distance traveled, etc.)
 * - Custom Achievements (Server-specific milestones)
 * 
 * Progress can fluctuate based on player actions (death penalties, lost equipment),
 * but is designed to generally trend upward as players engage with the game.
 * 
 * @author WDP Development Team
 * @version 1.0.0
 */
public class WDPProgressPlugin extends JavaPlugin {
    
    private static WDPProgressPlugin instance;
    
    // Core managers
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private ProgressCalculator progressCalculator;
    
    // Integrations
    private VaultIntegration vaultIntegration;
    
    // API
    private ProgressAPI progressAPI;
    
    // Plugin state
    private boolean enabled = false;
    
    @Override
    public void onEnable() {
        instance = this;
        
        long startTime = System.currentTimeMillis();
        getLogger().info("╔════════════════════════════════════════╗");
        getLogger().info("║   WDP Progress System - Initializing   ║");
        getLogger().info("╚════════════════════════════════════════╝");
        
        try {
            // Initialize configuration
            getLogger().info("Loading configuration...");
            configManager = new ConfigManager(this);
            if (!configManager.loadConfig()) {
                throw new RuntimeException("Failed to load configuration");
            }
            
            // Initialize database
            getLogger().info("Initializing database connection...");
            databaseManager = new DatabaseManager(this);
            if (!databaseManager.connect()) {
                throw new RuntimeException("Failed to connect to database");
            }
            
            // Initialize player data manager
            getLogger().info("Initializing player data manager...");
            playerDataManager = new PlayerDataManager(this);
            
            // Initialize progress calculator
            getLogger().info("Initializing progress calculator...");
            progressCalculator = new ProgressCalculator(this);
            
            // Initialize Vault integration if available
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                getLogger().info("Hooking into Vault...");
                vaultIntegration = new VaultIntegration(this);
                if (vaultIntegration.setupEconomy()) {
                    getLogger().info("Successfully hooked into Vault economy");
                } else {
                    getLogger().warning("Vault found but economy setup failed - economy features disabled");
                }
            } else {
                getLogger().warning("Vault not found - economy features disabled");
            }
            
            // Register event listeners
            getLogger().info("Registering event listeners...");
            registerEventListeners();
            
            // Register commands
            getLogger().info("Registering commands...");
            registerCommands();
            
            // Initialize API
            getLogger().info("Initializing public API...");
            progressAPI = new ProgressAPI(this);
            
            // Load online players' data
            getLogger().info("Loading data for online players...");
            Bukkit.getOnlinePlayers().forEach(player -> 
                playerDataManager.loadPlayerData(player.getUniqueId())
            );
            
            // Start async tasks
            getLogger().info("Starting background tasks...");
            startBackgroundTasks();
            
            enabled = true;
            long loadTime = System.currentTimeMillis() - startTime;
            
            getLogger().info("╔════════════════════════════════════════╗");
            getLogger().info("║   WDP Progress System - Enabled!       ║");
            getLogger().info("║   Loaded in " + loadTime + "ms" + " ".repeat(Math.max(0, 25 - String.valueOf(loadTime).length())) + "║");
            getLogger().info("╚════════════════════════════════════════╝");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable WDP Progress!", e);
            getLogger().severe("Plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        if (!enabled) {
            return;
        }
        
        getLogger().info("Disabling WDP Progress System...");
        
        try {
            // Cancel all tasks
            Bukkit.getScheduler().cancelTasks(this);
            
            // Save all online players' data
            getLogger().info("Saving player data...");
            Bukkit.getOnlinePlayers().forEach(player -> 
                playerDataManager.savePlayerData(player.getUniqueId())
            );
            
            // Close database connections
            if (databaseManager != null) {
                getLogger().info("Closing database connections...");
                databaseManager.disconnect();
            }
            
            getLogger().info("WDP Progress System disabled successfully");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during plugin disable", e);
        }
        
        instance = null;
    }
    
    /**
     * Register all event listeners
     */
    private void registerEventListeners() {
        org.bukkit.plugin.PluginManager pm = Bukkit.getPluginManager();
        
        pm.registerEvents(new PlayerJoinQuitListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new AdvancementListener(this), this);
        pm.registerEvents(new ExperienceListener(this), this);
        pm.registerEvents(new com.wdp.progress.ui.ProgressMenuListener(), this);
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new StatisticsListener(this), this);
    }
    
    /**
     * Register all commands
     */
    private void registerCommands() {
        getCommand("progress").setExecutor(new ProgressCommand(this));
        getCommand("progress").setTabCompleter(new ProgressCommand(this));
        
        getCommand("progressadmin").setExecutor(new ProgressAdminCommand(this));
        getCommand("progressadmin").setTabCompleter(new ProgressAdminCommand(this));
    }
    
    /**
     * Start background tasks for periodic updates
     */
    private void startBackgroundTasks() {
        // Auto-save task - saves all online players' data periodically
        long autoSaveInterval = configManager.getAutoSaveInterval() * 20L; // Convert seconds to ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> 
                playerDataManager.savePlayerData(player.getUniqueId())
            );
        }, autoSaveInterval, autoSaveInterval);
        
        // Progress recalculation task - recalculates progress for online players
        long recalcInterval = configManager.getRecalculationInterval() * 20L;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> 
                playerDataManager.updatePlayerProgress(player.getUniqueId(), true)
            );
        }, recalcInterval, recalcInterval);
    }
    
    /**
     * Reload the plugin configuration and reinitialize components
     */
    public boolean reload() {
        try {
            getLogger().info("Reloading WDP Progress configuration...");
            
            if (!configManager.loadConfig()) {
                getLogger().severe("Failed to reload configuration");
                return false;
            }
            
            progressCalculator.reloadWeights();
            
            getLogger().info("Configuration reloaded successfully");
            return true;
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during reload", e);
            return false;
        }
    }
    
    // Getters
    
    public static WDPProgressPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public ProgressCalculator getProgressCalculator() {
        return progressCalculator;
    }
    
    public VaultIntegration getVaultIntegration() {
        return vaultIntegration;
    }
    
    public ProgressAPI getProgressAPI() {
        return progressAPI;
    }
    
    public boolean isPluginEnabled() {
        return enabled;
    }
}
