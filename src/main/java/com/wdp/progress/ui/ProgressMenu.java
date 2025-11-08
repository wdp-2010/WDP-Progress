package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import com.wdp.progress.progress.ProgressCalculator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interactive GUI menu for viewing player progress
 */
public class ProgressMenu {
    
    private final WDPProgressPlugin plugin;
    private final DecimalFormat df = new DecimalFormat("#.#");
    
    // Detail menus
    private final AdvancementsDetailMenu advancementsDetailMenu;
    private final EquipmentDetailMenu equipmentDetailMenu;
    private final StatisticsDetailMenu statisticsDetailMenu;
    private final EconomyExperienceDetailMenu economyExperienceDetailMenu;
    private final DeathPenaltyDetailMenu deathPenaltyDetailMenu;
    
    public ProgressMenu(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        
        // Initialize detail menus
        this.advancementsDetailMenu = new AdvancementsDetailMenu(plugin, this);
        this.equipmentDetailMenu = new EquipmentDetailMenu(plugin, this);
        this.statisticsDetailMenu = new StatisticsDetailMenu(plugin, this);
        this.economyExperienceDetailMenu = new EconomyExperienceDetailMenu(plugin, this);
        this.deathPenaltyDetailMenu = new DeathPenaltyDetailMenu(plugin, this);
    }
    
    /**
     * Get detail menu handlers
     */
    public AdvancementsDetailMenu getAdvancementsDetailMenu() {
        return advancementsDetailMenu;
    }
    
    public EquipmentDetailMenu getEquipmentDetailMenu() {
        return equipmentDetailMenu;
    }
    
    public StatisticsDetailMenu getStatisticsDetailMenu() {
        return statisticsDetailMenu;
    }
    
    public EconomyExperienceDetailMenu getEconomyExperienceDetailMenu() {
        return economyExperienceDetailMenu;
    }
    
    public DeathPenaltyDetailMenu getDeathPenaltyDetailMenu() {
        return deathPenaltyDetailMenu;
    }
    
    /**
     * Open the main progress menu for a player
     */
    public void openProgressMenu(Player viewer, Player target) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        ProgressCalculator.ProgressResult result = plugin.getProgressCalculator().calculateProgress(target, data);
        
        Inventory inv = Bukkit.createInventory(null, 54, 
            ChatColor.DARK_PURPLE + "⚡ " + ChatColor.GOLD + target.getName() + "'s Progress " + ChatColor.DARK_PURPLE + "⚡");
        
        // Main progress display (center top)
        inv.setItem(4, createMainProgressItem(result));
        
        // Category items
        inv.setItem(10, createAdvancementsItem(result, target));
        inv.setItem(12, createExperienceItem(result, target));
        inv.setItem(14, createEquipmentItem(result, target));
        inv.setItem(16, createEconomyItem(result, target));
        inv.setItem(28, createStatisticsItem(result, target));
        inv.setItem(30, createAchievementsItem(result, data));
        inv.setItem(32, createDeathPenaltyItem(result, data, target));
        inv.setItem(34, createTipsItem(result));
        
        // Information items
        inv.setItem(45, createExplainItem());
        inv.setItem(49, createHistoryItem(target));
        inv.setItem(53, createCloseItem());
        
        // Decorative borders
        fillBorder(inv);
        
