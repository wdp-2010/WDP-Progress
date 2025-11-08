# Progress Menu GUI Layout - v1.2.0 (Layered Menu System)

## Main Progress Menu

```
┌─────────────────────────────────────────────────────────────────────┐
│ ⚡ PlayerName's Progress ⚡                                          │
├─────────────────────────────────────────────────────────────────────┤
│ [█][█][█][█][⭐ Overall Progress: 67.5/100 ⭐][█][█][█][█]          │
│ [█][ ][ ][ ][ ][ ][ ][ ][█]                                         │
│ [█][📖 Advancements][█][✨ Experience][█][⚔ Equipment][█][💰 Economy][█] │
│ [█][ ][ ][ ][ ][ ][ ][ ][█]                                         │
│ [█][📊 Statistics][█][⭐ Achievements][█][☠ Deaths][█][💡 Tips][█]     │
│ [█][ ][ ][ ][ ][ ][ ][ ][█]                                         │
│ [❓ Explain][ ][ ][ ][📊 History][ ][ ][ ][✖ Close]                │
└─────────────────────────────────────────────────────────────────────┘
```

**NEW**: All category items are now **clickable** and open detailed sub-menus!

## Item Positions (54-slot inventory)

### Row 1 (0-8): Border + Main Display
- Slots 0-3: Gray glass pane border
- **Slot 4**: ⭐ **Main Progress Display** (Diamond/Emerald/Gold/Iron/Coal based on score)
- Slots 5-8: Gray glass pane border

### Row 2 (9-17): Border
- Slot 9: Gray glass pane border
- Slots 10-16: Empty
- Slot 17: Gray glass pane border

### Row 3 (18-26): Category Items (Top Row)
- Slot 18: Gray glass pane border
- **Slot 19**: Empty (spacing)
- **Slot 10**: 📖 **Advancements** (Book) - **CLICKABLE** → Opens Advancements Detail Menu
- **Slot 20**: Empty (spacing)
- **Slot 12**: ✨ **Experience** (Experience Bottle) - **CLICKABLE** → Opens Economy & Experience Menu
- **Slot 21**: Empty (spacing)
- **Slot 14**: ⚔ **Equipment** (Diamond Chestplate) - **CLICKABLE** → Opens Equipment Detail Menu
- **Slot 22**: Empty (spacing)
- **Slot 16**: 💰 **Economy** (Gold Ingot) - **CLICKABLE** → Opens Economy & Experience Menu
- Slot 26: Gray glass pane border

### Row 4 (27-35): Border
- Slot 27: Gray glass pane border
- Slots 28-34: Empty
- Slot 35: Gray glass pane border

### Row 5 (36-44): Category Items (Bottom Row)
- Slot 36: Gray glass pane border
- **Slot 37**: Empty (spacing)
- **Slot 28**: 📊 **Statistics** (Diamond Sword) - **CLICKABLE** → Opens Statistics Detail Menu
- **Slot 38**: Empty (spacing)
- **Slot 30**: ⭐ **Achievements** (Nether Star) - Shows completed achievements
- **Slot 39**: Empty (spacing)
- **Slot 32**: ☠ **Death Penalties** (Skeleton Skull) - **CLICKABLE** → Opens Death Penalty Menu
- **Slot 40**: Empty (spacing)
- **Slot 34**: 💡 **Tips** (Writable Book) - Improvement suggestions
- Slot 44: Gray glass pane border

### Row 6 (45-53): Bottom Bar
- **Slot 45**: ❓ **Explain** (Knowledge Book) - How the system works
- Slots 46-48: Gray glass pane border
- **Slot 49**: 📊 **History** (Clock) - Coming soon
- Slots 50-52: Gray glass pane border
- **Slot 53**: ✖ **Close** (Barrier) - Click to close

## Color Coding

### Progress Tiers (Main Display)
- **Red** (1-20): Beginner - Coal
- **Gold** (21-40): Novice - Iron Ingot
- **Yellow** (41-60): Intermediate - Gold Ingot
- **Green** (61-80): Advanced - Emerald
- **Aqua** (81-99): Expert - Diamond
- **Light Purple** (100): Master - Diamond (special)

### Progress Bars
- **Filled**: Colored based on score (matches tier color)
- **Empty**: Dark Gray

