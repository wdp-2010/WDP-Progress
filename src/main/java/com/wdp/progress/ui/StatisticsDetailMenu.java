package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed menu showing all player statistics
 */
public class StatisticsDetailMenu extends DetailMenu {
    
    private final DecimalFormat df = new DecimalFormat("#,###");
    private final DecimalFormat df2 = new DecimalFormat("#.##");
    
    public StatisticsDetailMenu(WDPProgressPlugin plugin, ProgressMenu mainMenu) {
        super(plugin, mainMenu);
    }
    
    @Override
    protected String getMenuTitle(Player target) {
        return ChatColor.YELLOW + "📊 Statistics - " + target.getName();
    }
    
    @Override
    protected ChatColor getCategoryColor() {
        return ChatColor.YELLOW;
    }
    
    @Override
    protected Inventory createInventory(Player viewer, Player target, int page) {
        return createStandardInventory(viewer, target, page);
    }
    
    @Override
    protected List<ItemStack> getDisplayItems(Player target, PlayerData data) {
        List<ItemStack> items = new ArrayList<>();
        
        // Playtime
        items.add(createStatItem(Material.CLOCK, "⏰ Play Time", 
            target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600, "hours"));
        
        // Movement
        items.add(createStatItem(Material.LEATHER_BOOTS, "🚶 Distance Walked", 
            target.getStatistic(Statistic.WALK_ONE_CM) / 100000.0, "km"));
        items.add(createStatItem(Material.DIAMOND_BOOTS, "🏃 Distance Sprinted", 
            target.getStatistic(Statistic.SPRINT_ONE_CM) / 100000.0, "km"));
        items.add(createStatItem(Material.ELYTRA, "🦅 Distance Flown", 
            target.getStatistic(Statistic.AVIATE_ONE_CM) / 100000.0, "km"));
        items.add(createStatItem(Material.OAK_BOAT, "⛵ Distance by Boat", 
            target.getStatistic(Statistic.BOAT_ONE_CM) / 100000.0, "km"));
        
        // Combat
        items.add(createStatItem(Material.DIAMOND_SWORD, "⚔ Mob Kills", 
            target.getStatistic(Statistic.MOB_KILLS), "kills"));
        items.add(createStatItem(Material.SKELETON_SKULL, "💀 Player Kills", 
            target.getStatistic(Statistic.PLAYER_KILLS), "kills"));
        items.add(createStatItem(Material.TOTEM_OF_UNDYING, "☠ Deaths", 
            target.getStatistic(Statistic.DEATHS), "times"));
        
        // Mining
        items.add(createStatItem(Material.DIAMOND_PICKAXE, "⛏ Blocks Mined", 
            getBlocksBroken(target), "blocks"));
        items.add(createStatItem(Material.DIAMOND_ORE, "💎 Diamonds Mined", 
            target.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE), "diamonds"));
        items.add(createStatItem(Material.IRON_ORE, "🔧 Iron Mined", 
            target.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE), "iron"));
        items.add(createStatItem(Material.GOLD_ORE, "🌟 Gold Mined", 
            target.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE), "gold"));
        items.add(createStatItem(Material.ANCIENT_DEBRIS, "🔥 Ancient Debris", 
            target.getStatistic(Statistic.MINE_BLOCK, Material.ANCIENT_DEBRIS), "debris"));
        
        // Building
        items.add(createStatItem(Material.GRASS_BLOCK, "🏗 Blocks Placed", 
            getBlocksPlaced(target), "blocks"));
        
        // Interaction
        items.add(createStatItem(Material.CHEST, "📦 Chests Opened", 
            target.getStatistic(Statistic.CHEST_OPENED), "times"));
        items.add(createStatItem(Material.CRAFTING_TABLE, "🔨 Items Crafted", 
            getTotalCrafted(target), "items"));
        items.add(createStatItem(Material.VILLAGER_SPAWN_EGG, "🤝 Villager Trades", 
            target.getStatistic(Statistic.TRADED_WITH_VILLAGER), "trades"));
        
        // Food
        items.add(createStatItem(Material.COOKED_BEEF, "🍖 Food Eaten", 
            getFoodEaten(target), "items"));
        
        // Damage
        items.add(createStatItem(Material.RED_DYE, "❤ Damage Dealt", 
            target.getStatistic(Statistic.DAMAGE_DEALT) / 2.0, "hearts"));
        items.add(createStatItem(Material.ROSE_BUSH, "💔 Damage Taken", 
            target.getStatistic(Statistic.DAMAGE_TAKEN) / 2.0, "hearts"));
        
        // Special
        items.add(createStatItem(Material.ENDER_PEARL, "🌀 Ender Pearls Thrown", 
            target.getStatistic(Statistic.USE_ITEM, Material.ENDER_PEARL), "pearls"));
        items.add(createStatItem(Material.FISHING_ROD, "🎣 Fish Caught", 
            target.getStatistic(Statistic.FISH_CAUGHT), "fish"));
        items.add(createStatItem(Material.BREWING_STAND, "⚗ Potions Brewed", 
            target.getStatistic(Statistic.BREWINGSTAND_INTERACTION), "potions"));
        
        // Boss kills
        items.add(createMobKillItem(target, EntityType.ENDER_DRAGON, "🐉 Ender Dragons Killed", Material.DRAGON_HEAD));
        items.add(createMobKillItem(target, EntityType.WITHER, "💀 Withers Killed", Material.WITHER_SKELETON_SKULL));
        items.add(createMobKillItem(target, EntityType.WARDEN, "👁 Wardens Killed", Material.SCULK));
        items.add(createMobKillItem(target, EntityType.ELDER_GUARDIAN, "🐠 Elder Guardians Killed", Material.PRISMARINE_CRYSTALS));
        
        return items;
    }
    
    private ItemStack createStatItem(Material material, String name, double value, String unit) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(getCategoryColor() + name);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Value: " + ChatColor.AQUA + df2.format(value) + " " + unit);
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createStatItem(Material material, String name, int value, String unit) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(getCategoryColor() + name);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Value: " + ChatColor.AQUA + df.format(value) + " " + unit);
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createMobKillItem(Player target, EntityType type, String name, Material icon) {
        int kills = target.getStatistic(Statistic.KILL_ENTITY, type);
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(getCategoryColor() + name);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Kills: " + ChatColor.AQUA + df.format(kills));
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private int getBlocksBroken(Player target) {
        int total = 0;
        for (Material mat : Material.values()) {
            if (mat.isBlock()) {
                try {
                    total += target.getStatistic(Statistic.MINE_BLOCK, mat);
                } catch (Exception ignored) {}
            }
        }
        return total;
    }
    
    private int getBlocksPlaced(Player target) {
        int total = 0;
        for (Material mat : Material.values()) {
            if (mat.isBlock()) {
                try {
                    total += target.getStatistic(Statistic.USE_ITEM, mat);
                } catch (Exception ignored) {}
            }
        }
        return total;
    }
    
    private int getTotalCrafted(Player target) {
        int total = 0;
        for (Material mat : Material.values()) {
            try {
                total += target.getStatistic(Statistic.CRAFT_ITEM, mat);
            } catch (Exception ignored) {}
        }
        return total;
    }
    
    private int getFoodEaten(Player target) {
        int total = 0;
        Material[] foods = {
            Material.APPLE, Material.GOLDEN_APPLE, Material.BREAD, Material.COOKED_BEEF,
            Material.COOKED_PORKCHOP, Material.COOKED_CHICKEN, Material.COOKED_COD,
            Material.COOKED_SALMON, Material.COOKED_MUTTON, Material.COOKED_RABBIT,
            Material.BAKED_POTATO, Material.CAKE, Material.COOKIE, Material.MELON_SLICE,
            Material.MUSHROOM_STEW, Material.PUMPKIN_PIE, Material.RABBIT_STEW
        };
        for (Material food : foods) {
            try {
                total += target.getStatistic(Statistic.USE_ITEM, food);
            } catch (Exception ignored) {}
        }
        return total;
    }
}
