package com.wdp.progress.progress;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.config.ConfigManager;
import com.wdp.progress.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core progress calculation engine.
 * 
 * This class is responsible for calculating a player's overall progress score (1-100)
 * based on multiple weighted categories:
 * 
 * 1. ADVANCEMENTS (25%): Story progression, achievements, exploration
 * 2. EXPERIENCE (15%): Player XP levels with diminishing returns
 * 3. EQUIPMENT (20%): Quality of armor, tools, weapons, enchantments
 * 4. ECONOMY (15%): Money/balance with logarithmic scaling
 * 5. STATISTICS (15%): Combat, mining, exploration metrics
 * 6. ACHIEVEMENTS (10%): Custom server-specific milestones
 * 
 * The calculation is designed to:
 * - Trend upward as players progress through the game
 * - Use diminishing returns to prevent excessive grinding
 * - Consider multiple data sources (inventory, ender chest, statistics)
 * - Apply penalties for deaths and equipment loss
 * - Provide a realistic representation of actual game progression
 */
public class ProgressCalculator {
    
    private final WDPProgressPlugin plugin;
    private final ConfigManager config;
    
    // Cached weights (reloaded on config change)
    private double advancementsWeight;
    private double experienceWeight;
    private double equipmentWeight;
    private double economyWeight;
    private double statisticsWeight;
    private double achievementsWeight;
    
    public ProgressCalculator(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        loadWeights();
    }
    
    /**
     * Load or reload category weights from configuration
     */
    public void reloadWeights() {
        loadWeights();
    }
    
    private void loadWeights() {
        advancementsWeight = config.getCategoryWeight("advancements");
        experienceWeight = config.getCategoryWeight("experience");
        equipmentWeight = config.getCategoryWeight("equipment");
        economyWeight = config.getCategoryWeight("economy");
        statisticsWeight = config.getCategoryWeight("statistics");
        achievementsWeight = config.getCategoryWeight("achievements");
    }
    
    /**
     * Calculate the overall progress score for a player.
     * This is the main entry point for progress calculation.
     * 
     * @param player The player to calculate progress for
     * @param playerData The player's stored data
     * @return ProgressResult containing the final score and breakdown
     */
    public ProgressResult calculateProgress(Player player, PlayerData playerData) {
        ProgressResult result = new ProgressResult();
        
        try {
            // Calculate each category score (0-100 scale within that category)
            if (config.isAdvancementsEnabled()) {
                double advScore = calculateAdvancementScore(player);
                result.setAdvancementsScore(advScore);
                result.addToTotal(advScore * (advancementsWeight / 100.0));
            }
            
            if (config.isExperienceEnabled()) {
                double expScore = calculateExperienceScore(player);
                result.setExperienceScore(expScore);
                result.addToTotal(expScore * (experienceWeight / 100.0));
            }
            
            if (config.isEquipmentEnabled()) {
                double eqScore = calculateEquipmentScore(player);
                result.setEquipmentScore(eqScore);
                result.addToTotal(eqScore * (equipmentWeight / 100.0));
            }
            
            if (config.isEconomyEnabled() && plugin.getVaultIntegration() != null) {
                double econScore = calculateEconomyScore(player);
                result.setEconomyScore(econScore);
                result.addToTotal(econScore * (economyWeight / 100.0));
            }
            
            if (config.isStatisticsEnabled()) {
                double statScore = calculateStatisticsScore(player);
                result.setStatisticsScore(statScore);
                result.addToTotal(statScore * (statisticsWeight / 100.0));
            }
            
            if (config.isAchievementsEnabled()) {
                double achScore = calculateAchievementsScore(playerData);
                result.setAchievementsScore(achScore);
                result.addToTotal(achScore * (achievementsWeight / 100.0));
            }
            
            // Apply death penalties
            if (config.isDeathPenaltyEnabled()) {
                double deathPenalty = calculateDeathPenalty(player, playerData);
                result.setDeathPenalty(deathPenalty);
                result.addToTotal(-deathPenalty);
            }
            
            // Clamp to configured min/max
            double finalScore = Math.max(config.getMinProgress(), 
                                Math.min(config.getMaxProgress(), result.getTotalScore()));
            
            result.setFinalScore(finalScore);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error calculating progress for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            result.setFinalScore(config.getMinProgress());
        }
        
        return result;
    }
    