## Item Tooltips

### Main Progress Item
```
⭐ Overall Progress: 67.5/100 ⭐

Progress Tier: Advanced

[████████████████████░░░░░░░░░░░░░░░░]

» Click categories below for details
» Hover for tips and information
```

### Category Items (Example: Advancements)
```
📖 Advancements

Category Score: 75.0/100
Weight: 25%
Contribution: +18.8 points

What is this?
Tracks your story progression through
Minecraft advancements. Complete the
main story, explore the Nether and End,
and unlock achievements.

How to improve:
✓ Complete story advancements
✓ Enter and explore the Nether
✓ Find and enter the End dimension
✓ Defeat the Ender Dragon
✓ Get the Elytra wings
```

### Death Penalty Item
```
💀 Death Penalties

Total Penalty: -2.5 points

What is this?
Deaths reduce your progress in
multiple ways:

1. Permanent Penalty
   Each death costs you points
   that never recover.

2. Temporary Penalty
   Immediate penalty that slowly
   recovers over time.

3. Equipment Loss
   Lost items reduce equipment
   score until replaced.

How to avoid:
✓ Be careful in combat
✓ Wear good armor
✓ Keep backup gear safe
✓ Avoid risky situations
```

### Tips Item
```
💡 Tips to Improve

Quick tips based on your progress:

► Focus on advancements!
  Complete the main story line
  and explore dimensions.

Hover over each category above
for detailed improvement tips!
```

### Explain Item
```
❓ How Does This Work?

The Progress System Explained:

Your progress score (1-100) shows how
far you've advanced in the game.

It's calculated from 6 categories:
• Advancements (25%)
• Equipment (20%)
• Experience (15%)
• Economy (15%)
• Statistics (15%)
• Achievements (10%)

Each category has its own score
(0-100), weighted by importance,
then combined for your final score.

Progress Tiers:
1-20: Beginner
21-40: Novice
41-60: Intermediate
61-80: Advanced
81-99: Expert
100: Master (very rare!)

Hover over items for details!
```

## Interactions

### Clickable Items
- **Close Button** (Barrier): Closes the menu
- All other items: Display information (no action)

### Non-Interactive Items
- Gray glass panes (border)
- All category items (informational only)

## Design Philosophy

1. **Clarity**: Clear labels and color coding
2. **Information Density**: Detailed tooltips without cluttering the view
3. **Visual Appeal**: Unicode symbols and color gradients
4. **Accessibility**: Simple language explanations
5. **Intuitive Layout**: Logical grouping of related items

## Debug Command Output

For comparison, the debug command shows technical details in text format:

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

Experience:
  Score: 60.0/100 ██████████░░░░░░░░░░
  Weight: 15%
  Contribution: +9.0 points

Equipment:
  Score: 80.0/100 ████████████░░░░░░░░
  Weight: 20%
  Contribution: +16.0 points

Economy:
  Score: 55.0/100 ██████████░░░░░░░░░░
  Weight: 15%
  Contribution: +8.3 points

Statistics:
  Score: 70.0/100 ██████████░░░░░░░░░░
  Weight: 15%
  Contribution: +10.5 points

Achievements:
  Score: 30.0/100 ██████░░░░░░░░░░░░░░
  Weight: 10%
  Contribution: +3.0 points

Death Penalty: -2.5 points

CALCULATION FORMULA:
Final = (Adv×25% + Exp×15% + Equip×20% + Econ×15% + Stats×15% + Ach×10%) - Deaths

PLAYER STATISTICS:
• Level: 30
• Total Deaths: 5
• Achievements: 3
• Balance: $15,420.50
• Last Updated: 2025-01-12 14:30:45

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

# Detail Menu Layouts - v1.2.0

All detail menus share a common structure provided by the `DetailMenu` base class:
- **Size**: 6 rows (54 slots)
- **Border**: Gray glass panes on top row, bottom row, and sides
- **Content Area**: 4 rows × 7 columns = 28 slots per page (scrollable)
- **Navigation**: Back button (slot 49), Previous Page (slot 48), Next Page (slot 50)

## 1. Statistics Detail Menu

**Title**: `"Statistics - " + playerName`

