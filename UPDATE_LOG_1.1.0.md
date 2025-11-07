# WDP Progress - Version 1.1.0 Update

## 🎉 Major Updates

### Version Upgrade
- **Updated to Minecraft 1.21.6** (using Spigot API 1.21.3-R0.1-SNAPSHOT)
- **Updated to Java 21** for modern performance and features

### New Features

#### 1. Interactive GUI Menu ✨

The `/progress` command now opens a beautiful, interactive inventory-based GUI instead of text output!

**Features:**
- **Main Progress Display** - Shows your overall score with color-coded tier (center top)
- **6 Category Items** - Each category is represented by a unique item:
  - 📖 **Advancements** (Book) - Story progression and achievements
  - ✨ **Experience** (Experience Bottle) - Your XP levels
  - ⚔ **Equipment** (Diamond Chestplate) - Gear quality
  - 💰 **Economy** (Gold Ingot) - Money and wealth
  - ⚡ **Statistics** (Diamond Sword) - Gameplay activity
  - ⭐ **Achievements** (Nether Star) - Custom server achievements
  
- **Death Penalty Display** (Skull) - Shows impact of deaths on your progress
- **Tips Item** (Writable Book) - Personalized advice based on your lowest category
- **Explanation Section** (Knowledge Book) - Simple guide explaining how the system works
- **Progress History** (Clock) - Coming soon feature
- **Close Button** (Barrier) - Click to close the menu

**Detailed Information:**
Each item shows:
- Category score (0-100)
- Weight percentage
- Contribution to final score
- Simple explanation in plain language
- How to improve tips
- Relevant stats (balance, level, etc.)

**Progress Tiers with Colors:**
- 1-20: **Beginner** (Red)
- 21-40: **Novice** (Gold)
- 41-60: **Intermediate** (Yellow)
- 61-80: **Advanced** (Green)
- 81-99: **Expert** (Aqua)
- 100: **Master** (Light Purple)

#### 2. Debug Command 🔧

New `/progress debug <player>` command for detailed debugging!

**Usage:**
```
/progress debug          # Debug your own progress
/progress debug <player> # Debug another player's progress
```

**Permission:**
- `wdp.progress.debug` - Required to use debug command

**Features:**
- Shows raw category scores (0-100) before weighting
- Displays weight percentages for each category
- Calculates contribution of each category to final score
- Shows death penalty breakdown
- Displays calculation formula
- Lists detailed player statistics:
  - Current XP level
  - Total deaths
  - Completed achievements count
  - Account balance (if Vault is available)
  - Last update timestamp
- Visual progress bars for each category
- Color-coded output for easy reading

**Example Output:**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
       DEBUG: PlayerName's Progress
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FINAL SCORE: 67.5/100

CATEGORY BREAKDOWN:

Advancements:
  Score: 75.0/100 ██████████░░░░░░░░░░
  Weight: 25%
  Contribution: +18.8 points

[... more categories ...]

Death Penalty: -2.5 points

CALCULATION FORMULA:
Final = (Adv×25% + Exp×15% + Equip×20% + Econ×15% + Stats×15% + Ach×10%) - Deaths

PLAYER STATISTICS:
• Level: 30
• Total Deaths: 5
• Achievements: 3
• Balance: $15,420.50
• Last Updated: 2025-01-12 14:30:45
```

## Command Updates

### `/progress` Command
- **Old:** Showed text-based progress report
- **New:** Opens interactive GUI menu
- **Usage:**
  - `/progress` - View your own progress (GUI)
  - `/progress <player>` - View another player's progress (GUI)
  - `/progress debug` - Debug your own progress (text)
  - `/progress debug <player>` - Debug another player's progress (text)

### Tab Completion
Enhanced tab completion now suggests:
- `debug` subcommand (if you have permission)
- Player names for viewing others
- Player names for debug command

## Technical Details

### New Classes
1. **ProgressMenu.java** (589 lines)
   - Manages GUI inventory creation
   - Handles item creation and lore
   - Color-coded progress bars
   - Dynamic tier calculation
   - Simple explanations for each category

2. **ProgressMenuListener.java** (40 lines)
   - Handles GUI click events
   - Prevents item theft from menu
   - Handles close button clicks

### Updated Classes
1. **ProgressCommand.java**
   - Added GUI menu integration
   - Added debug subcommand handler
   - Removed old text-based display methods
   - Enhanced tab completion
   - Cleaner command structure

2. **WDPProgressPlugin.java**
   - Registered ProgressMenuListener
   - Updated version compatibility

3. **plugin.yml**
   - Updated command usage descriptions
   - Added `wdp.progress.debug` permission
   - Updated permission hierarchy

## Permissions

### New Permission
- **wdp.progress.debug**
  - Description: View detailed debug information
  - Default: `op`
  - Allows use of `/progress debug` command

### Existing Permissions
- **wdp.progress.view** (default: true) - View your own progress
- **wdp.progress.view.others** (default: op) - View other players' progress
- **wdp.progress.admin** (default: op) - Access to admin commands

## User Experience Improvements

### Simple Language Explanations
Each category in the GUI now includes:
- **"What is this?"** section explaining the category in simple terms
- **"How to improve:"** section with actionable tips
- Clear, jargon-free language suitable for all players

### Visual Enhancements
- Color-coded items based on progress tier
- Unicode symbols (⚡✨⚔💰⭐📖) for visual appeal
- Progress bars using block characters (█)
- Decorative borders with glass panes
- Consistent color scheme throughout

### Interactive Elements
- Click to close with barrier block
- Hover to view detailed information
- Category items organized in logical grid
- Clear visual hierarchy

## Configuration

No configuration changes required! All existing config options still work.

## Compatibility

- **Minecraft Version:** 1.21.6 (using Spigot API 1.21.3)
- **Java Version:** 21+
- **Dependencies:** None required (Vault optional)
- **Database:** SQLite or MySQL (unchanged)

## Migration Notes

No data migration required! All existing player data remains compatible.

## Known Issues

- History feature (clock item) is marked "Coming soon"
- GUI cannot be opened by console (by design)

## Future Enhancements

Planned features:
- Historical progress tracking graph
- Clickable category items to view detailed stats
- Achievement progress tracking in GUI
- Leaderboard integration
- Comparative progress view (compare with other players)

---

## For Developers

### API Changes
No breaking API changes. All existing ProgressAPI methods still work.

### New Methods Available
```java
// Open progress menu for a player
ProgressMenu menu = new ProgressMenu(plugin);
menu.openProgressMenu(viewer, target);
```

### Event Handling
The `ProgressMenuListener` is automatically registered by the plugin.

---

**Upgrade Instructions:**
1. Stop your server
2. Replace the old plugin JAR with the new one
3. Start your server
4. Run `/progressadmin reload` if needed

**No configuration changes required!**

---

*Updated: January 2025*
*Plugin Version: 1.1.0*