    /**
     * Calculate advancement progression score (0-100)
     * 
     * Analyzes completed advancements across different categories:
     * - Story progression (main game flow)
     * - Nether exploration
     * - End dimension
     * - Adventure achievements
     * - Husbandry (farming, animals)
     * 
     * Key milestones (like killing the dragon) provide bonus points.
     */
    private double calculateAdvancementScore(Player player) {
        Map<String, Double> categoryScores = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();
        Map<String, Integer> categoryTotals = new HashMap<>();
        
        // Initialize categories
        for (String category : config.getAdvancementCategoryWeights().keySet()) {
            categoryScores.put(category, 0.0);
            categoryCounts.put(category, 0);
            categoryTotals.put(category, 0);
        }
        
        int totalCompleted = 0;
        int totalAdvancements = 0;
        double milestoneBonus = 0.0;
        
        // Iterate through all advancements
        Iterator<Advancement> advIterator = Bukkit.getServer().advancementIterator();
        while (advIterator.hasNext()) {
            Advancement advancement = advIterator.next();
            String key = advancement.getKey().toString();
            
            // Skip recipe advancements
            if (key.contains("recipes/")) {
                continue;
            }
            
            totalAdvancements++;
            
            // Determine category
            String category = determineAdvancementCategory(key);
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0) + 1);
            