### Layout Structure
```
Row 0: [G] [G] [G] [G] [G] [G] [G] [G] [G]  (Border)
Row 1: [G] [1] [2] [3] [4] [5] [6] [7] [G]  (Stats Page 1)
Row 2: [G] [8] [9] [10] [11] [12] [13] [14] [G]
Row 3: [G] [15] [16] [17] [18] [19] [20] [21] [G]
Row 4: [G] [22] [23] [24] [25] [26] [27] [28] [G]
Row 5: [G] [G] [G] [PREV] [BACK] [NEXT] [G] [G] [G]  (Navigation)
```

### Statistics Displayed (30+ items)

**Movement Statistics**:
1. Distance Walked (IRON_BOOTS)
2. Distance Sprinted (GOLDEN_BOOTS)
3. Distance Flown (ELYTRA)
4. Distance by Boat (OAK_BOAT)
5. Distance Swam (WATER_BUCKET)
6. Distance Climbed (LADDER)
7. Distance Fallen (FEATHER)

**Combat Statistics**:
8. Mob Kills (IRON_SWORD)
9. Player Kills (DIAMOND_SWORD)
10. Deaths (SKELETON_SKULL)
11. Damage Dealt (BOW)
12. Damage Taken (SHIELD)

**Mining & Building**:
13. Blocks Broken (DIAMOND_PICKAXE)
14. Blocks Placed (GRASS_BLOCK)
15. Ores Mined (IRON_ORE)

**Interaction Statistics**:
16. Items Crafted (CRAFTING_TABLE)
17. Food Eaten (COOKED_BEEF)
18. Chests Opened (CHEST)

**Special Statistics**:
19. Jumps (SLIME_BALL)
20. Playtime (CLOCK)
21. Time Since Death (TOTEM_OF_UNDYING)
22. Animals Bred (WHEAT)
23. Fish Caught (FISHING_ROD)
24. Items Enchanted (ENCHANTED_BOOK)
25. Raids Won (OMINOUS_BANNER)

**Boss Kills**:
26. Ender Dragons (DRAGON_HEAD)
27. Withers (WITHER_SKELETON_SKULL)
28. Elder Guardians (PRISMARINE_SHARD)
29. Wardens (ECHO_SHARD)

**Formatting**: Numbers with thousands separators, units (km, hearts, hours), color-coded tiers

---

## 2. Advancements Detail Menu

**Title**: `"Advancements - " + playerName`

### Layout Structure
```
Row 0: [G] [G] [G] [G] [G] [G] [G] [G] [G]  (Border)
Row 1: [G] [SUMMARY] [ADV2] [ADV3] [ADV4] [ADV5] [ADV6] [ADV7] [G]
Row 2: [G] [ADV8] [ADV9] [ADV10] [ADV11] [ADV12] [ADV13] [ADV14] [G]
Row 3: [G] [ADV15] [ADV16] [ADV17] [ADV18] [ADV19] [ADV20] [ADV21] [G]
Row 4: [G] [ADV22] [ADV23] [ADV24] [ADV25] [ADV26] [ADV27] [ADV28] [G]
Row 5: [G] [G] [G] [PREV] [BACK] [NEXT] [G] [G] [G]  (Navigation)
```

### Display Format

**Summary Item** (Slot 10):
- **Material**: BOOK (complete) or WRITABLE_BOOK (incomplete)
- **Name**: "§6§lAdvancements Overview"
- **Lore**:
  - Completed: X/Y
  - Progress: ZZ.Z%
  - Categories shown: Adventure, Nether, End, Husbandry, etc.

**Individual Advancements**:
- **Complete**: GREEN_CONCRETE "§a✓ Advancement Name"
- **Incomplete**: GRAY_CONCRETE "§7✗ Advancement Name"
- **Lore**: Advancement description and requirements

**Categories Tracked**:
- Minecraft (root advancements)
- Adventure (exploration and combat)
- Nether (nether-specific)
- End (end-specific)
- Husbandry (animals and farming)

---

## 3. Equipment Detail Menu

**Title**: `"Equipment - " + playerName`

