# WDP Progress v1.2.0 - Layered Menu System Update

**Release Date:** November 8, 2025  
**Update Type:** Major Feature Release  
**Minecraft Version:** 1.21.6  
**Java Version:** 21+

---

## 🎉 Major Features

### 🎨 Layered Menu System

Complete redesign of the GUI system with navigable, detailed sub-menus for every progress category.

#### Main Menu Enhancements
- **Live Statistics Display**: Quick overview shows playtime, mob kills, distance traveled, and deaths
- **Click-to-Navigate**: All category items now have "▶ Click to view details!" hints
- **Consistent Styling**: Unified color scheme and formatting across all menus
- **Smart Tooltips**: Helpful information on every icon

#### New Detail Menus

**1. Advancements Detail Menu** (`📖 Advancements`)
- Shows **ALL** Minecraft advancements (100+ items)
- Color-coded status: ✓ Green = Completed, ✗ Gray = Not Completed
- Category labels: Story, Nether, The End, Adventure, Husbandry
- Progress tracking for multi-step advancements
- Summary item showing total completion percentage
- Scrollable with pagination (28 items per page)

**2. Equipment Detail Menu** (`⚔ Equipment Quality`)
- **Armor Display**: All 4 armor slots with detailed stats
- **Held Items**: Main hand and off-hand equipment
- **Notable Items**: Automatically highlights special items (Elytra, Trident, Netherite gear, etc.)
- **Per-Item Details**:
  - Material tier with color coding (Netherite → Wood)
  - Durability percentage with color indicators
  - All enchantments with roman numerals (Protection IV, Mending I, etc.)
  - Estimated value in points
- **Full Set Bonus**: Visual indicator when wearing complete armor set (+15%)
- **Section Headers**: Organized layout with glass pane separators

**3. Statistics Detail Menu** (`📊 Statistics & Activity`)
- **30+ Tracked Statistics** organized by category:
  - **Movement**: Walk distance, Sprint distance, Fly distance (Elytra), Boat distance
  - **Combat**: Mob kills, Player kills, Deaths (with Totem tracking)
  - **Mining**: Total blocks mined, Diamonds, Iron, Gold, Ancient Debris
  - **Building**: Blocks placed
  - **Interaction**: Chests opened, Items crafted, Villager trades, Food eaten
  - **Damage**: Damage dealt and taken (displayed in hearts)
  - **Special**: Ender pearls thrown, Fish caught, Potions brewed
  - **Boss Kills**: Ender Dragon, Wither, Warden, Elder Guardian (with icons)
- **Smart Formatting**: Thousands separators, decimals for precision
- **Units Display**: km for distance, hearts for damage, hours for playtime

**4. Economy & Experience Detail Menu** (`💰 Economy & Experience`)
- **Economy Section**:
  - Current balance with formatting ($1,234,567.89)
  - Wealth tier indicator (Broke → Millionaire with color coding)
  - Next milestone calculation
- **Experience Section**:
  - Current level and XP progress bar percentage
  - Total lifetime XP earned
  - Level tier (Novice → Legendary)
  - Level breakdown with achievement milestones
  - XP requirements for next level
- **Combined View**: Both systems in one convenient menu

**5. Death Penalty Detail Menu** (`☠ Death Penalties`)
- **Overview**: Total deaths and current penalty points
- **GravesX Integration Status**: Visual indicator showing if smart tracking is active
- **Active Graves**: Count of unretrieved graves with recovery tips
- **How It Works**: Educational section explaining the penalty system
- **Tips Section**: Advice on reducing death penalties
- **Fair System**: Explains time-based decay and recovery mechanics

#### Navigation System
- **Pagination**: Previous/Next Page buttons for large lists
- **Back Button**: Always present to return to main menu
- **Page Indicator**: Shows current page and total pages
- **Consistent Controls**: Same navigation across all sub-menus
- **Bordered Design**: Glass pane borders for professional look

---

## 🎯 Advancement Management System (OP Only)

### New Admin Command
```
/progressadmin advancements <player>
```

### Features
- **Filtering System**:
  - Filter by status: All, Completed, Uncompleted
  - Filter by category: Story, Nether, End, Adventure, Husbandry
- **Individual Control**: Click any advancement to toggle completion status
- **Bulk Operations**:
  - **Grant All**: Complete all advancements at once (with confirmation)
  - **Reset All**: Clear all advancement progress (with confirmation)
- **Real-time Updates**: Changes reflect immediately
- **Visual Feedback**: Color-coded icons (Green = Complete, Red = Incomplete)
- **Safety Confirmations**: Double-click required for destructive operations

---

## ⚰️ GravesX Integration

### Smart Death Tracking
- **Item Value Calculation**: Tracks what was actually lost on death
- **Material Values**: Netherite > Diamond > Iron > Gold > Stone
- **Enchantment Values**: Each enchantment level adds to total loss
- **Durability Factor**: Damaged items worth less
- **Time-Based Decay**: Penalty reduces over time
  - 30% reduction after 1 minute
  - 60% reduction after 3 minutes
  - 100% reduction after 5 minutes (penalty clears)

### Grave Recovery System
- **Proximity Detection**: Tracks when player gets near their grave
- **Recovery Bonus**: Penalty reduced when items are retrieved
- **Multiple Graves**: Supports tracking multiple active graves
- **Location Storage**: Remembers grave locations for reference

### Fallback System
- **Graceful Degradation**: Works without GravesX using simple death count
- **Automatic Detection**: Checks for GravesX at runtime
- **No Dependencies**: GravesX is optional (soft dependency only)

---

## 🔧 Technical Improvements

