package com.wdp.progress.commands;

import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.data.PlayerData;
import com.wdp.progress.progress.ProgressCalculator;
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
 * Admin command for progress management
 */
public class ProgressAdminCommand implements CommandExecutor, TabCompleter {
    
    private final WDPProgressPlugin plugin;
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    public ProgressAdminCommand(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("wdp.progress.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                return handleReload(sender);
                
            case "recalculate":
                return handleRecalculate(sender, args);
                
            case "set":
                return handleSet(sender, args);
                
            case "reset":
                return handleReset(sender, args);
                
            case "debug":
                return handleDebug(sender, args);
                
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /progressadmin for help.");
                return true;
        }
    }
    
    /**
     * Handle reload subcommand
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("wdp.progress.admin.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin.");
            return true;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Reloading WDP Progress configuration...");
        
        if (plugin.reload()) {
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to reload configuration. Check console for errors.");
        }
        
        return true;
    }
    
    /**
     * Handle recalculate subcommand
     */
    private boolean handleRecalculate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wdp.progress.admin.recalculate")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to recalculate progress.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /progressadmin recalculate <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Recalculating progress for " + target.getName() + "...");
        
        double newProgress = plugin.getPlayerDataManager().forceRecalculate(target.getUniqueId());
        
        sender.sendMessage(ChatColor.GREEN + "Progress recalculated: " + ChatColor.AQUA + df.format(newProgress) + "/100");
        
        return true;
    }
    
    /**
     * Handle set subcommand
     */
    private boolean handleSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wdp.progress.admin.set")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to set progress.");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /progressadmin set <player> <value>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }
        
        double value;
        try {
            value = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number: " + args[2]);
            return true;
        }
        
        if (plugin.getPlayerDataManager().setProgress(target.getUniqueId(), value)) {
            sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s progress to " + 
                ChatColor.AQUA + df.format(value) + "/100");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to set progress.");
        }
        
        return true;
    }
    
    /**
     * Handle reset subcommand
     */
    private boolean handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wdp.progress.admin.reset")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reset progress.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /progressadmin reset <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Are you sure? This will completely reset " + target.getName() + "'s progress data.");
        sender.sendMessage(ChatColor.YELLOW + "Run the command again within 10 seconds to confirm.");
        
        // TODO: Implement confirmation system with a map and scheduler
        // For now, just reset immediately
        if (plugin.getPlayerDataManager().resetProgress(target.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "Reset " + target.getName() + "'s progress data.");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to reset progress.");
        }
        
        return true;
    }
    
    /**
     * Handle debug subcommand
     */
    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wdp.progress.admin.debug")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to view debug information.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /progressadmin debug <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        ProgressCalculator.ProgressResult result = plugin.getProgressCalculator().calculateProgress(target, data);
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage(ChatColor.AQUA + "       Debug Info: " + ChatColor.WHITE + target.getName());
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "  Current Progress: " + ChatColor.WHITE + df.format(data.getCurrentProgress()));
        sender.sendMessage(ChatColor.YELLOW + "  Last Progress: " + ChatColor.WHITE + df.format(data.getLastProgress()));
        sender.sendMessage(ChatColor.YELLOW + "  Progress Delta: " + ChatColor.WHITE + df.format(data.getProgressDelta()));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "  Category Scores (0-100 within category):");
        sender.sendMessage(ChatColor.GRAY + "    Advancements: " + ChatColor.WHITE + df.format(result.getAdvancementsScore()));
        sender.sendMessage(ChatColor.GRAY + "    Experience: " + ChatColor.WHITE + df.format(result.getExperienceScore()));
        sender.sendMessage(ChatColor.GRAY + "    Equipment: " + ChatColor.WHITE + df.format(result.getEquipmentScore()));
        sender.sendMessage(ChatColor.GRAY + "    Economy: " + ChatColor.WHITE + df.format(result.getEconomyScore()));
        sender.sendMessage(ChatColor.GRAY + "    Statistics: " + ChatColor.WHITE + df.format(result.getStatisticsScore()));
        sender.sendMessage(ChatColor.GRAY + "    Achievements: " + ChatColor.WHITE + df.format(result.getAchievementsScore()));
        sender.sendMessage(ChatColor.GRAY + "    Death Penalty: " + ChatColor.RED + "-" + df.format(result.getDeathPenalty()));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "  Category Weights:");
        sender.sendMessage(ChatColor.GRAY + "    Advancements: " + ChatColor.WHITE + plugin.getConfigManager().getCategoryWeight("advancements") + "%");
        sender.sendMessage(ChatColor.GRAY + "    Experience: " + ChatColor.WHITE + plugin.getConfigManager().getCategoryWeight("experience") + "%");
        sender.sendMessage(ChatColor.GRAY + "    Equipment: " + ChatColor.WHITE + plugin.getConfigManager().getCategoryWeight("equipment") + "%");
        sender.sendMessage(ChatColor.GRAY + "    Economy: " + ChatColor.WHITE + plugin.getConfigManager().getCategoryWeight("economy") + "%");
        sender.sendMessage(ChatColor.GRAY + "    Statistics: " + ChatColor.WHITE + plugin.getConfigManager().getCategoryWeight("statistics") + "%");
        sender.sendMessage(ChatColor.GRAY + "    Achievements: " + ChatColor.WHITE + plugin.getConfigManager().getCategoryWeight("achievements") + "%");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "  Player Level: " + ChatColor.WHITE + target.getLevel());
        sender.sendMessage(ChatColor.YELLOW + "  Completed Achievements: " + ChatColor.WHITE + data.getCompletedAchievements().size());
        sender.sendMessage(ChatColor.YELLOW + "  Last Death: " + ChatColor.WHITE + 
            (data.getLastDeathTime() > 0 ? ((System.currentTimeMillis() - data.getLastDeathTime()) / 1000 + "s ago") : "Never"));
        sender.sendMessage("");
        
        if (plugin.getVaultIntegration() != null && plugin.getVaultIntegration().hasEconomy()) {
            sender.sendMessage(ChatColor.YELLOW + "  Balance: " + ChatColor.WHITE + 
                df.format(plugin.getVaultIntegration().getBalance(target)));
        }
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        return true;
    }
    
    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━ " + ChatColor.AQUA + "WDP Progress Admin" + ChatColor.GOLD + " ━━━━━━━━━");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "/progressadmin reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/progressadmin recalculate <player>" + ChatColor.GRAY + " - Recalculate progress");
        sender.sendMessage(ChatColor.YELLOW + "/progressadmin set <player> <value>" + ChatColor.GRAY + " - Set progress value");
        sender.sendMessage(ChatColor.YELLOW + "/progressadmin reset <player>" + ChatColor.GRAY + " - Reset player data");
        sender.sendMessage(ChatColor.YELLOW + "/progressadmin debug <player>" + ChatColor.GRAY + " - View debug info");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Suggest subcommands
            List<String> subCommands = Arrays.asList("reload", "recalculate", "set", "reset", "debug");
            for (String sub : subCommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
            // Suggest player names
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            // Suggest common values
            completions.addAll(Arrays.asList("1", "25", "50", "75", "100"));
        }
        
        return completions;
    }
}