### Layout Structure
```
Row 0: [G] [G] [G] [G] [G] [G] [G] [G] [G]  (Border)
Row 1: [G] [HEAD] [CHEST] [LEGS] [FEET] [MAIN] [OFF] [ITEM1] [G]
Row 2: [G] [ITEM2] [ITEM3] [ITEM4] [ITEM5] [ITEM6] [ITEM7] [ITEM8] [G]
Row 3: [G] [ITEM9] [ITEM10] [ITEM11] [ITEM12] [ITEM13] [ITEM14] [ITEM15] [G]
Row 4: [G] [ITEM16] [ITEM17] [ITEM18] [ITEM19] [ITEM20] [ITEM21] [ITEM22] [G]
Row 5: [G] [G] [G] [PREV] [BACK] [NEXT] [G] [G] [G]  (Navigation)
```

### Item Display Format

**Armor Slots** (Slots 10-13):
- **Material**: Player's actual armor piece (or BARRIER if empty)
- **Name**: 
  - Equipped: "§a[Tier] [Slot]"
  - Empty: "§7Empty [Slot]"
- **Lore**:
  - Material tier (Leather → Netherite)
  - Durability: X/Y (ZZ%)
  - Enchantments: "§d⚡ EnchantName Level"
  - Protection value
  - Full set bonus: "§6⚠ Full Set Bonus: +15% value"

**Held Items** (Slots 14-15):
- Main Hand / Off Hand
- Same format as armor
- Shows weapon stats or tool stats

**Notable Inventory Items** (Slots 16+):
- Enchanted items
- Valuable items (diamonds, netherite, etc.)
- Rare items (totems, elytra, etc.)
- **Value Calculation**: Based on rarity, enchantments, durability
- **Sorting**: By estimated value (highest first)

**Empty Slot Display**:
- Material: BARRIER
- Name: "§7Empty [Slot Name]"
- Lore: "§8No item equipped"

---

## 4. Economy & Experience Detail Menu

**Title**: `"Economy & Experience - " + playerName`

### Layout Structure
```
Row 0: [G] [G] [G] [G] [G] [G] [G] [G] [G]  (Border)
Row 1: [G] [ECON_SUMMARY] [WEALTH] [MILESTONE] [XP_SUMMARY] [LEVEL] [PROGRESS] [TOTAL] [G]
Row 2: [G] [ITEM8] [ITEM9] [ITEM10] [ITEM11] [ITEM12] [ITEM13] [ITEM14] [G]
Row 3: [G] [ITEM15] [ITEM16] [ITEM17] [ITEM18] [ITEM19] [ITEM20] [ITEM21] [G]
Row 4: [G] [ITEM22] [ITEM23] [ITEM24] [ITEM25] [ITEM26] [ITEM27] [ITEM28] [G]
Row 5: [G] [G] [G] [PREV] [BACK] [NEXT] [G] [G] [G]  (Navigation)
```

### Economy Section

**Balance Overview** (Slot 10):
- **Material**: GOLD_INGOT
- **Name**: "§6§lEconomy Overview"
- **Lore**:
  - Current balance: $X,XXX.XX
  - Wealth tier
  - Server rank (if available)

**Wealth Tier** (Slot 11):
- **Material**: EMERALD (tier-based)
- **Name**: Color-coded tier name
- **Tiers**:
  - §7Broke: $0-$999
  - §fPoor: $1,000-$4,999
  - §aComfortable: $5,000-$19,999
  - §2Wealthy: $20,000-$99,999
  - §6Rich: $100,000-$499,999
  - §b§lMillionaire: $500,000+

**Next Milestone** (Slot 12):
- **Material**: GOLD_BLOCK
- **Name**: "§e§lNext Milestone"
- **Lore**:
  - Target amount
  - Amount needed
  - Progress bar

### Experience Section

**Level Overview** (Slot 13):
- **Material**: EXPERIENCE_BOTTLE
- **Name**: "§b§lExperience Overview"
- **Lore**:
  - Current level
  - Progress to next level
  - Total XP accumulated

**Level Tier** (Slot 14):
- **Material**: ENCHANTED_BOOK
- **Name**: Color-coded tier
- **Tiers**:
  - §7Novice: 0-9
  - §fApprentice: 10-19
  - §aJourneyman: 20-29
  - §2Expert: 30-39
  - §5Master: 40-49
  - §6§lLegendary: 50+

