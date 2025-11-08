package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed menu showing death penalty information and graves
 */
public class DeathPenaltyDetailMenu extends DetailMenu {
    
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    public DeathPenaltyDetailMenu(WDPProgressPlugin plugin, ProgressMenu mainMenu) {
        super(plugin, mainMenu);
    }
    
    @Override
    protected String getMenuTitle(Player target) {
        return ChatColor.RED + "☠ Death Penalty - " + target.getName();
    }
    
    @Override
    protected ChatColor getCategoryColor() {
        return ChatColor.RED;
    }
    
    @Override
    protected Inventory createInventory(Player viewer, Player target, int page) {
        return createStandardInventory(viewer, target, page);
    }
    
    @Override
    protected List<ItemStack> getDisplayItems(Player target, PlayerData data) {
        List<ItemStack> items = new ArrayList<>();
        
        // Overview
        items.add(createOverviewItem(data));
        
        // Death count
        int deaths = data.getTotalDeaths();
        items.add(createInfoItem(Material.SKELETON_SKULL, "Total Deaths", 
            String.valueOf(deaths), "Times you've died"));
        
        // Current penalty (from ProgressResult)
        double penalty = plugin.getProgressCalculator().calculateProgress(target, data).getDeathPenalty();
        items.add(createInfoItem(Material.REDSTONE, "Current Penalty", 
            "-" + df.format(penalty) + " points", "Applied to your score"));
        
        // GravesX integration status
        if (plugin.getGravesXIntegration() != null) {
            items.add(createGravesXStatusItem(true));
            
            // Active graves information
            if (data.getActiveGraves() != null && !data.getActiveGraves().isEmpty()) {
                items.add(createActiveGravesItem(data.getActiveGraves().size()));
            }
        } else {
            items.add(createGravesXStatusItem(false));
        }
        
        // How penalty works
        items.add(createHowItWorksItem());
        
        // Tips to reduce penalty
        items.add(createTipsItem());
        
        return items;
    }
    
    private ItemStack createOverviewItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + "⚡ Death Penalty Overview");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Deaths: " + ChatColor.RED + data.getTotalDeaths());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Your deaths affect your score!");
        lore.add(ChatColor.GRAY + "But the system is fair - it tracks");
        lore.add(ChatColor.GRAY + "what you actually lost and whether");
        lore.add(ChatColor.GRAY + "you recovered your items.");
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
    
    private ItemStack createGravesXStatusItem(boolean enabled) {
        ItemStack item = new ItemStack(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        
        if (enabled) {
            meta.setDisplayName(ChatColor.GREEN + "✓ GravesX Integration Active");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Smart death tracking enabled!");
            lore.add(ChatColor.GREEN + "✓ Tracks actual item loss");
            lore.add(ChatColor.GREEN + "✓ Monitors grave recovery");
            lore.add(ChatColor.GREEN + "✓ Fair penalty calculation");
            lore.add("");
            meta.setLore(lore);
        } else {
            meta.setDisplayName(ChatColor.RED + "✗ GravesX Not Detected");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Using basic death tracking");
            lore.add(ChatColor.YELLOW + "Install GravesX for smart penalties!");
            lore.add("");
            meta.setLore(lore);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createActiveGravesItem(int count) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.YELLOW + "⚰ Active Graves");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "You have " + ChatColor.WHITE + count + ChatColor.GRAY + " active grave(s)");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Recover your graves to");
        lore.add(ChatColor.YELLOW + "reduce the penalty!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createHowItWorksItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "📖 How Death Penalty Works");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "With GravesX:");
        lore.add(ChatColor.YELLOW + "1. " + ChatColor.WHITE + "Calculates value of lost items");
        lore.add(ChatColor.YELLOW + "2. " + ChatColor.WHITE + "Tracks grave location");
        lore.add(ChatColor.YELLOW + "3. " + ChatColor.WHITE + "Monitors recovery attempts");
        lore.add(ChatColor.YELLOW + "4. " + ChatColor.WHITE + "Reduces penalty over time");
        lore.add("");
        lore.add(ChatColor.GRAY + "Without GravesX:");
        lore.add(ChatColor.YELLOW + "• " + ChatColor.WHITE + "Simple death count penalty");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createTipsItem() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + "💡 Tips to Reduce Penalty");
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "✓ Recover your grave quickly");
        lore.add(ChatColor.GREEN + "✓ Keep valuable items in ender chest");
        lore.add(ChatColor.GREEN + "✓ Use Totem of Undying");
        lore.add(ChatColor.GREEN + "✓ Avoid risky situations");
        lore.add(ChatColor.GREEN + "✓ Get good armor and enchantments");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Remember: The penalty decreases");
        lore.add(ChatColor.YELLOW + "when you recover your items!");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
}
