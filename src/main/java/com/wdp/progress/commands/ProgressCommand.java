package com.wdp.progress.commands;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import com.wdp.progress.progress.ProgressCalculator;
import com.wdp.progress.ui.ProgressMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main /progress command for players to view their progression
 * Supports: /progress, /progress <player>, /progress debug <player>
 */
public class ProgressCommand implements CommandExecutor, TabCompleter {
    
    private final WDPProgressPlugin plugin;
    private final DecimalFormat df;
    private final ProgressMenu menu;
    
    public ProgressCommand(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        int decimalPlaces = plugin.getConfigManager().getDecimalPlaces();
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < decimalPlaces; i++) {
            pattern.append("#");
        }
        this.df = new DecimalFormat(pattern.toString());
        this.menu = new ProgressMenu(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Handle /progress debug <player>
        if (args.length >= 1 && args[0].equalsIgnoreCase("debug")) {
            return handleDebugCommand(sender, args);
        }
        
        // Check if viewing self or another player
        Player target;
        Player viewer;
        
        if (args.length == 0) {
            // View own progress
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console must specify a player name.");
                return true;
            }
            target = (Player) sender;
            viewer = (Player) sender;
        } else {
            // View another player's progress
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console cannot open GUI menus.");
                return true;
            }
            
            viewer = (Player) sender;
            
            if (!sender.hasPermission("wdp.progress.view.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to view other players' progress.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or not online.");
                return true;
            }
        }
        
        // Get player data
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Could not load progress data.");
            return true;
        }
        
                
        // Open the progress menu
        plugin.getProgressMenu().openProgressMenu(viewer, target);
        
        return true;
    }
    
    /**
     * Handle /progress debug <player> command
     */
    private boolean handleDebugCommand(CommandSender sender, String[] args) {
        // Permission check
        if (!sender.hasPermission("wdp.progress.debug")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use debug commands.");
            return true;
        }
        
        // Get target player
        Player target;
        
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /progress debug <player>");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or not online.");
                return true;
            }
        }
        
        // Get player data
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Could not load progress data.");
            return true;
        }
        
        // Calculate current progress
        ProgressCalculator.ProgressResult result = plugin.getProgressCalculator().calculateProgress(target, data);
        
        // Display detailed debug information
        displayDebugInfo(sender, target, result, data);
        
        return true;
    }
    
    /**
     * Display detailed debug information
     */
    private void displayDebugInfo(CommandSender sender, Player target, 
                                  ProgressCalculator.ProgressResult result, PlayerData data) {
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_RED + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage(ChatColor.RED + "       DEBUG: " + ChatColor.WHITE + target.getName() + "'s Progress");
        sender.sendMessage(ChatColor.DARK_RED + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        // Final score
        sender.sendMessage(ChatColor.GOLD + "FINAL SCORE: " + ChatColor.WHITE + df.format(result.getFinalScore()) + "/100");
        sender.sendMessage("");
        
        // Category breakdown
        sender.sendMessage(ChatColor.YELLOW + "CATEGORY BREAKDOWN:");
        sender.sendMessage("");
        
        displayCategoryDebug(sender, "Advancements", result.getAdvancementsScore(), 
            plugin.getConfigManager().getCategoryWeight("advancements"));
        displayCategoryDebug(sender, "Experience", result.getExperienceScore(), 
            plugin.getConfigManager().getCategoryWeight("experience"));
        displayCategoryDebug(sender, "Equipment", result.getEquipmentScore(), 
            plugin.getConfigManager().getCategoryWeight("equipment"));
        displayCategoryDebug(sender, "Economy", result.getEconomyScore(), 
            plugin.getConfigManager().getCategoryWeight("economy"));
        displayCategoryDebug(sender, "Statistics", result.getStatisticsScore(), 
            plugin.getConfigManager().getCategoryWeight("statistics"));
        displayCategoryDebug(sender, "Achievements", result.getAchievementsScore(), 
            plugin.getConfigManager().getCategoryWeight("achievements"));
        
        sender.sendMessage("");
        
        // Death penalty
        double deathPenalty = result.getDeathPenalty();
        sender.sendMessage(ChatColor.RED + "Death Penalty: " + ChatColor.WHITE + "-" + df.format(deathPenalty) + " points");
        sender.sendMessage("");
        
        // Calculation formula
        sender.sendMessage(ChatColor.AQUA + "CALCULATION FORMULA:");
        sender.sendMessage(ChatColor.GRAY + "Final = (Adv×25% + Exp×15% + Equip×20% + Econ×15% + Stats×15% + Ach×10%) - Deaths");
        sender.sendMessage("");
        
        // Detailed stats
        sender.sendMessage(ChatColor.YELLOW + "PLAYER STATISTICS:");
        sender.sendMessage(ChatColor.GRAY + "• Level: " + ChatColor.WHITE + target.getLevel());
        sender.sendMessage(ChatColor.GRAY + "• Total Deaths: " + ChatColor.WHITE + data.getTotalDeaths());
        sender.sendMessage(ChatColor.GRAY + "• Achievements: " + ChatColor.WHITE + data.getCompletedAchievements().size());
        sender.sendMessage(ChatColor.GRAY + "• Last Updated: " + ChatColor.WHITE + 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(data.getLastUpdated())));
        
        if (plugin.getVaultIntegration() != null && plugin.getVaultIntegration().hasEconomy()) {
            double balance = plugin.getVaultIntegration().getBalance(target);
            sender.sendMessage(ChatColor.GRAY + "• Balance: " + ChatColor.WHITE + "$" + df.format(balance));
        }
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_RED + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
    }
    
    /**
     * Display individual category debug info
     */
    private void displayCategoryDebug(CommandSender sender, String name, double score, double weight) {
        double contribution = (score * weight) / 100.0;
        String bar = createSimpleBar(score);
        
        sender.sendMessage(ChatColor.GOLD + name + ":");
        sender.sendMessage(ChatColor.GRAY + "  Score: " + ChatColor.WHITE + df.format(score) + "/100 " + bar);
        sender.sendMessage(ChatColor.GRAY + "  Weight: " + ChatColor.WHITE + weight + "%");
        sender.sendMessage(ChatColor.GRAY + "  Contribution: " + ChatColor.AQUA + "+" + df.format(contribution) + " points");
        sender.sendMessage("");
    }
    
    /**
     * Create a simple progress bar
     */
    private String createSimpleBar(double value) {
        int filled = (int) (value / 10); // 10 characters for 100
        int empty = 10 - filled;
        
        StringBuilder bar = new StringBuilder();
        bar.append(ChatColor.GREEN);
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        bar.append(ChatColor.DARK_GRAY);
        for (int i = 0; i < empty; i++) {
            bar.append("█");
        }
        
        return bar.toString();
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Suggest debug command
            if (sender.hasPermission("wdp.progress.debug") && "debug".startsWith(args[0].toLowerCase())) {
                completions.add("debug");
            }
            
            // Suggest online player names
            if (sender.hasPermission("wdp.progress.view.others")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            // Suggest player names for debug command
            if (sender.hasPermission("wdp.progress.debug")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            }
        }
        
        return completions;
    }
}
