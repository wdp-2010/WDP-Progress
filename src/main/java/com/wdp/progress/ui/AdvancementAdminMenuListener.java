package com.wdp.progress.ui;

import com.wdp.progress.WDPProgressPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles click events in the Advancement Admin Menu
 */
public class AdvancementAdminMenuListener implements Listener {
    
    private final WDPProgressPlugin plugin;
    private final AdvancementAdminMenu menu;
    
    public AdvancementAdminMenuListener(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        this.menu = new AdvancementAdminMenu(plugin);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player admin = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Check if this is an advancement admin menu
        if (!title.startsWith("§8§l⚙ §6Advancements:")) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }
        
        // Get menu state from metadata
        if (!admin.hasMetadata("wdp_adv_menu_target")) {
            return;
        }
        
        String targetName = admin.getMetadata("wdp_adv_menu_target").get(0).asString();
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            admin.sendMessage("§c§l✘ §cTarget player is no longer online!");
            admin.closeInventory();
            return;
        }
        
        String categoryStr = admin.getMetadata("wdp_adv_menu_category").get(0).asString();
        String filterStr = admin.getMetadata("wdp_adv_menu_filter").get(0).asString();
        int page = admin.getMetadata("wdp_adv_menu_page").get(0).asInt();
        
        AdvancementAdminMenu.Category category = AdvancementAdminMenu.Category.valueOf(categoryStr);
        AdvancementAdminMenu.FilterMode filter = AdvancementAdminMenu.FilterMode.valueOf(filterStr);
        
        int slot = event.getSlot();
        
        // Handle control panel clicks (bottom row)
        if (slot >= 45) {
            handleControlPanelClick(admin, target, category, filter, page, slot, event.getClick());
            return;
        }
        
        // Handle navigation
        if (slot == 18 || slot == 26) {
            // Navigation handled by control panel
            return;
        }
        
        // Handle advancement click (slots 0-44)
        if (slot < 45) {
            handleAdvancementClick(admin, target, clicked);
        }
    }
    
    /**
     * Handle clicks on control panel items
     */
    private void handleControlPanelClick(Player admin, Player target, 
                                         AdvancementAdminMenu.Category category, 
                                         AdvancementAdminMenu.FilterMode filter, 
                                         int page, int slot, ClickType clickType) {
        // Category buttons (45-50)
        if (slot >= 45 && slot <= 50) {
            AdvancementAdminMenu.Category newCategory = switch (slot) {
                case 45 -> AdvancementAdminMenu.Category.ALL;
                case 46 -> AdvancementAdminMenu.Category.STORY;
                case 47 -> AdvancementAdminMenu.Category.NETHER;
                case 48 -> AdvancementAdminMenu.Category.END;
                case 49 -> AdvancementAdminMenu.Category.ADVENTURE;
                case 50 -> AdvancementAdminMenu.Category.HUSBANDRY;
                default -> category;
            };
            
            menu.openMenu(admin, target, newCategory, filter, 0);
            return;
        }
        
        // Filter button (51)
        if (slot == 51) {
            AdvancementAdminMenu.FilterMode newFilter = switch (filter) {
                case ALL -> AdvancementAdminMenu.FilterMode.COMPLETED;
                case COMPLETED -> AdvancementAdminMenu.FilterMode.INCOMPLETE;
                case INCOMPLETE -> AdvancementAdminMenu.FilterMode.ALL;
            };
            
            menu.openMenu(admin, target, category, newFilter, 0);
            return;
        }
        
        // Bulk actions (52)
        if (slot == 52) {
            if (clickType.isLeftClick()) {
                // Confirm reset all
                admin.closeInventory();
                admin.sendMessage("§c§l⚠ WARNING: §cYou are about to reset ALL advancements for §e" + target.getName());
                admin.sendMessage("§cType §e/progressadmin confirm-reset-adv " + target.getName() + " §cto confirm");
                return;
            } else if (clickType.isRightClick()) {
                // Confirm grant all
                admin.closeInventory();
                admin.sendMessage("§e§l⚠ WARNING: §eYou are about to grant ALL advancements to §e" + target.getName());
                admin.sendMessage("§eType §a/progressadmin confirm-grant-adv " + target.getName() + " §eto confirm");
                return;
            }
        }
        
        // Close button (53)
        if (slot == 53) {
            admin.closeInventory();
            admin.sendMessage("§7Advancement admin menu closed.");
        }
    }
    
    /**
     * Handle clicks on advancement items
     */
    private void handleAdvancementClick(Player admin, Player target, ItemStack item) {
        // Extract advancement key from lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }
        
        String keyLine = null;
        for (String line : item.getItemMeta().getLore()) {
            if (line.startsWith("§7Key: §f")) {
                keyLine = line.replace("§7Key: §f", "");
                break;
            }
        }
        
        if (keyLine == null) {
            return;
        }
        
        // Get the advancement
        NamespacedKey key = NamespacedKey.fromString(keyLine);
        if (key == null) {
            admin.sendMessage("§c§l✘ §cInvalid advancement key!");
            return;
        }
        
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) {
            admin.sendMessage("§c§l✘ §cAdvancement not found!");
            return;
        }
        
        // Toggle advancement
        boolean completed = target.getAdvancementProgress(advancement).isDone();
        
        if (completed) {
            menu.revokeAdvancement(target, advancement, admin);
        } else {
            menu.grantAdvancement(target, advancement, admin);
        }
        
        // Refresh menu after a brief delay to show changes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (admin.hasMetadata("wdp_adv_menu_target")) {
                String categoryStr = admin.getMetadata("wdp_adv_menu_category").get(0).asString();
                String filterStr = admin.getMetadata("wdp_adv_menu_filter").get(0).asString();
                int page = admin.getMetadata("wdp_adv_menu_page").get(0).asInt();
                
                menu.openMenu(admin, target, 
                    AdvancementAdminMenu.Category.valueOf(categoryStr),
                    AdvancementAdminMenu.FilterMode.valueOf(filterStr),
                    page);
            }
        }, 2L);
    }
    
    public AdvancementAdminMenu getMenu() {
        return menu;
    }
}