### Architecture
- **Base Menu Class**: `DetailMenu.java` provides common functionality
- **Inheritance Pattern**: All sub-menus extend base for consistency
- **Pagination Engine**: Automatic scrolling for lists exceeding 28 items
- **Smart Slots**: Optimized slot layout (7x4 grid = 28 item slots)
- **Border System**: Automatic glass pane borders around all menus

### Performance
- **Lazy Loading**: Sub-menus created only when accessed
- **Shared Instance**: Single ProgressMenu instance across plugin
- **Efficient Queries**: Optimized data retrieval for large datasets
- **Cache Friendly**: Leverages existing PlayerData caching

### Code Quality
- **1,500+ New Lines**: Well-documented and organized
- **6 New Classes**: Modular design for maintainability
- **Consistent Styling**: Unified color scheme and formatting
- **Error Handling**: Graceful degradation when data unavailable

---

## 🐛 Bug Fixes

- Fixed GravesX plugin detection (was checking for "Graves" instead of "GravesX")
- Fixed missing `getProgressMenu()` method in WDPProgressPlugin
- Fixed statistic `EAT_CAKE_SLICE` not available in 1.21.6 (removed)
- Fixed PlayerData method calls for death tracking (now uses getTotalDeaths())
- Corrected menu title parsing for player name extraction
- Fixed pagination calculation for empty or small lists

---

## 📋 Files Changed/Added

### New Files
1. `DetailMenu.java` - Base class for all sub-menus (190 lines)
2. `AdvancementsDetailMenu.java` - Advancement viewer (230 lines)
3. `EquipmentDetailMenu.java` - Equipment inspector (380 lines)
4. `StatisticsDetailMenu.java` - Statistics viewer (280 lines)
5. `EconomyExperienceDetailMenu.java` - Economy & XP viewer (250 lines)
6. `DeathPenaltyDetailMenu.java` - Death penalty viewer (190 lines)

### Modified Files
1. `ProgressMenu.java` - Added sub-menu initialization and navigation
2. `ProgressMenuListener.java` - Enhanced click handling for navigation
3. `WDPProgressPlugin.java` - Added ProgressMenu field and getter, fixed GravesX detection
4. `ProgressCommand.java` - Updated to use plugin's menu instance
5. `README.md` - Updated to version 1.2.0 with new features documented

---

## 📊 Statistics

- **Total Lines Added**: ~1,500 lines of new code
- **New Classes**: 6 detail menu classes
- **Statistics Tracked**: 30+ different gameplay metrics
- **Menu Capacity**: 28 items per page with unlimited pagination
- **Sub-Menus**: 5 fully-featured navigable menus
- **Color Schemes**: Consistent theme across all menus
- **Admin Features**: 1 new advancement management system

---

## 🎯 Migration Notes

### From v1.1.0
- **No Breaking Changes**: Fully backward compatible
- **Config Compatible**: No config changes required
- **Database Compatible**: No schema changes
- **API Compatible**: All existing API methods unchanged
- **Auto-Update**: Simply replace JAR and restart

### New Permissions
No new permissions added. All features use existing permission nodes:
- `wdp.progress.view` - Access main menu and all sub-menus
- `wdp.progress.admin` - Access advancement management menu

---

## 🚀 Usage Examples

### For Players
```
/progress                    # Opens main menu with stats
Click "📖 Advancements"      # View all advancements
Click "⚔ Equipment"          # Inspect gear and items
Click "📊 Statistics"        # See detailed statistics
Click "💰 Economy"           # Check wealth and XP
Click "☠ Death Penalties"    # Understand penalties
```

### For Admins
```
/progressadmin advancements PlayerName    # Open advancement manager
Click "Show: All"                        # Filter by status
Click "Category: Story"                  # Filter by category
Click any advancement                    # Toggle completion
Click "Grant All Advancements"           # Bulk grant (with confirm)
Click "Reset All Advancements"           # Bulk reset (with confirm)
```

---

## 🎨 Visual Enhancements

### Color Coding
- **Green**: Completed items, positive values, bonuses
- **Red**: Incomplete items, penalties, negative values
- **Yellow**: Informational text, tips, section headers
- **Gray**: Neutral items, descriptions, borders
- **Aqua/Light Purple**: Highlighted values, special items
- **Gold**: Currency, valuable items, milestones

### Icons
- 📖 Advancements (Book)
- ⚔ Equipment (Diamond Sword/Chestplate)
- 📊 Statistics (Diamond Sword)
- 💰 Economy (Gold Ingot)
- ✨ Experience (Experience Bottle)
- ☠ Death Penalties (Skeleton Skull)
- ⚰️ Graves (Chest)
- 🎯 Admin Controls (Nether Star)

---

## 🔮 Future Plans

### Planned for v1.3.0
- **Progress History Graph**: Visual chart of progress over time
- **Leaderboards**: Top players by progress score
- **Custom Achievements Menu**: Scrollable view of server achievements
- **Comparison View**: Compare your progress to another player
- **Export Function**: Download progress report as PDF/Image

### Planned for v1.4.0
- **Web Dashboard**: View progress on external website
- **Discord Integration**: Progress updates in Discord
- **Rewards System**: Auto-grant rewards at milestones
- **Titles & Prefixes**: Custom titles based on progress

---

## 💬 Support

- **Discord**: [WDP Server Discord](https://discord.gg/wdpserver)
- **GitHub Issues**: [Report bugs or request features](https://github.com/wdpserver/wdp-progress/issues)
- **Wiki**: [Full documentation](https://github.com/wdpserver/wdp-progress/wiki)

---

## 🙏 Acknowledgments

Special thanks to:
- **GravesX Team** for the excellent graves plugin and integration possibilities
- **Spigot Community** for API documentation and support
- **WDP Server Players** for testing and feedback

---

**Enjoy the new layered menu system! Your feedback helps us improve.** 🎮✨
