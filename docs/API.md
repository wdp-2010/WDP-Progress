# WDP Progress API Documentation

Complete API reference for developers integrating with the WDP Progress system.

## Getting Started

### Add Dependency

**Maven:**
```xml
<repositories>
    <repository>
        <id>wdp-repo</id>
        <url>https://repo.wdpserver.com/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.wdp</groupId>
        <artifactId>wdp-progress</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**plugin.yml:**
```yaml
depend: [WDPProgress]
# or
softdepend: [WDPProgress]
```

### Initialize API

```java
import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.api.ProgressAPI;

public class YourPlugin extends JavaPlugin {
    private ProgressAPI progressAPI;
    
    @Override
    public void onEnable() {
        if (setupProgressAPI()) {
            getLogger().info("Successfully hooked into WDP Progress!");
        } else {
            getLogger().warning("WDP Progress not found!");
        }
    }
    
    private boolean setupProgressAPI() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WDPProgress");
        if (plugin == null || !(plugin instanceof WDPProgressPlugin)) {
            return false;
        }
        
        progressAPI = ((WDPProgressPlugin) plugin).getProgressAPI();
        return progressAPI != null;
    }
}
```

## Core API Methods

### Query Progress

#### `getPlayerProgress(Player player)`
Get a player's current progress score (1-100).

```java
double progress = api.getPlayerProgress(player);
player.sendMessage("Your progress: " + progress + "/100");
```

#### `getPlayerProgress(UUID uuid)`
Get progress by UUID (works for offline players).

```java
UUID uuid = UUID.fromString("...");
double progress = api.getPlayerProgress(uuid);
```

#### `getPlayerProgressPercentage(Player player)`
Get progress as a decimal percentage (0.0 - 1.0).

```java
double percentage = api.getPlayerProgressPercentage(player);
// percentage = 0.75 means 75% progress
```

#### `hasProgress(Player player, double threshold)`
Check if player has reached a minimum progress level.

```java
if (api.hasProgress(player, 50.0)) {
    // Player has at least 50 progress
    player.sendMessage("You can access intermediate content!");
}
```

### Modify Progress

#### `recalculateProgress(Player player)`
Force immediate recalculation of player's progress.

```java
double newProgress = api.recalculateProgress(player);
player.sendMessage("Progress recalculated: " + newProgress);
```

#### `setProgress(Player player, double progress)`
Manually set a player's progress (admin only, requires config permission).

```java
// Set player to 75 progress
if (api.setProgress(player, 75.0)) {
    player.sendMessage("Progress manually set to 75");
}
```

### Custom Achievements

#### `grantAchievement(Player player, String achievementId)`
Grant a custom achievement to a player.

```java
// Achievement must be defined in config.yml
if (api.grantAchievement(player, "master_builder")) {
    player.sendMessage("Achievement unlocked: Master Builder!");
    // Progress is automatically recalculated
}
```

#### `removeAchievement(Player player, String achievementId)`
Remove a custom achievement from a player.

```java
if (api.removeAchievement(player, "master_builder")) {
    player.sendMessage("Achievement revoked");
}
```

#### `hasAchievement(Player player, String achievementId)`
Check if a player has a specific achievement.

```java
if (api.hasAchievement(player, "first_pvp_kill")) {
    // Player has already gotten their first PvP kill
}
```

### Leaderboards

#### `getTopPlayers(int limit)`
Get the top players by progress.

```java
List<Map.Entry<UUID, Double>> topPlayers = api.getTopPlayers(10);

for (int i = 0; i < topPlayers.size(); i++) {
    UUID uuid = topPlayers.get(i).getKey();
    double progress = topPlayers.get(i).getValue();
    
    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
    sender.sendMessage((i+1) + ". " + player.getName() + " - " + progress);
}
```

#### `getProgressHistory(Player player, int limit)`
Get a player's progress history.

```java
import com.wdp.progress.data.DatabaseManager.ProgressHistoryEntry;

List<ProgressHistoryEntry> history = api.getProgressHistory(player, 20);

for (ProgressHistoryEntry entry : history) {
    double progress = entry.getProgress();
    long timestamp = entry.getTimestamp();
    // Display history...
}
```

### Configuration

#### `getMinProgress()` / `getMaxProgress()`
Get the configured minimum and maximum progress values.

```java
int min = api.getMinProgress(); // Usually 1
int max = api.getMaxProgress(); // Usually 100
```

#### `isEnabled()`
Check if the plugin is properly loaded.

```java
if (api.isEnabled()) {
    // Plugin is ready to use
}
```

## Events

### ProgressChangeEvent

Fired when a player's progress changes significantly.

**Important**: This event is **asynchronous** - do not modify game state directly!

```java
import com.wdp.progress.api.events.ProgressChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ProgressListener implements Listener {
    
    @EventHandler
    public void onProgressChange(ProgressChangeEvent event) {
        Player player = event.getPlayer();
        double oldProgress = event.getOldProgress();
        double newProgress = event.getNewProgress();
        double delta = event.getProgressDelta();
        
        // Check if progress increased or decreased
        if (event.isIncrease()) {
            // Progress went up
            player.sendMessage("Progress increased by " + delta);
        } else if (event.isDecrease()) {
            // Progress went down (death penalty, lost items, etc.)
            player.sendMessage("Progress decreased by " + Math.abs(delta));
        }
        
        // Get detailed breakdown
        ProgressResult result = event.getResult();
        double advancementsScore = result.getAdvancementsScore();
        double equipmentScore = result.getEquipmentScore();
        // ... etc
    }
}
```

### Event Properties

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayer()` | `Player` | The player whose progress changed |
| `getOldProgress()` | `double` | Previous progress value |
| `getNewProgress()` | `double` | New progress value |
| `getProgressDelta()` | `double` | Change in progress (can be negative) |
| `isIncrease()` | `boolean` | True if progress increased |
| `isDecrease()` | `boolean` | True if progress decreased |
| `getResult()` | `ProgressResult` | Detailed calculation breakdown |

