package com.wdp.progress.config;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages plugin configuration loading and access.
 * Provides type-safe access to all configuration values.
 */
public class ConfigManager {
    
    private final WDPProgressPlugin plugin;
    private FileConfiguration config;
    
    // Cached configuration values
    private Map<String, Double> categoryWeights;
    private Map<String, Double> advancementCategoryWeights;
    private Map<String, Double> advancementMilestones;
    private Map<String, Double> materialScores;
    private Map<String, Double> enchantmentValues;
    private Map<String, Double> specialItemScores;
    private Map<String, Double> economyMilestones;
    private Map<String, Double> specialMobBonuses;
    private Map<String, Double> valuableBlockBonuses;
    private Map<String, Double> customAchievements;
    private Map<String, Double> equipmentComponentWeights;
    private Map<String, Double> statisticsWeights;
    
    public ConfigManager(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load or reload the configuration file
     */
    public boolean loadConfig() {
        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            config = plugin.getConfig();
            
            // Initialize cache maps
            categoryWeights = new HashMap<>();
            advancementCategoryWeights = new HashMap<>();
            advancementMilestones = new HashMap<>();
            materialScores = new HashMap<>();
            enchantmentValues = new HashMap<>();
            specialItemScores = new HashMap<>();
            economyMilestones = new HashMap<>();
            specialMobBonuses = new HashMap<>();
            valuableBlockBonuses = new HashMap<>();
            customAchievements = new HashMap<>();
            equipmentComponentWeights = new HashMap<>();
            statisticsWeights = new HashMap<>();
            
            // Load all configuration sections
            loadCategoryWeights();
            loadAdvancementConfig();
            loadEquipmentConfig();
            loadEconomyConfig();
            loadStatisticsConfig();
            loadAchievementsConfig();
            
            validateConfiguration();
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load configuration", e);
            return false;
        }
    }
    
    /**
     * Load category weights from configuration
     */
    private void loadCategoryWeights() {
        ConfigurationSection weights = config.getConfigurationSection("weights");
        if (weights != null) {
            for (String key : weights.getKeys(false)) {
                categoryWeights.put(key, weights.getDouble(key));
            }
        }
    }
    
    /**
     * Load advancement configuration
     */
    private void loadAdvancementConfig() {
        ConfigurationSection catWeights = config.getConfigurationSection("advancements.category-weights");
        if (catWeights != null) {
            for (String key : catWeights.getKeys(false)) {
                advancementCategoryWeights.put(key, catWeights.getDouble(key));
            }
        }
        
        ConfigurationSection milestones = config.getConfigurationSection("advancements.milestones");
        if (milestones != null) {
            for (String key : milestones.getKeys(false)) {
                advancementMilestones.put(key, milestones.getDouble(key));
            }
        }
    }
    
    /**
     * Load equipment configuration
     */
    private void loadEquipmentConfig() {
        ConfigurationSection materials = config.getConfigurationSection("equipment.material-scores");
        if (materials != null) {
            for (String key : materials.getKeys(false)) {
                materialScores.put(key.toUpperCase(), materials.getDouble(key));
            }
        }
        
        ConfigurationSection enchants = config.getConfigurationSection("equipment.enchantments.high-value");
        if (enchants != null) {
            for (String key : enchants.getKeys(false)) {
                enchantmentValues.put(key.toUpperCase(), enchants.getDouble(key));
            }
        }
        
        ConfigurationSection special = config.getConfigurationSection("equipment.special-items");
        if (special != null) {
            for (String key : special.getKeys(false)) {
                specialItemScores.put(key.toUpperCase(), special.getDouble(key));
            }
        }
        
        ConfigurationSection components = config.getConfigurationSection("equipment.component-weights");
        if (components != null) {
            for (String key : components.getKeys(false)) {
                equipmentComponentWeights.put(key, components.getDouble(key));
            }
        }
    }
    
    /**
     * Load economy configuration
     */
    private void loadEconomyConfig() {
        ConfigurationSection milestones = config.getConfigurationSection("economy.milestones");
        if (milestones != null) {
            for (String key : milestones.getKeys(false)) {
                economyMilestones.put(key, milestones.getDouble(key));
            }
        }
    }
    
    /**
     * Load statistics configuration
     */
    private void loadStatisticsConfig() {
        ConfigurationSection statWeights = config.getConfigurationSection("statistics.stat-weights");
        if (statWeights != null) {
            for (String key : statWeights.getKeys(false)) {
                statisticsWeights.put(key, statWeights.getDouble(key));
            }
        }
        
        ConfigurationSection specialMobs = config.getConfigurationSection("statistics.mob-kills.special-mobs");
        if (specialMobs != null) {
            for (String key : specialMobs.getKeys(false)) {
                specialMobBonuses.put(key.toUpperCase(), specialMobs.getDouble(key));
            }
        }
        
        ConfigurationSection valuableBlocks = config.getConfigurationSection("statistics.blocks-mined.valuable-blocks");
        if (valuableBlocks != null) {
            for (String key : valuableBlocks.getKeys(false)) {
                valuableBlockBonuses.put(key.toUpperCase(), valuableBlocks.getDouble(key));
            }
        }
    }
    
