# Progress Menu GUI Layout

```
┌─────────────────────────────────────────────────────────────────────┐
│ ⚡ PlayerName's Progress ⚡                                          │
├─────────────────────────────────────────────────────────────────────┤
│ [█][█][█][█][⭐ Overall Progress: 67.5/100 ⭐][█][█][█][█]          │
│ [█][ ][ ][ ][ ][ ][ ][ ][█]                                         │
│ [█][📖 Advancements][█][✨ Experience][█][⚔ Equipment][█][💰 Economy][█] │
│ [█][ ][ ][ ][ ][ ][ ][ ][█]                                         │
│ [█][⚡ Statistics][█][⭐ Achievements][█][💀 Deaths][█][💡 Tips][█]     │
│ [█][ ][ ][ ][ ][ ][ ][ ][█]                                         │
│ [❓ Explain][ ][ ][ ][📊 History][ ][ ][ ][✖ Close]                │
└─────────────────────────────────────────────────────────────────────┘
```

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
- **Slot 10**: 📖 **Advancements** (Book)
- **Slot 20**: Empty (spacing)
- **Slot 12**: ✨ **Experience** (Experience Bottle)
- **Slot 21**: Empty (spacing)
- **Slot 14**: ⚔ **Equipment** (Diamond Chestplate)
- **Slot 22**: Empty (spacing)
- **Slot 16**: 💰 **Economy** (Gold Ingot)
- Slot 26: Gray glass pane border

### Row 4 (27-35): Border
- Slot 27: Gray glass pane border
- Slots 28-34: Empty
- Slot 35: Gray glass pane border

### Row 5 (36-44): Category Items (Bottom Row)
- Slot 36: Gray glass pane border
- **Slot 37**: Empty (spacing)
- **Slot 28**: ⚡ **Statistics** (Diamond Sword)
- **Slot 38**: Empty (spacing)
- **Slot 30**: ⭐ **Achievements** (Nether Star)
- **Slot 39**: Empty (spacing)
- **Slot 32**: 💀 **Death Penalties** (Skeleton Skull)
- **Slot 40**: Empty (spacing)
- **Slot 34**: 💡 **Tips** (Writable Book)
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

*The GUI provides a beautiful, user-friendly interface while the debug command offers technical details for troubleshooting.*