### ProgressResult

Access detailed category scores from the calculation:

```java
ProgressResult result = event.getResult();

// Category scores (0-100 within that category)
double advScore = result.getAdvancementsScore();
double expScore = result.getExperienceScore();
double eqScore = result.getEquipmentScore();
double econScore = result.getEconomyScore();
double statScore = result.getStatisticsScore();
double achScore = result.getAchievementsScore();
double penalty = result.getDeathPenalty();
```

## Use Cases

### Quest System Integration

```java
// Check if player has enough progress to start a quest
public boolean canStartQuest(Player player, int requiredProgress) {
    return progressAPI.hasProgress(player, requiredProgress);
}

// Grant achievement when quest is completed
public void completeQuest(Player player, String questId) {
    progressAPI.grantAchievement(player, "quest_" + questId);
}
```

### Rank System

```java
@EventHandler
public void onProgressChange(ProgressChangeEvent event) {
    Player player = event.getPlayer();
    double progress = event.getNewProgress();
    
    // Promote player based on progress
    if (progress >= 80 && !hasRank(player, "Expert")) {
        promotePlayer(player, "Expert");
    } else if (progress >= 60 && !hasRank(player, "Advanced")) {
        promotePlayer(player, "Advanced");
    }
    // ... etc
}
```

### Access Control

```java
// Prevent access to area unless player has enough progress
@EventHandler
public void onPlayerMove(PlayerMoveEvent event) {
    if (isInRestrictedArea(event.getTo())) {
        if (!progressAPI.hasProgress(event.getPlayer(), 50.0)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You need 50 progress to enter this area!");
        }
    }
}
```

### Custom Achievements

Define in config.yml:
```yaml
achievements:
  custom-achievements:
    first_trade: 2.0
    master_trader: 10.0
    legendary_merchant: 15.0
```

Grant in your plugin:
```java
@EventHandler
public void onPlayerTrade(TradeEvent event) {
    Player player = event.getPlayer();
    
    if (!progressAPI.hasAchievement(player, "first_trade")) {
        progressAPI.grantAchievement(player, "first_trade");
        player.sendMessage("Achievement: First Trade! (+2 progress)");
    }
    
    // Track trades and grant higher achievements
    int tradeCount = getTradeCount(player);
    if (tradeCount >= 100 && !progressAPI.hasAchievement(player, "master_trader")) {
        progressAPI.grantAchievement(player, "master_trader");
    }
}
```

### Progress-Based Rewards

```java
// Give rewards at progress milestones
@EventHandler
public void onProgressChange(ProgressChangeEvent event) {
    Player player = event.getPlayer();
    double oldProgress = event.getOldProgress();
    double newProgress = event.getNewProgress();
    
    // Check if player crossed a milestone
    for (int milestone : new int[]{25, 50, 75, 100}) {
        if (oldProgress < milestone && newProgress >= milestone) {
            giveReward(player, milestone);
            Bukkit.broadcastMessage(player.getName() + " reached " + milestone + " progress!");
        }
    }
}

private void giveReward(Player player, int milestone) {
    switch (milestone) {
        case 25:
            // Novice reward
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
            break;
        case 50:
            // Intermediate reward
            player.getInventory().addItem(new ItemStack(Material.ELYTRA, 1));
            break;
        case 75:
            // Advanced reward
            player.getInventory().addItem(new ItemStack(Material.NETHERITE_INGOT, 3));
            break;
        case 100:
            // Master reward
            player.setHealth(player.getMaxHealth());
            player.sendMessage(ChatColor.GOLD + "Congratulations on mastering the game!");
            break;
    }
}
```

## Best Practices

1. **Always check if API is available** before using it
2. **Use soft-depend** if your plugin can work without progress
3. **Don't spam recalculations** - the plugin handles updates automatically
4. **Use events for reactions** - don't poll for progress changes
5. **Cache progress values** if you need to check frequently
6. **Respect async events** - don't modify game state in ProgressChangeEvent
7. **Test with offline players** - use UUID-based methods when appropriate

## Thread Safety

- All API methods are **thread-safe**
- `ProgressChangeEvent` is fired **asynchronously**
- Use `Bukkit.getScheduler().runTask()` to sync back to main thread if needed

```java
@EventHandler
public void onProgressChange(ProgressChangeEvent event) {
    // This is async - be careful!
    
    // Sync to main thread for game modifications
    Bukkit.getScheduler().runTask(plugin, () -> {
        // Now safe to modify game state
        event.getPlayer().sendMessage("Progress updated!");
    });
}
```

## Support

For API support:
- Check the [FAQ](FAQ.md)
- Join our [Discord](https://discord.gg/wdpserver)
- Open an [Issue](https://github.com/wdpserver/wdp-progress/issues)