        viewer.openInventory(inv);
    }
    
    /**
     * Create main progress display item
     */
    private ItemStack createMainProgressItem(ProgressCalculator.ProgressResult result) {
        double progress = result.getFinalScore();
        Material material = getProgressMaterial(progress);
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        String color = getColorCode(progress);
        String tier = getProgressTier(progress);
        
        meta.setDisplayName(color + "⭐ Overall Progress: " + df.format(progress) + "/100 ⭐");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Progress Tier: " + color + tier);
        lore.add("");
        lore.add(color + createProgressBar(progress));
        lore.add("");
        lore.add(ChatColor.YELLOW + "» Click categories below for details");
        lore.add(ChatColor.YELLOW + "» Hover for tips and information");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create advancements category item
     */
    private ItemStack createAdvancementsItem(ProgressCalculator.ProgressResult result, Player target) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        
        double score = result.getAdvancementsScore();
        double weight = plugin.getConfigManager().getCategoryWeight("advancements");
        double contribution = (score * weight) / 100.0;
        
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "📖 Advancements");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Category Score: " + ChatColor.WHITE + df.format(score) + "/100");
        lore.add(ChatColor.GRAY + "Weight: " + ChatColor.WHITE + weight + "%");
        lore.add(ChatColor.GRAY + "Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "What is this?");
        lore.add(ChatColor.GRAY + "Tracks your story progression through");
        lore.add(ChatColor.GRAY + "Minecraft advancements. Complete the");
        lore.add(ChatColor.GRAY + "main story, explore the Nether and End,");
        lore.add(ChatColor.GRAY + "and unlock achievements.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to improve:");
        lore.add(ChatColor.GREEN + "✓ Complete story advancements");
        lore.add(ChatColor.GREEN + "✓ Enter and explore the Nether");
        lore.add(ChatColor.GREEN + "✓ Find and enter the End dimension");
        lore.add(ChatColor.GREEN + "✓ Defeat the Ender Dragon");
        lore.add(ChatColor.GREEN + "✓ Get the Elytra wings");
        lore.add("");
        lore.add(ChatColor.GOLD + "▶ Click to view detailed advancements!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create experience category item
     */
    private ItemStack createExperienceItem(ProgressCalculator.ProgressResult result, Player target) {
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        
        double score = result.getExperienceScore();
        double weight = plugin.getConfigManager().getCategoryWeight("experience");
        double contribution = (score * weight) / 100.0;
        
        meta.setDisplayName(ChatColor.GREEN + "✨ Experience Levels");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Current Level: " + ChatColor.WHITE + target.getLevel());
        lore.add(ChatColor.GRAY + "Category Score: " + ChatColor.WHITE + df.format(score) + "/100");
        lore.add(ChatColor.GRAY + "Weight: " + ChatColor.WHITE + weight + "%");
        lore.add(ChatColor.GRAY + "Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "What is this?");
        lore.add(ChatColor.GRAY + "Your XP level matters! Higher levels");
        lore.add(ChatColor.GRAY + "show you've invested time gathering");
        lore.add(ChatColor.GRAY + "experience. Uses smart scaling so");
        lore.add(ChatColor.GRAY + "grinding doesn't break the system.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to improve:");
        lore.add(ChatColor.GREEN + "✓ Kill mobs to gain experience");
        lore.add(ChatColor.GREEN + "✓ Mine ores (especially diamonds)");
        lore.add(ChatColor.GREEN + "✓ Breed animals for XP");
        lore.add(ChatColor.GREEN + "✓ Trade with villagers");
        lore.add(ChatColor.GREEN + "✓ Smelt items in furnaces");
        lore.add("");
        lore.add(ChatColor.GRAY + "Note: Uses diminishing returns");
        lore.add("");
        lore.add(ChatColor.GOLD + "▶ Click to view XP details!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create equipment category item
     */
    private ItemStack createEquipmentItem(ProgressCalculator.ProgressResult result, Player target) {
        ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        
        double score = result.getEquipmentScore();
        double weight = plugin.getConfigManager().getCategoryWeight("equipment");
        double contribution = (score * weight) / 100.0;
        
        meta.setDisplayName(ChatColor.AQUA + "⚔ Equipment Quality");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Category Score: " + ChatColor.WHITE + df.format(score) + "/100");
        lore.add(ChatColor.GRAY + "Weight: " + ChatColor.WHITE + weight + "%");
        lore.add(ChatColor.GRAY + "Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "What is this?");
        lore.add(ChatColor.GRAY + "Evaluates your gear quality including");
        lore.add(ChatColor.GRAY + "armor, tools, and weapons. Better");
        lore.add(ChatColor.GRAY + "materials and enchantments increase");
        lore.add(ChatColor.GRAY + "your score. Checks inventory, armor");
        lore.add(ChatColor.GRAY + "slots, and ender chest.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to improve:");
        lore.add(ChatColor.GREEN + "✓ Upgrade to diamond/netherite gear");
        lore.add(ChatColor.GREEN + "✓ Enchant your equipment");
        lore.add(ChatColor.GREEN + "✓ Get Mending enchantment");
        lore.add(ChatColor.GREEN + "✓ Obtain special items (Elytra, Trident)");
        lore.add(ChatColor.GREEN + "✓ Keep gear repaired (durability matters)");
        lore.add(ChatColor.GREEN + "✓ Get a full armor set (15% bonus)");
        lore.add("");
        lore.add(ChatColor.GOLD + "▶ Click to view equipment details!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create economy category item
     */
    private ItemStack createEconomyItem(ProgressCalculator.ProgressResult result, Player target) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        
        double score = result.getEconomyScore();
        double weight = plugin.getConfigManager().getCategoryWeight("economy");
        double contribution = (score * weight) / 100.0;
        
        meta.setDisplayName(ChatColor.GOLD + "💰 Economy & Wealth");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        if (plugin.getVaultIntegration() != null && plugin.getVaultIntegration().hasEconomy()) {
            double balance = plugin.getVaultIntegration().getBalance(target);
            lore.add(ChatColor.GRAY + "Balance: " + ChatColor.WHITE + "$" + df.format(balance));
        }
        
        lore.add(ChatColor.GRAY + "Category Score: " + ChatColor.WHITE + df.format(score) + "/100");
        lore.add(ChatColor.GRAY + "Weight: " + ChatColor.WHITE + weight + "%");
        lore.add(ChatColor.GRAY + "Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "What is this?");
        lore.add(ChatColor.GRAY + "Your money and economic success!");
        lore.add(ChatColor.GRAY + "Earn money through various means.");
        lore.add(ChatColor.GRAY + "Uses smart scaling so the first $100");
        lore.add(ChatColor.GRAY + "is as important as jumping from");
        lore.add(ChatColor.GRAY + "$900 to $1000.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to improve:");
        lore.add(ChatColor.GREEN + "✓ Participate in the economy");
        lore.add(ChatColor.GREEN + "✓ Sell items to other players");
        lore.add(ChatColor.GREEN + "✓ Complete jobs and quests");
        lore.add(ChatColor.GREEN + "✓ Trade valuable resources");
        lore.add(ChatColor.GREEN + "✓ Build and sell services");
        lore.add("");
        lore.add(ChatColor.GOLD + "▶ Click to view economy & XP!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create statistics category item
     */
    private ItemStack createStatisticsItem(ProgressCalculator.ProgressResult result, Player target) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        
        double score = result.getStatisticsScore();
        double weight = plugin.getConfigManager().getCategoryWeight("statistics");
        double contribution = (score * weight) / 100.0;
        
        meta.setDisplayName(ChatColor.YELLOW + "📊 Statistics & Activity");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Category Score: " + ChatColor.WHITE + df.format(score) + "/100");
        lore.add(ChatColor.GRAY + "Weight: " + ChatColor.WHITE + weight + "%");
        lore.add(ChatColor.GRAY + "Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Quick Stats:");
        lore.add(ChatColor.GRAY + "• Playtime: " + ChatColor.WHITE + 
            (target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600) + " hours");
        lore.add(ChatColor.GRAY + "• Mob Kills: " + ChatColor.WHITE + 
            target.getStatistic(Statistic.MOB_KILLS));
        lore.add(ChatColor.GRAY + "• Distance: " + ChatColor.WHITE + 
            (target.getStatistic(Statistic.WALK_ONE_CM) / 100000) + " km");
        lore.add(ChatColor.GRAY + "• Deaths: " + ChatColor.WHITE + 
            target.getStatistic(Statistic.DEATHS));
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to improve:");
        lore.add(ChatColor.GREEN + "✓ Fight and defeat boss mobs");
        lore.add(ChatColor.GREEN + "✓ Mine valuable ores");
        lore.add(ChatColor.GREEN + "✓ Explore new areas");
        lore.add(ChatColor.GREEN + "✓ Play actively");
        lore.add(ChatColor.GREEN + "✓ Avoid dying");
        lore.add("");
        lore.add(ChatColor.GOLD + "▶ Click to view detailed statistics!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create achievements category item
     */
    private ItemStack createAchievementsItem(ProgressCalculator.ProgressResult result, PlayerData data) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        
        double score = result.getAchievementsScore();
        double weight = plugin.getConfigManager().getCategoryWeight("achievements");
        double contribution = (score * weight) / 100.0;
        
        int completed = data.getCompletedAchievements().size();
        int total = plugin.getConfigManager().getCustomAchievements().size();
        
        meta.setDisplayName(ChatColor.YELLOW + "⭐ Custom Achievements");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Completed: " + ChatColor.WHITE + completed + "/" + total);
        lore.add(ChatColor.GRAY + "Category Score: " + ChatColor.WHITE + df.format(score) + "/100");
        lore.add(ChatColor.GRAY + "Weight: " + ChatColor.WHITE + weight + "%");
        lore.add(ChatColor.GRAY + "Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "What is this?");
        lore.add(ChatColor.GRAY + "Server-specific achievements and");
        lore.add(ChatColor.GRAY + "milestones unique to this server.");
        lore.add(ChatColor.GRAY + "Complete special challenges and");
        lore.add(ChatColor.GRAY + "objectives to unlock these.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Your completed achievements:");
        
        if (completed > 0) {
            int shown = 0;
            for (String achievement : data.getCompletedAchievements()) {
                if (shown >= 5) {
                    lore.add(ChatColor.GRAY + "... and " + (completed - 5) + " more");
                    break;
                }
                lore.add(ChatColor.GREEN + "✓ " + achievement);
                shown++;
            }
        } else {
            lore.add(ChatColor.GRAY + "None yet - play to unlock them!");
        }
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create death penalty item
     */
    private ItemStack createDeathPenaltyItem(ProgressCalculator.ProgressResult result, PlayerData data, Player target) {
        ItemStack item = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = item.getItemMeta();
        
        double penalty = result.getDeathPenalty();
        
        meta.setDisplayName(ChatColor.DARK_RED + "💀 Death Penalties");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Total Penalty: " + ChatColor.RED + "-" + df.format(penalty) + " points");
        lore.add("");
        lore.add(ChatColor.YELLOW + "What is this?");
        lore.add(ChatColor.GRAY + "Deaths reduce your progress in");
        lore.add(ChatColor.GRAY + "multiple ways:");
        lore.add("");
        lore.add(ChatColor.RED + "1. Permanent Penalty");
        lore.add(ChatColor.GRAY + "   Each death costs you points");
        lore.add(ChatColor.GRAY + "   that never recover.");
        lore.add("");
        lore.add(ChatColor.GOLD + "2. Temporary Penalty");
        lore.add(ChatColor.GRAY + "   Immediate penalty that slowly");
        lore.add(ChatColor.GRAY + "   recovers over time.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "3. Equipment Loss");
        lore.add(ChatColor.GRAY + "   Lost items reduce equipment");
        lore.add(ChatColor.GRAY + "   score until replaced.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to avoid:");
        lore.add(ChatColor.GREEN + "✓ Be careful in combat");
        lore.add(ChatColor.GREEN + "✓ Wear good armor");
        lore.add(ChatColor.GREEN + "✓ Keep backup gear safe");
        lore.add(ChatColor.GREEN + "✓ Avoid risky situations");
        lore.add("");
        lore.add(ChatColor.GOLD + "▶ Click to view death details!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create tips item
     */
    private ItemStack createTipsItem(ProgressCalculator.ProgressResult result) {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "💡 Tips to Improve");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "Quick tips based on your progress:");
        lore.add("");
        
        // Find lowest category and give specific tip
        String lowestCategory = findLowestCategory(result);
        
        switch (lowestCategory) {
            case "advancements":
                lore.add(ChatColor.GREEN + "► Focus on advancements!");
                lore.add(ChatColor.GRAY + "  Complete the main story line");
                lore.add(ChatColor.GRAY + "  and explore dimensions.");
                break;
            case "experience":
                lore.add(ChatColor.GREEN + "► Gain more XP levels!");
                lore.add(ChatColor.GRAY + "  Kill mobs and mine ores to");
                lore.add(ChatColor.GRAY + "  increase experience.");
                break;
            case "equipment":
                lore.add(ChatColor.GREEN + "► Upgrade your gear!");
                lore.add(ChatColor.GRAY + "  Get better materials and add");
                lore.add(ChatColor.GRAY + "  powerful enchantments.");
                break;
            case "economy":
                lore.add(ChatColor.GREEN + "► Earn more money!");
                lore.add(ChatColor.GRAY + "  Participate in the economy");
                lore.add(ChatColor.GRAY + "  and trade with players.");
                break;
            case "statistics":
                lore.add(ChatColor.GREEN + "► Be more active!");
                lore.add(ChatColor.GRAY + "  Fight mobs, mine blocks, and");
                lore.add(ChatColor.GRAY + "  explore the world.");
                break;
            case "achievements":
                lore.add(ChatColor.GREEN + "► Complete achievements!");
                lore.add(ChatColor.GRAY + "  Unlock server-specific");
                lore.add(ChatColor.GRAY + "  achievements and milestones.");
                break;
        }
        
        lore.add("");
        lore.add(ChatColor.GRAY + "Hover over each category above");
        lore.add(ChatColor.GRAY + "for detailed improvement tips!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create explain item
     */
    private ItemStack createExplainItem() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.AQUA + "❓ How Does This Work?");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "The Progress System Explained:");
        lore.add("");
        lore.add(ChatColor.GRAY + "Your progress score (1-100) shows how");
        lore.add(ChatColor.GRAY + "far you've advanced in the game.");
        lore.add("");
        lore.add(ChatColor.WHITE + "It's calculated from 6 categories:");
        lore.add(ChatColor.LIGHT_PURPLE + "• Advancements " + ChatColor.GRAY + "(25%)");
        lore.add(ChatColor.AQUA + "• Equipment " + ChatColor.GRAY + "(20%)");
        lore.add(ChatColor.GREEN + "• Experience " + ChatColor.GRAY + "(15%)");
        lore.add(ChatColor.GOLD + "• Economy " + ChatColor.GRAY + "(15%)");
        lore.add(ChatColor.RED + "• Statistics " + ChatColor.GRAY + "(15%)");
        lore.add(ChatColor.YELLOW + "• Achievements " + ChatColor.GRAY + "(10%)");
        lore.add("");
        lore.add(ChatColor.GRAY + "Each category has its own score");
        lore.add(ChatColor.GRAY + "(0-100), weighted by importance,");
        lore.add(ChatColor.GRAY + "then combined for your final score.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Progress Tiers:");
        lore.add(ChatColor.RED + "1-20: " + ChatColor.GRAY + "Beginner");
        lore.add(ChatColor.GOLD + "21-40: " + ChatColor.GRAY + "Novice");
        lore.add(ChatColor.YELLOW + "41-60: " + ChatColor.GRAY + "Intermediate");
        lore.add(ChatColor.GREEN + "61-80: " + ChatColor.GRAY + "Advanced");
        lore.add(ChatColor.AQUA + "81-99: " + ChatColor.GRAY + "Expert");
        lore.add(ChatColor.LIGHT_PURPLE + "100: " + ChatColor.GRAY + "Master (very rare!)");
        lore.add("");
        lore.add(ChatColor.GRAY + "Hover over items for details!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create history item
     */
    private ItemStack createHistoryItem(Player target) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.YELLOW + "📊 Progress History");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "View your progress over time");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Coming soon:");
        lore.add(ChatColor.GRAY + "• Progress graph");
        lore.add(ChatColor.GRAY + "• Historical data");
        lore.add(ChatColor.GRAY + "• Trends and statistics");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Create close item
     */
    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "✖ Close Menu");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to close this menu");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Fill border with decorative glass
     */
    private void fillBorder(Inventory inv) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.setDisplayName(" ");
        border.setItemMeta(meta);
        
        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
            if (inv.getItem(i + 45) == null) inv.setItem(i + 45, border);
        }
        
        // Side columns
        for (int i = 9; i < 45; i += 9) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
            if (inv.getItem(i + 8) == null) inv.setItem(i + 8, border);
        }
    }
    
    // Helper methods
    
    private Material getProgressMaterial(double progress) {
        if (progress >= 81) return Material.DIAMOND;
        if (progress >= 61) return Material.EMERALD;
        if (progress >= 41) return Material.GOLD_INGOT;
        if (progress >= 21) return Material.IRON_INGOT;
        return Material.COAL;
    }
    
    private String getColorCode(double progress) {
        if (progress >= 81) return ChatColor.AQUA + "";
        if (progress >= 61) return ChatColor.GREEN + "";
        if (progress >= 41) return ChatColor.YELLOW + "";
        if (progress >= 21) return ChatColor.GOLD + "";
        return ChatColor.RED + "";
    }
    
    private String getProgressTier(double progress) {
        if (progress >= 100) return "Master";
        if (progress >= 81) return "Expert";
        if (progress >= 61) return "Advanced";
        if (progress >= 41) return "Intermediate";
        if (progress >= 21) return "Novice";
        return "Beginner";
    }
    
    private String createProgressBar(double progress) {
        int filled = (int) (progress / 5); // 20 characters for 100%
        int empty = 20 - filled;
        
        StringBuilder bar = new StringBuilder();
        String color = getColorCode(progress);
        
        bar.append(color);
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        bar.append(ChatColor.DARK_GRAY);
        for (int i = 0; i < empty; i++) {
            bar.append("█");
        }
        
        return bar.toString();
    }
    
    private String findLowestCategory(ProgressCalculator.ProgressResult result) {
        double lowest = Double.MAX_VALUE;
        String category = "advancements";
        
        if (result.getAdvancementsScore() < lowest) {
            lowest = result.getAdvancementsScore();
            category = "advancements";
        }
        if (result.getExperienceScore() < lowest) {
            lowest = result.getExperienceScore();
            category = "experience";
        }
        if (result.getEquipmentScore() < lowest) {
            lowest = result.getEquipmentScore();
            category = "equipment";
        }
        if (result.getEconomyScore() < lowest) {
            lowest = result.getEconomyScore();
            category = "economy";
        }
        if (result.getStatisticsScore() < lowest) {
            lowest = result.getStatisticsScore();
            category = "statistics";
        }
        if (result.getAchievementsScore() < lowest) {
            lowest = result.getAchievementsScore();
            category = "achievements";
        }
        
        return category;
    }
}
