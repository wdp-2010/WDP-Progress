package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed menu showing economy and experience information
 */
public class EconomyExperienceDetailMenu extends DetailMenu {
    
    private final DecimalFormat df = new DecimalFormat("#,###.##");
    
    public EconomyExperienceDetailMenu(WDPProgressPlugin plugin, ProgressMenu mainMenu) {
        super(plugin, mainMenu);
    }
    
    @Override
    protected String getMenuTitle(Player target) {
        return ChatColor.GOLD + "💰 Economy & Experience - " + target.getName();
    }
    
    @Override
    protected ChatColor getCategoryColor() {
        return ChatColor.GOLD;
    }
    
    @Override
    protected Inventory createInventory(Player viewer, Player target, int page) {
        return createStandardInventory(viewer, target, page);
    }
    
    @Override
    protected List<ItemStack> getDisplayItems(Player target, PlayerData data) {
        List<ItemStack> items = new ArrayList<>();
        
        // Economy Section
        items.add(createSectionHeader("💰 Economy", Material.GOLD_INGOT));
        
        if (plugin.getVaultIntegration() != null && plugin.getVaultIntegration().hasEconomy()) {
            double balance = plugin.getVaultIntegration().getBalance(target);
            
            items.add(createInfoItem(Material.EMERALD, "Current Balance", 
                "$" + df.format(balance), "Your total money"));
            
            items.add(createInfoItem(Material.GOLD_INGOT, "Wealth Tier", 
                getWealthTier(balance), "Based on your balance"));
            
            items.add(createInfoItem(Material.DIAMOND, "Next Milestone", 
                "$" + df.format(getNextMilestone(balance)), "Keep earning!"));
            
        } else {
            items.add(createErrorItem("Economy system not available"));
        }
        
        // Experience Section
        items.add(createSectionHeader("✨ Experience", Material.EXPERIENCE_BOTTLE));
        
        int level = target.getLevel();
        float exp = target.getExp();
        int totalExp = target.getTotalExperience();
        
        items.add(createInfoItem(Material.EXPERIENCE_BOTTLE, "Current Level", 
            String.valueOf(level), "Your XP level"));
        
        items.add(createInfoItem(Material.ENCHANTING_TABLE, "Progress to Next Level", 
            Math.round(exp * 100) + "%", "Keep grinding!"));
        
        items.add(createInfoItem(Material.BOOK, "Total Experience", 
            df.format(totalExp) + " XP", "Lifetime XP earned"));
        
        items.add(createInfoItem(Material.EMERALD, "Level Tier", 
            getLevelTier(level), "Based on your level"));
        
        items.add(createLevelBreakdownItem(level));
        
        // XP Requirements
        items.add(createSectionHeader("📊 Level Information", Material.WRITABLE_BOOK));
        
        items.add(createXPRequirementItem(level));
        
        return items;
    }
    
    private ItemStack createSectionHeader(String name, Material icon) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "━━━ " + name + " ━━━");
        List<String> lore = new ArrayList<>();
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createInfoItem(Material material, String title, String value, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(getCategoryColor() + title);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Value: " + ChatColor.AQUA + value);
        lore.add(ChatColor.GRAY + description);
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createErrorItem(String message) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + message);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "This feature requires Vault");
        lore.add(ChatColor.GRAY + "and an economy plugin.");
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createLevelBreakdownItem(int level) {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "⭐ Level Breakdown");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Your progression:");
        lore.add("");
        
        if (level >= 100) {
            lore.add(ChatColor.DARK_PURPLE + "✓ Level 100+ " + ChatColor.GOLD + "(Legendary!)");
        } else if (level >= 75) {
            lore.add(ChatColor.AQUA + "✓ Level 75+ " + ChatColor.GOLD + "(Expert!)");
        } else if (level >= 50) {
            lore.add(ChatColor.GREEN + "✓ Level 50+ " + ChatColor.GOLD + "(Advanced!)");
        } else if (level >= 30) {
            lore.add(ChatColor.YELLOW + "✓ Level 30+ " + ChatColor.GOLD + "(Intermediate!)");
        }
        
        if (level >= 30) lore.add(ChatColor.GREEN + "✓ Level 30+ (Enchanting unlocked)");
        if (level >= 50) lore.add(ChatColor.GREEN + "✓ Level 50+ (Expert tier)");
        if (level >= 75) lore.add(ChatColor.GREEN + "✓ Level 75+ (Master tier)");
        if (level >= 100) lore.add(ChatColor.GREEN + "✓ Level 100+ (Legendary tier)");
        
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createXPRequirementItem(int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.AQUA + "📈 XP Requirements");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "XP needed for next level:");
        lore.add(ChatColor.WHITE + "  " + getXPForNextLevel(level) + " XP");
        lore.add("");
        lore.add(ChatColor.GRAY + "Note: XP requirements increase");
        lore.add(ChatColor.GRAY + "as you level up!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private String getWealthTier(double balance) {
        if (balance >= 1000000) return ChatColor.DARK_PURPLE + "Millionaire";
        if (balance >= 500000) return ChatColor.LIGHT_PURPLE + "Ultra Wealthy";
        if (balance >= 100000) return ChatColor.GOLD + "Very Wealthy";
        if (balance >= 50000) return ChatColor.YELLOW + "Wealthy";
        if (balance >= 10000) return ChatColor.GREEN + "Well Off";
        if (balance >= 5000) return ChatColor.AQUA + "Comfortable";
        if (balance >= 1000) return ChatColor.WHITE + "Getting Started";
        return ChatColor.GRAY + "Broke";
    }
    
    private double getNextMilestone(double balance) {
        double[] milestones = {100, 500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000, 5000000};
        for (double milestone : milestones) {
            if (balance < milestone) {
                return milestone;
            }
        }
        return balance * 2; // Double current if past all milestones
    }
    
    private String getLevelTier(int level) {
        if (level >= 100) return ChatColor.DARK_PURPLE + "Legendary";
        if (level >= 75) return ChatColor.LIGHT_PURPLE + "Master";
        if (level >= 50) return ChatColor.AQUA + "Expert";
        if (level >= 30) return ChatColor.GREEN + "Advanced";
        if (level >= 15) return ChatColor.YELLOW + "Intermediate";
        if (level >= 5) return ChatColor.WHITE + "Beginner";
        return ChatColor.GRAY + "Novice";
    }
    
    private int getXPForNextLevel(int level) {
        if (level >= 30) {
            return 9 * level - 158;
        } else if (level >= 15) {
            return 5 * level - 38;
        } else {
            return 2 * level + 7;
        }
    }
}
