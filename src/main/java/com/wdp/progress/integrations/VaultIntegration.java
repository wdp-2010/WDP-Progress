package com.wdp.progress.integrations;

import com.wdp.progress.WDPProgressPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integration with Vault economy API
 */
public class VaultIntegration {
    
    private final WDPProgressPlugin plugin;
    private Economy economy;
    
    public VaultIntegration(WDPProgressPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Setup Vault economy provider
     */
    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        
        economy = rsp.getProvider();
        return economy != null;
    }
    
    /**
     * Check if economy is available
     */
    public boolean hasEconomy() {
        return economy != null;
    }
    
    /**
     * Get player's balance
     */
    public double getBalance(Player player) {
        if (economy == null) {
            return 0.0;
        }
        return economy.getBalance(player);
    }
    
    /**
     * Get economy provider
     */
    public Economy getEconomy() {
        return economy;
    }
}
