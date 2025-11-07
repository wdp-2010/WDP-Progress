# WDP Progress System

**Advanced Player Progress Tracking for Minecraft Servers**

[![Version](https://img.shields.io/badge/version-1.1.0-blue.svg)](https://github.com/wdpserver/wdp-progress)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.6-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21+-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

## 📋 Overview

WDP Progress is a sophisticated player progression tracking system that calculates a comprehensive progress score (1-100) based on multiple dimensions of gameplay. Unlike simple level-based systems, this plugin evaluates players across six weighted categories to provide a realistic representation of their overall advancement in the game.

### Key Features

- **🎯 Multi-Dimensional Progress Tracking**: Evaluates 6 major categories with configurable weights
- **📊 Advanced Algorithms**: Uses logarithmic scaling and diminishing returns to prevent grinding
- **💾 Dual Database Support**: SQLite for easy setup, MySQL for scalability
- **🔌 Full API**: Extensive API for other plugins to query and modify progress
- **⚡ High Performance**: Async calculations, connection pooling, and intelligent caching
- **✨ Interactive GUI Menu**: Beautiful inventory-based progress viewer with detailed tooltips
- **🔧 Debug Command**: Detailed technical breakdown for troubleshooting
- **📈 Progress History**: Track player progression over time
- **🏆 Custom Achievements**: Server-specific milestones that boost progress
- **💀 Death Penalties**: Dynamic penalties that can recover over time
- **🎨 Simple Explanations**: Each category explained in plain language
- **🔧 Highly Configurable**: Almost everything is customizable via config

### What's New in 1.1.0

- **🎨 Interactive GUI Menu**: Beautiful inventory-based interface with clickable items
- **🔍 Debug Command**: `/progress debug <player>` for detailed technical breakdown
- **📝 Simple Language**: Easy-to-understand explanations for all categories
- **⬆️ Version Update**: Updated to Minecraft 1.21.6 and Java 21
- **🎯 Enhanced Tab Completion**: Better command suggestions

## 🎮 Progress Scale Explained

The progress score (1-100) represents a player's overall advancement through the game:

### Progress Tiers

| Score | Tier | Description |
|-------|------|-------------|
| 1-20 | **Beginner** | Just started, basic tools and resources |
| 21-40 | **Novice** | Iron age, initial exploration, early advancements |
| 41-60 | **Intermediate** | Diamond tools, nether access, established base |
| 61-80 | **Advanced** | End dimension, elytra, advanced enchantments |
| 81-99 | **Expert** | Netherite gear, wither defeated, extensive achievements |
| 100 | **Master** | Complete mastery of all game aspects |

### What Affects Your Score?

#### 1. **Advancements (25%)** - Story Progression
- Completing Minecraft advancements across all categories
- Key milestones: Enter Nether (+10), Kill Dragon (+20), Get Elytra (+15)
- Weighted by importance: Story (35%), End (25%), Nether (20%)

#### 2. **Experience (15%)** - Player Levels
- Based on XP levels with diminishing returns
- Level 10 = Early game, Level 30 = Mid game, Level 100 = Master
- Uses logarithmic scaling to prevent grinding abuse

#### 3. **Equipment (20%)** - Gear Quality
- Evaluates armor, tools, weapons, and special items
- Material tier: Netherite (10 pts) > Diamond (8) > Iron (3) > Stone (1)
- Enchantments boost value significantly (Mending +150%, Fortune +140%)
- Damaged items worth less, full armor sets get 15% bonus
- Checks inventory + ender chest for best items

#### 4. **Economy (15%)** - Wealth
- Money/balance with logarithmic scaling
- $100 = ~20% progress, $10,000 = ~40%, $1,000,000 = 100%
- Milestone bonuses at key thresholds
- Requires Vault + economy plugin

#### 5. **Statistics (15%)** - Gameplay Metrics
- Mob kills (boss kills worth more: Dragon +50, Wither +40, Warden +30)
- Blocks mined (valuable ores worth more: Ancient Debris +10, Diamond +5)
- Distance traveled (exploration encourages progress)
- Playtime (with diminishing returns)
- Death penalty: -0.5 points per death

#### 6. **Achievements (10%)** - Custom Milestones
- Server-specific achievements defined in config
- Can be granted by other plugins via API
- Examples: first_join, master_builder, legendary_fighter

## 📦 Installation

1. **Download** the latest release from [Releases](https://github.com/wdpserver/wdp-progress/releases)
2. **Place** `WDP-Progress-1.0.0.jar` in your server's `plugins` folder
3. **Restart** your server
4. **Configure** `plugins/WDPProgress/config.yml` to your preferences
5. **(Optional)** Install [Vault](https://www.spigotmc.org/resources/vault.34315/) and an economy plugin for economy features

## 🔧 Configuration

The plugin is extensively configurable. See [CONFIGURATION.md](docs/CONFIGURATION.md) for full documentation.

### Quick Configuration Tips

```yaml
# Adjust category weights (must sum to 100)
weights:
  advancements: 25.0    # Story progression
  experience: 15.0      # Player levels
  equipment: 20.0       # Gear quality
  economy: 15.0         # Money/balance
  statistics: 15.0      # Gameplay metrics
  achievements: 10.0    # Custom milestones

# Configure what's tracked
equipment:
  include-ender-chest: true   # Check ender chest for items
  include-inventory: true     # Check main inventory
  include-armor: true         # Check equipped armor

# Death penalties
death-penalty:
  enabled: true
  temporary-penalty:
    amount: 2.0              # Progress points lost
    recovery-time: 3600      # Seconds until recovery (1 hour)
```

## 📝 Commands

### Player Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/progress` | `wdp.progress.view` | Open interactive GUI menu with your progress |
| `/progress <player>` | `wdp.progress.view.others` | View another player's progress (GUI) |
| `/progress debug` | `wdp.progress.debug` | View technical debug info about your progress |
| `/progress debug <player>` | `wdp.progress.debug` | Debug another player's progress |

**Aliases:** `/prog`, `/wdpprogress`

### Admin Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/progressadmin reload` | `wdp.progress.admin.reload` | Reload configuration |
| `/progressadmin recalculate <player>` | `wdp.progress.admin.recalculate` | Force recalculation |
| `/progressadmin set <player> <value>` | `wdp.progress.admin.set` | Manually set progress |
| `/progressadmin reset <player>` | `wdp.progress.admin.reset` | Reset player data |
| `/progressadmin debug <player>` | `wdp.progress.admin.debug` | View detailed debug info |

**Aliases:** `/progadmin`, `/padmin`

## 🔌 API Usage

WDP Progress provides a comprehensive API for other plugins.

### Basic Usage

```java
// Get the API instance
WDPProgressPlugin progressPlugin = (WDPProgressPlugin) Bukkit.getPluginManager().getPlugin("WDPProgress");
ProgressAPI api = progressPlugin.getProgressAPI();

// Get player progress (1-100)
double progress = api.getPlayerProgress(player);

// Check if player has reached a threshold
if (api.hasProgress(player, 50.0)) {
    // Player is at least level 50 progress
    player.sendMessage("You've reached intermediate level!");
}

// Grant custom achievement
api.grantAchievement(player, "master_builder");

// Force recalculation
double newProgress = api.recalculateProgress(player);
```

### Listen to Progress Changes

```java
@EventHandler
public void onProgressChange(ProgressChangeEvent event) {
    Player player = event.getPlayer();
    double oldProgress = event.getOldProgress();
    double newProgress = event.getNewProgress();
    
    if (event.isIncrease()) {
        player.sendMessage("Progress increased by " + event.getProgressDelta());
    }
    
    // Check if player reached a milestone
    if (oldProgress < 50 && newProgress >= 50) {
        // Player just reached intermediate level
        Bukkit.broadcastMessage(player.getName() + " reached Intermediate level!");
    }
}
```

See [API.md](docs/API.md) for complete API documentation.

## 🏗️ Building

### Requirements
- Java 21 or higher
- Maven 3.6+
- Minecraft 1.21.6 (Spigot API 1.21.3)

### Build Commands

```bash
# Build without deploying
mvn clean package -DskipTests

# Build and deploy (configure deploy.sh first)
mvn clean package

# Clean build directory
mvn clean
```

The compiled JAR will be in `target/WDPProgress-1.1.0-SNAPSHOT.jar`

## 🗄️ Database

### SQLite (Default)
- Automatic setup, no configuration needed
- Perfect for small-medium servers
- Data stored in `plugins/WDPProgress/progress_data.db`

### MySQL (Recommended for large servers)
```yaml
database:
  type: MYSQL
  mysql:
    host: "localhost"
    port: 3306
    database: "wdp_progress"
    username: "root"
    password: "yourpassword"
```

The plugin automatically creates required tables on first run.

## 📊 Performance

- **Async Calculations**: Heavy calculations run asynchronously
- **Connection Pooling**: HikariCP for efficient database access
- **Smart Caching**: Player data cached in memory with configurable expiration
- **Throttled Updates**: Prevents spam from rapid player actions
- **Batch Operations**: Efficient bulk saves during shutdown

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file.

## 🐛 Bug Reports

Found a bug? Please [open an issue](https://github.com/wdpserver/wdp-progress/issues) with:
- Minecraft version
- Server software (Spigot/Paper)
- Plugin version
- Steps to reproduce
- Error logs (if any)

## 💬 Support

- **Discord**: [WDP Server Discord](https://discord.gg/wdpserver)
- **Issues**: [GitHub Issues](https://github.com/wdpserver/wdp-progress/issues)

## 🙏 Credits

Developed by the WDP Development Team for the WDP Minecraft Server.

---

**Note**: This plugin is designed for the WDP Server but can be adapted for any Minecraft server. Configuration is key to getting the progress scale right for your server's gameplay style.