            // Check if completed
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (progress.isDone()) {
                totalCompleted++;
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                
                // Check for milestone bonus
                double bonus = config.getAdvancementMilestoneBonus(key);
                if (bonus > 0) {
                    milestoneBonus += bonus;
                }
            }
        }
        
        // Calculate category scores
        double totalCategoryScore = 0.0;
        double totalCategoryWeight = 0.0;
        
        for (String category : categoryScores.keySet()) {
            int completed = categoryCounts.getOrDefault(category, 0);
            int total = categoryTotals.getOrDefault(category, 1); // Avoid division by zero
            
            double categoryPercent = (double) completed / total * 100.0;
            double weight = config.getAdvancementCategoryWeight(category);
            
            totalCategoryScore += categoryPercent * weight;
            totalCategoryWeight += weight;
        }
        
        // Normalize to 0-100 scale
        double baseScore = totalCategoryWeight > 0 ? totalCategoryScore / totalCategoryWeight : 0.0;
        
        // Add milestone bonus (capped at 20% of total score)
        double bonusScore = Math.min(milestoneBonus, 20.0);
        
        return Math.min(100.0, baseScore + bonusScore);
    }
    
    /**
     * Determine which category an advancement belongs to
     */
    private String determineAdvancementCategory(String key) {
        if (key.contains("story/") || key.contains("minecraft:story")) {
            return "story";
        } else if (key.contains("nether/") || key.contains("minecraft:nether")) {
            return "nether";
        } else if (key.contains("end/") || key.contains("minecraft:end")) {
            return "end";
        } else if (key.contains("adventure/") || key.contains("minecraft:adventure")) {
            return "adventure";
        } else if (key.contains("husbandry/") || key.contains("minecraft:husbandry")) {
            return "husbandry";
        }
        return "other";
    }
    
    /**
     * Calculate experience level score (0-100)
     * 
     * Uses logarithmic scaling to implement diminishing returns.
     * Higher levels require exponentially more XP but provide
     * logarithmically less progress benefit.
     * 
     * This prevents players from grinding to level 1000 for max progress.
     */
    private double calculateExperienceScore(Player player) {
        int level = player.getLevel();
        int maxLevel = config.getMaxExperienceLevel();
        
        if (level <= 0) {
            return 0.0;
        }
        
        double baseScore;
        
        if (config.isDiminishingReturnsEnabled()) {
            // Logarithmic scaling: log(level + 1) / log(maxLevel + 1)
            baseScore = (Math.log(level + 1) / Math.log(maxLevel + 1)) * 100.0;
        } else {
            // Linear scaling
            baseScore = Math.min(100.0, (double) level / maxLevel * 100.0);
        }
        
        // Check for milestone bonuses
        double milestoneBonus = 0.0;
        Map<Integer, Double> milestones = config.getExperienceMilestoneLevels();
        for (Map.Entry<Integer, Double> entry : milestones.entrySet()) {
            if (level >= entry.getKey()) {
                milestoneBonus = Math.max(milestoneBonus, entry.getValue());
            }
        }
        
        return Math.min(100.0, baseScore + milestoneBonus);
    }
    
    /**
     * Calculate equipment quality score (0-100)
     * 
     * Evaluates:
     * - Armor (helmet, chestplate, leggings, boots)
     * - Tools (pickaxe, axe, shovel, hoe)
     * - Weapons (sword, bow, crossbow, trident)
     * - Special items (elytra, shield, totems)
     * 
     * Considers:
     * - Material tier (netherite > diamond > iron > etc.)
     * - Enchantments (type, level, value)
     * - Durability (damaged items worth less)
     * - Item rarity and special properties
     * 
     * Checks:
     * - Equipped armor
     * - Main inventory
     * - Ender chest (configurable)
     */
    private double calculateEquipmentScore(Player player) {
        Map<String, Double> componentScores = new HashMap<>();
        Map<String, Double> componentMaxScores = new HashMap<>();
        
        // Initialize component maps
        for (String component : Arrays.asList("armor", "tools", "weapons", "special")) {
            componentScores.put(component, 0.0);
            componentMaxScores.put(component, 100.0);
        }
        
        // Evaluate equipped armor
        if (config.includeArmor()) {
            double armorScore = evaluateArmor(player);
            componentScores.put("armor", armorScore);
        }
        
        // Evaluate inventory
        if (config.includeInventory()) {
            evaluateInventory(player.getInventory().getContents(), componentScores);
        }
        
        // Evaluate ender chest
        if (config.includeEnderChest()) {
            evaluateInventory(player.getEnderChest().getContents(), componentScores);
        }
        
        // Calculate weighted average
        double totalScore = 0.0;
        double totalWeight = 0.0;
        
        for (String component : componentScores.keySet()) {
            double score = Math.min(100.0, componentScores.get(component));
            double weight = config.getEquipmentComponentWeight(component);
            
            totalScore += score * weight;
            totalWeight += weight;
        }
        
        return totalWeight > 0 ? totalScore / totalWeight : 0.0;
    }
    
    /**
     * Evaluate equipped armor pieces
     */
    private double evaluateArmor(Player player) {
        double totalScore = 0.0;
        int pieces = 0;
        
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                totalScore += evaluateItem(armor, "armor");
                pieces++;
            }
        }
        
        // Full set bonus
        if (pieces == 4) {
            totalScore *= 1.15; // 15% bonus for full armor set
        }
        
        return totalScore;
    }
    
    /**
     * Evaluate items in an inventory
     */
    private void evaluateInventory(ItemStack[] contents, Map<String, Double> componentScores) {
        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            
            String component = categorizeItem(item);
            if (component != null) {
                double score = evaluateItem(item, component);
                componentScores.put(component, componentScores.get(component) + score);
            }
        }
    }
    
    /**
     * Categorize an item into a component type
     */
    private String categorizeItem(ItemStack item) {
        String typeName = item.getType().name();
        
        // Check for special items first
        double specialScore = config.getSpecialItemScore(typeName);
        if (specialScore > 0) {
            return "special";
        }
        
        // Armor
        if (typeName.endsWith("_HELMET") || typeName.endsWith("_CHESTPLATE") || 
            typeName.endsWith("_LEGGINGS") || typeName.endsWith("_BOOTS")) {
            return "armor";
        }
        
        // Tools
        if (typeName.endsWith("_PICKAXE") || typeName.endsWith("_AXE") || 
            typeName.endsWith("_SHOVEL") || typeName.endsWith("_HOE")) {
            return "tools";
        }
        
        // Weapons
        if (typeName.endsWith("_SWORD") || typeName.equals("BOW") || 
            typeName.equals("CROSSBOW") || typeName.equals("TRIDENT")) {
            return "weapons";
        }
        
        // Special
        if (typeName.equals("ELYTRA") || typeName.equals("SHIELD") || 
            typeName.equals("TOTEM_OF_UNDYING")) {
            return "special";
        }
        
        return null;
    }
    
    /**
     * Evaluate a single item's value
     */
    private double evaluateItem(ItemStack item, String component) {
        if (item == null || item.getType() == Material.AIR) {
            return 0.0;
        }
        
        // Check for special item score
        double specialScore = config.getSpecialItemScore(item.getType().name());
        if (specialScore > 0) {
            return specialScore;
        }
        
        // Get material base score
        String materialType = extractMaterialType(item.getType().name());
        double baseScore = config.getMaterialScore(materialType);
        
        if (baseScore == 0.0) {
            return 0.0;
        }
        
        // Apply enchantment multiplier
        double enchantmentMultiplier = 1.0;
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            enchantmentMultiplier = calculateEnchantmentMultiplier(item);
        }
        
        baseScore *= enchantmentMultiplier;
        
        // Apply durability factor
        if (item.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) item.getItemMeta();
            if (item.getType().getMaxDurability() > 0) {
                int maxDurability = item.getType().getMaxDurability();
                int damage = damageable.getDamage();
                int currentDurability = maxDurability - damage;
                double durabilityPercent = (double) currentDurability / maxDurability * 100.0;
                
                if (durabilityPercent < config.getDurabilityMinimumThreshold()) {
                    baseScore *= config.getLowDurabilityPenalty();
                }
            }
        }
        
        return baseScore;
    }
    
    /**
     * Extract material type from item name (e.g., "DIAMOND" from "DIAMOND_SWORD")
     */
    private String extractMaterialType(String itemName) {
        if (itemName.startsWith("NETHERITE_")) return "NETHERITE";
        if (itemName.startsWith("DIAMOND_")) return "DIAMOND";
        if (itemName.startsWith("IRON_")) return "IRON";
        if (itemName.startsWith("GOLDEN_")) return "GOLDEN";
        if (itemName.startsWith("STONE_")) return "STONE";
        if (itemName.startsWith("WOODEN_") || itemName.startsWith("WOOD_")) return "WOOD";
        if (itemName.startsWith("LEATHER_")) return "LEATHER";
        if (itemName.startsWith("CHAINMAIL_")) return "CHAINMAIL";
        return "UNKNOWN";
    }
    
    /**
     * Calculate enchantment multiplier for an item
     */
    private double calculateEnchantmentMultiplier(ItemStack item) {
        double multiplier = 1.0;
        double baseMultiplier = config.getEnchantmentBaseMultiplier();
        
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            
            double enchantValue = config.getEnchantmentValue(enchant.getKey().getKey().toUpperCase());
            multiplier += (baseMultiplier * level * enchantValue);
        }
        
        return multiplier;
    }
    
    /**
     * Calculate economy score (0-100)
     * 
     * Uses logarithmic scaling to prevent excessive money grinding.
     * Formula: (log10(balance) / log10(maxBalance)) * 100
     * 
     * This means:
     * - $100 = ~20% progress
     * - $1,000 = ~30% progress
     * - $10,000 = ~40% progress
     * - $100,000 = ~50% progress
     * - $1,000,000 = 100% progress (max)
     */
    private double calculateEconomyScore(Player player) {
        if (plugin.getVaultIntegration() == null || !plugin.getVaultIntegration().hasEconomy()) {
            return 0.0;
        }
        
        double balance = plugin.getVaultIntegration().getBalance(player);
        
        if (balance <= 0) {
            return 0.0;
        }
        
        double maxBalance = config.getEconomyMaxBalance();
        
        // Logarithmic scaling
        double baseScore = (Math.log10(balance) / Math.log10(maxBalance)) * 100.0;
        baseScore = Math.max(0.0, Math.min(100.0, baseScore));
        
        // Add milestone bonuses
        double milestoneBonus = config.getEconomyMilestoneBonus(balance);
        
        return Math.min(100.0, baseScore + milestoneBonus);
    }
    
    /**
     * Calculate statistics score (0-100)
     * 
     * Evaluates multiple gameplay statistics:
     * - Mob kills (especially rare/boss mobs)
     * - Blocks mined (especially valuable ores)
     * - Distance traveled (exploration)
     * - Playtime (with diminishing returns)
     * - Death penalty
     */
    private double calculateStatisticsScore(Player player) {
        Map<String, Double> statScores = new HashMap<>();
        
        // Mob kills
        double mobKillScore = calculateMobKillScore(player);
        statScores.put("mob-kills", mobKillScore);
        
        // Blocks mined
        double blockMineScore = calculateBlockMineScore(player);
        statScores.put("blocks-mined", blockMineScore);
        
        // Distance traveled
        double distanceScore = calculateDistanceScore(player);
        statScores.put("distance-traveled", distanceScore);
        
        // Playtime
        double playtimeScore = calculatePlaytimeScore(player);
        statScores.put("playtime", playtimeScore);
        
        // Calculate weighted average
        double totalScore = 0.0;
        double totalWeight = 0.0;
        
        for (Map.Entry<String, Double> entry : statScores.entrySet()) {
            double score = entry.getValue();
            double weight = config.getStatisticWeight(entry.getKey());
            
            if (weight > 0) {
                totalScore += score * weight;
                totalWeight += weight;
            }
        }
        
        return totalWeight > 0 ? totalScore / totalWeight : 0.0;
    }
    
    /**
     * Calculate mob kill score
     */
    private double calculateMobKillScore(Player player) {
        int totalKills = 0;
        double bonusPoints = 0.0;
        
        // Count all hostile mob kills
        for (EntityType entityType : EntityType.values()) {
            if (entityType.isAlive()) {
                try {
                    int kills = player.getStatistic(Statistic.KILL_ENTITY, entityType);
                    totalKills += kills;
                    
                    // Check for special mob bonuses
                    double bonus = config.getSpecialMobBonus(entityType.name());
                    if (bonus > 0) {
                        bonusPoints += (kills * bonus);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Entity type doesn't support this statistic
                }
            }
        }
        
        // Calculate base score with diminishing returns
        int maxKills = config.getMaxMobKills();
        double baseScore = Math.min(100.0, (Math.sqrt(totalKills) / Math.sqrt(maxKills)) * 100.0);
        
        // Add bonus points (capped at 25% of total)
        double bonusScore = Math.min(25.0, bonusPoints * 0.1);
        
        return Math.min(100.0, baseScore + bonusScore);
    }
    
    /**
     * Calculate block mining score
     */
    private double calculateBlockMineScore(Player player) {
        int totalBlocks = 0;
        double bonusPoints = 0.0;
        
        // Count all blocks mined
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                try {
                    int mined = player.getStatistic(Statistic.MINE_BLOCK, material);
                    totalBlocks += mined;
                    
                    // Check for valuable block bonuses
                    double bonus = config.getValuableBlockBonus(material.name());
                    if (bonus > 0) {
                        bonusPoints += (mined * bonus);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Material doesn't support this statistic
                }
            }
        }
        
        // Calculate base score with diminishing returns
        int maxBlocks = config.getMaxBlocksMined();
        double baseScore = Math.min(100.0, (Math.sqrt(totalBlocks) / Math.sqrt(maxBlocks)) * 100.0);
        
        // Add bonus points (capped at 20% of total)
        double bonusScore = Math.min(20.0, bonusPoints * 0.05);
        
        return Math.min(100.0, baseScore + bonusScore);
    }
    
    /**
     * Calculate distance traveled score
     */
    private double calculateDistanceScore(Player player) {
        long totalDistance = 0;
        
        // Sum all movement types
        try {
            totalDistance += player.getStatistic(Statistic.WALK_ONE_CM);
            totalDistance += player.getStatistic(Statistic.SPRINT_ONE_CM);
            totalDistance += player.getStatistic(Statistic.CROUCH_ONE_CM);
            totalDistance += player.getStatistic(Statistic.SWIM_ONE_CM);
            totalDistance += player.getStatistic(Statistic.FLY_ONE_CM);
            totalDistance += player.getStatistic(Statistic.BOAT_ONE_CM);
            totalDistance += player.getStatistic(Statistic.MINECART_ONE_CM);
            totalDistance += player.getStatistic(Statistic.HORSE_ONE_CM);
            totalDistance += player.getStatistic(Statistic.PIG_ONE_CM);
            totalDistance += player.getStatistic(Statistic.AVIATE_ONE_CM);
        } catch (Exception ignored) {}
        
        // Convert cm to blocks
        long distanceBlocks = totalDistance / 100;
        
        long maxDistance = config.getMaxDistanceTraveled();
        
        // Logarithmic scaling for exploration
        return Math.min(100.0, (Math.log(distanceBlocks + 1) / Math.log(maxDistance + 1)) * 100.0);
    }
    
    /**
     * Calculate playtime score
     */
    private double calculatePlaytimeScore(Player player) {
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        double playTimeHours = playTimeTicks / 20.0 / 60.0 / 60.0; // Convert ticks to hours
        
        int maxHours = config.getMaxPlaytimeHours();
        
        // Logarithmic scaling with diminishing returns
        return Math.min(100.0, (Math.log(playTimeHours + 1) / Math.log(maxHours + 1)) * 100.0);
    }
    
    /**
     * Calculate custom achievements score (0-100)
     */
    private double calculateAchievementsScore(PlayerData playerData) {
        Set<String> completedAchievements = playerData.getCompletedAchievements();
        Map<String, Double> allAchievements = config.getCustomAchievements();
        
        if (allAchievements.isEmpty()) {
            return 0.0;
        }
        
        double earnedPoints = 0.0;
        double totalPoints = allAchievements.values().stream().mapToDouble(Double::doubleValue).sum();
        
        for (String achievement : completedAchievements) {
            double points = config.getCustomAchievementPoints(achievement);
            earnedPoints += points;
        }
        
        return Math.min(100.0, (earnedPoints / totalPoints) * 100.0);
    }
    
    /**
     * Calculate death penalty
     */
    private double calculateDeathPenalty(Player player, PlayerData playerData) {
        double totalPenalty = 0.0;
        
        // Death count penalty
        int deaths = player.getStatistic(Statistic.DEATHS);
        double deathPenalty = deaths * config.getDeathPenaltyPerDeath();
        deathPenalty = Math.min(config.getMaxDeathPenalty(), deathPenalty);
        totalPenalty += deathPenalty;
        
        // Temporary death penalty (if applicable)
        if (config.isTemporaryPenaltyEnabled()) {
            long lastDeath = playerData.getLastDeathTime();
            int recoveryTime = config.getTemporaryPenaltyRecoveryTime();
            long timeSinceDeath = (System.currentTimeMillis() - lastDeath) / 1000;
            
            if (timeSinceDeath < recoveryTime) {
                double tempPenalty = config.getTemporaryPenaltyAmount();
                // Linear recovery
                double recoveryPercent = (double) timeSinceDeath / recoveryTime;
                tempPenalty *= (1.0 - recoveryPercent);
                totalPenalty += tempPenalty;
            }
        }
        
        return totalPenalty;
    }
    
    /**
     * Result class containing progress breakdown
     */
    public static class ProgressResult {
        private double totalScore = 0.0;
        private double finalScore = 0.0;
        private double advancementsScore = 0.0;
        private double experienceScore = 0.0;
        private double equipmentScore = 0.0;
        private double economyScore = 0.0;
        private double statisticsScore = 0.0;
        private double achievementsScore = 0.0;
        private double deathPenalty = 0.0;
        
        public void addToTotal(double value) {
            this.totalScore += value;
        }
        
        // Getters and setters
        public double getTotalScore() { return totalScore; }
        public double getFinalScore() { return finalScore; }
        public double getAdvancementsScore() { return advancementsScore; }
        public double getExperienceScore() { return experienceScore; }
        public double getEquipmentScore() { return equipmentScore; }
        public double getEconomyScore() { return economyScore; }
        public double getStatisticsScore() { return statisticsScore; }
        public double getAchievementsScore() { return achievementsScore; }
        public double getDeathPenalty() { return deathPenalty; }
        
        public void setFinalScore(double finalScore) { this.finalScore = finalScore; }
        public void setAdvancementsScore(double advancementsScore) { this.advancementsScore = advancementsScore; }
        public void setExperienceScore(double experienceScore) { this.experienceScore = experienceScore; }
        public void setEquipmentScore(double equipmentScore) { this.equipmentScore = equipmentScore; }
        public void setEconomyScore(double economyScore) { this.economyScore = economyScore; }
        public void setStatisticsScore(double statisticsScore) { this.statisticsScore = statisticsScore; }
        public void setAchievementsScore(double achievementsScore) { this.achievementsScore = achievementsScore; }
        public void setDeathPenalty(double deathPenalty) { this.deathPenalty = deathPenalty; }
    }
}