    /**
     * Load custom achievements configuration
     */
    private void loadAchievementsConfig() {
        ConfigurationSection achievements = config.getConfigurationSection("achievements.custom-achievements");
        if (achievements != null) {
            for (String key : achievements.getKeys(false)) {
                customAchievements.put(key, achievements.getDouble(key));
            }
        }
    }
    
    /**
     * Validate configuration values
     */
    private void validateConfiguration() {
        // Validate category weights sum
        double totalWeight = categoryWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalWeight - 100.0) > 0.01) {
            plugin.getLogger().warning("Category weights sum to " + totalWeight + " instead of 100. Scores may be unexpected.");
        }
    }
    
    // Database getters
    
    public String getDatabaseType() {
        return config.getString("database.type", "SQLITE");
    }
    
    public String getSQLiteFile() {
        return config.getString("database.sqlite.file", "progress_data.db");
    }
    
    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }
    
    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }
    
    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "wdp_progress");
    }
    
    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }
    
    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "password");
    }
    
    // General getters
    
    public int getAutoSaveInterval() {
        return config.getInt("general.auto-save-interval", 300);
    }
    
    public int getRecalculationInterval() {
        return config.getInt("general.recalculation-interval", 60);
    }
    
    public boolean isDebugEnabled() {
        return config.getBoolean("general.debug", false);
    }
    
    public int getMinProgress() {
        return config.getInt("general.min-progress", 1);
    }
    
    public int getMaxProgress() {
        return config.getInt("general.max-progress", 100);
    }
    
    // Weight getters
    
    public double getCategoryWeight(String category) {
        return categoryWeights.getOrDefault(category, 0.0);
    }
    
    public Map<String, Double> getCategoryWeights() {
        return new HashMap<>(categoryWeights);
    }
    
    // Advancement getters
    
    public boolean isAdvancementsEnabled() {
        return config.getBoolean("advancements.enabled", true);
    }
    
    public double getAdvancementCategoryWeight(String category) {
        return advancementCategoryWeights.getOrDefault(category, 0.0);
    }
    
    public Map<String, Double> getAdvancementCategoryWeights() {
        return new HashMap<>(advancementCategoryWeights);
    }
    
    public double getAdvancementMilestoneBonus(String advancement) {
        return advancementMilestones.getOrDefault(advancement, 0.0);
    }
    
    public Map<String, Double> getAdvancementMilestones() {
        return new HashMap<>(advancementMilestones);
    }
    
    // Experience getters
    
    public boolean isExperienceEnabled() {
        return config.getBoolean("experience.enabled", true);
    }
    
    public int getMaxExperienceLevel() {
        return config.getInt("experience.max-level", 100);
    }
    
    public boolean isDiminishingReturnsEnabled() {
        return config.getBoolean("experience.diminishing-returns", true);
    }
    
    public Map<Integer, Double> getExperienceMilestoneLevels() {
        Map<Integer, Double> milestones = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("experience.milestone-levels");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                milestones.put(Integer.parseInt(key), section.getDouble(key));
            }
        }
        return milestones;
    }
    
    // Equipment getters
    
    public boolean isEquipmentEnabled() {
        return config.getBoolean("equipment.enabled", true);
    }
    
    public boolean includeEnderChest() {
        return config.getBoolean("equipment.include-ender-chest", true);
    }
    
    public boolean includeInventory() {
        return config.getBoolean("equipment.include-inventory", true);
    }
    
    public boolean includeArmor() {
        return config.getBoolean("equipment.include-armor", true);
    }
    
    public double getEquipmentComponentWeight(String component) {
        return equipmentComponentWeights.getOrDefault(component, 0.0);
    }
    
    public double getMaterialScore(String material) {
        return materialScores.getOrDefault(material.toUpperCase(), 0.0);
    }
    
    public double getEnchantmentBaseMultiplier() {
        return config.getDouble("equipment.enchantments.base-multiplier", 0.15);
    }
    
    public double getEnchantmentValue(String enchantment) {
        return enchantmentValues.getOrDefault(enchantment.toUpperCase(), 1.0);
    }
    
    public double getSpecialItemScore(String item) {
        return specialItemScores.getOrDefault(item.toUpperCase(), 0.0);
    }
    
    public int getDurabilityMinimumThreshold() {
        return config.getInt("equipment.durability.minimum-threshold", 20);
    }
    
    public double getLowDurabilityPenalty() {
        return config.getDouble("equipment.durability.low-durability-penalty", 0.5);
    }
    
    // Economy getters
    
    public boolean isEconomyEnabled() {
        return config.getBoolean("economy.enabled", true);
    }
    
    public double getEconomyMaxBalance() {
        return config.getDouble("economy.thresholds.max-balance", 1000000.0);
    }
    
    public double getEconomyMilestoneBonus(double balance) {
        double bonus = 0.0;
        for (Map.Entry<String, Double> entry : economyMilestones.entrySet()) {
            double threshold = Double.parseDouble(entry.getKey());
            if (balance >= threshold) {
                bonus = Math.max(bonus, entry.getValue());
            }
        }
        return bonus;
    }
    
    // Statistics getters
    
    public boolean isStatisticsEnabled() {
        return config.getBoolean("statistics.enabled", true);
    }
    
    public double getStatisticWeight(String stat) {
        return statisticsWeights.getOrDefault(stat, 0.0);
    }
    
    public int getMaxMobKills() {
        return config.getInt("statistics.mob-kills.max-kills", 10000);
    }
    
    public double getSpecialMobBonus(String mobType) {
        return specialMobBonuses.getOrDefault(mobType.toUpperCase(), 0.0);
    }
    
    public int getMaxBlocksMined() {
        return config.getInt("statistics.blocks-mined.max-blocks", 100000);
    }
    
    public double getValuableBlockBonus(String blockType) {
        return valuableBlockBonuses.getOrDefault(blockType.toUpperCase(), 0.0);
    }
    
    public long getMaxDistanceTraveled() {
        return config.getLong("statistics.distance-traveled.max-distance", 1000000L);
    }
    
    public int getMaxPlaytimeHours() {
        return config.getInt("statistics.playtime.max-hours", 500);
    }
    
    public double getDeathPenaltyPerDeath() {
        return config.getDouble("statistics.deaths.penalty-per-death", 0.5);
    }
    
    public double getMaxDeathPenalty() {
        return config.getDouble("statistics.deaths.max-penalty", 50.0);
    }
    
    // Achievements getters
    
    public boolean isAchievementsEnabled() {
        return config.getBoolean("achievements.enabled", true);
    }
    
    public double getCustomAchievementPoints(String achievementId) {
        return customAchievements.getOrDefault(achievementId, 0.0);
    }
    
    public Map<String, Double> getCustomAchievements() {
        return new HashMap<>(customAchievements);
    }
    
    // Death penalty getters
    
    public boolean isDeathPenaltyEnabled() {
        return config.getBoolean("death-penalty.enabled", true);
    }
    
    public double getExperienceLossImpact() {
        return config.getDouble("death-penalty.experience-loss-impact", 0.5);
    }
    
    public boolean isTemporaryPenaltyEnabled() {
        return config.getBoolean("death-penalty.temporary-penalty.enabled", true);
    }
    
    public double getTemporaryPenaltyAmount() {
        return config.getDouble("death-penalty.temporary-penalty.amount", 2.0);
    }
    
    public int getTemporaryPenaltyRecoveryTime() {
        return config.getInt("death-penalty.temporary-penalty.recovery-time", 3600);
    }
    
    public boolean isItemLossPenaltyEnabled() {
        return config.getBoolean("death-penalty.item-loss-penalty.enabled", true);
    }
    
    public double getItemLossMultiplier() {
        return config.getDouble("death-penalty.item-loss-penalty.loss-multiplier", 0.3);
    }
    
    // Display getters
    
    public int getProgressBarLength() {
        return config.getInt("display.progress-bar.length", 20);
    }
    
    public String getProgressBarFilledChar() {
        return config.getString("display.progress-bar.filled-char", "█");
    }
    
    public String getProgressBarEmptyChar() {
        return config.getString("display.progress-bar.empty-char", "░");
    }
    
    public String getProgressColor(int progress) {
        if (progress <= 20) return config.getString("display.progress-bar.color-gradient.0-20", "&c");
        if (progress <= 40) return config.getString("display.progress-bar.color-gradient.21-40", "&6");
        if (progress <= 60) return config.getString("display.progress-bar.color-gradient.41-60", "&e");
        if (progress <= 80) return config.getString("display.progress-bar.color-gradient.61-80", "&a");
        return config.getString("display.progress-bar.color-gradient.81-100", "&b");
    }
    
    public boolean showBreakdown() {
        return config.getBoolean("display.show-breakdown", true);
    }
    
    public boolean showTips() {
        return config.getBoolean("display.show-tips", true);
    }
    
    public int getDecimalPlaces() {
        return config.getInt("display.decimal-places", 1);
    }
    
    // Performance getters
    
    public boolean isCachingEnabled() {
        return config.getBoolean("performance.enable-caching", true);
    }
    
    public int getCacheExpiration() {
        return config.getInt("performance.cache-expiration", 600);
    }
    
    public boolean isAsyncCalculationEnabled() {
        return config.getBoolean("performance.async-calculation", true);
    }
    
    public boolean isBatchSavesEnabled() {
        return config.getBoolean("performance.batch-saves", true);
    }
    
    public int getBatchSize() {
        return config.getInt("performance.batch-size", 50);
    }
    
    // API getters
    
    public boolean allowExternalModifications() {
        return config.getBoolean("api.allow-external-modifications", true);
    }
    
    public boolean fireProgressEvents() {
        return config.getBoolean("api.fire-progress-events", true);
    }
    
    public double getEventThreshold() {
        return config.getDouble("api.event-threshold", 0.5);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}