**Progress Bar** (Slot 15):
- **Material**: GREEN_STAINED_GLASS_PANE (progressing) or LIME_STAINED_GLASS_PANE (full)
- **Name**: "§aLevel Progress"
- **Lore**: Visual progress bar and XP needed

**Total XP** (Slot 16):
- **Material**: NETHER_STAR
- **Name**: "§d§lTotal Experience"
- **Lore**: Lifetime XP accumulated

---

## 5. Death Penalty Detail Menu

**Title**: `"Death Penalty - " + playerName`

### Layout Structure
```
Row 0: [G] [G] [G] [G] [G] [G] [G] [G] [G]  (Border)
Row 1: [G] [OVERVIEW] [GRAVESX] [GRAVES] [INFO1] [INFO2] [INFO3] [INFO4] [G]
Row 2: [G] [ITEM8] [ITEM9] [ITEM10] [ITEM11] [ITEM12] [ITEM13] [ITEM14] [G]
Row 3: [G] [ITEM15] [ITEM16] [ITEM17] [ITEM18] [ITEM19] [ITEM20] [ITEM21] [G]
Row 4: [G] [ITEM22] [ITEM23] [ITEM24] [ITEM25] [ITEM26] [ITEM27] [ITEM28] [G]
Row 5: [G] [G] [G] [PREV] [BACK] [NEXT] [G] [G] [G]  (Navigation)
```

### Display Items

**Death Overview** (Slot 10):
- **Material**: SKELETON_SKULL
- **Name**: "§c§lDeath Statistics"
- **Lore**:
  - Total deaths: X
  - Current penalty: -Y.Y points
  - Penalty per death: 0.5 points
  - Max penalty: 10.0 points

**GravesX Status** (Slot 11):
- **Material**: CHEST (enabled) or BARRIER (disabled)
- **Name**: "§6§lGravesX Integration"
- **Lore**:
  - If enabled:
    - "§a✓ GravesX is active!"
    - "Your items are protected"
    - "Graves expire after X hours"
  - If disabled:
    - "§c✗ GravesX not installed"
    - "Items drop normally on death"

**Active Graves** (Slot 12):
- **Material**: CHEST_MINECART
- **Name**: "§e§lActive Graves"
- **Lore**:
  - Number of active graves
  - Locations (if available)
  - Time until expiration

**How It Works** (Slot 13+):
- **Material**: BOOK
- **Name**: "§b§lHow Death Penalties Work"
- **Lore**:
  - Explanation of penalty calculation
  - How to reduce penalties
  - Time-based decay information
  - Tips for avoiding deaths

---

## Navigation Controls (All Detail Menus)

### Back Button (Slot 49)
- **Material**: BARRIER
- **Name**: "§c§lBack to Main Menu"
- **Action**: Returns to main progress menu

### Previous Page Button (Slot 48)
- **Material**: ARROW
- **Name**: "§e§l← Previous Page"
- **Visibility**: Only shown when page > 1
- **Action**: Go to previous page

### Next Page Button (Slot 50)
- **Material**: ARROW
- **Name**: "§e§lNext Page →"
- **Visibility**: Only shown when more pages exist
- **Action**: Go to next page

### Pagination Details
- **Items Per Page**: 28 (4 rows × 7 columns)
- **Current Page Display**: Shown in button lore "Page X of Y"
- **Auto-Hiding**: Navigation buttons only appear when needed

---

## Color Coding Standards

### Status Colors
- **§a (Green)**: Positive, complete, active, equipped
- **§c (Red)**: Negative, incomplete, danger, penalty
- **§e (Yellow)**: Neutral, information, progress
- **§7 (Gray)**: Disabled, empty, unavailable
- **§6 (Gold)**: Important, valuable, special
- **§b (Aqua)**: Experience, levels, water-related
- **§d (Pink)**: Enchantments, magic, rare

### Tier Colors
- **§7 (Gray)**: Lowest tier
- **§f (White)**: Low tier
- **§a (Green)**: Mid-low tier
- **§2 (Dark Green)**: Mid tier
- **§5 (Purple)**: Mid-high tier
- **§6 (Gold)**: High tier
- **§b (Aqua)**: Very high tier
- **§6§l (Bold Gold)**: Top tier

---

*The GUI provides a beautiful, user-friendly interface while the debug command offers technical details for troubleshooting.*
