# WDP Progress - Quick Start Guide

## 🚀 Getting Started

### Installation
1. Download `WDPProgress-1.1.0.jar`
2. Place in your server's `plugins/` folder
3. Restart your server
4. Plugin will auto-configure with SQLite

### First Steps
```
/progress              # View your progress (opens beautiful GUI)
/progress debug        # See technical details (for troubleshooting)
```

---

## 📖 Player Guide

### Viewing Your Progress

**Command:** `/progress` or `/prog`

Opens an **interactive menu** showing:
- 📊 Your overall progress score (1-100)
- 🎯 Progress tier (Beginner → Master)
- 📈 6 category breakdowns
- 💡 Personalized tips
- ❓ System explanation

**Hover over items** for detailed information!  
**Click the barrier** to close the menu.

### Understanding Your Score

Your progress (1-100) is calculated from **6 categories**:

| Category | Weight | What It Tracks |
|----------|--------|----------------|
| 📖 **Advancements** | 25% | Story progression, achievements |
| ⚔ **Equipment** | 20% | Quality of your gear |
| ✨ **Experience** | 15% | Your XP levels |
| 💰 **Economy** | 15% | Your money/wealth |
| ⚡ **Statistics** | 15% | Mobs killed, blocks mined, etc. |
| ⭐ **Achievements** | 10% | Custom server achievements |

### Progress Tiers

| Score | Tier | Description |
|-------|------|-------------|
| 1-20 | 🔴 **Beginner** | Just starting out |
| 21-40 | 🟠 **Novice** | Making progress |
| 41-60 | 🟡 **Intermediate** | Well established |
| 61-80 | 🟢 **Advanced** | Highly skilled |
| 81-99 | 🔵 **Expert** | Master player |
| 100 | 🟣 **Master** | Complete mastery! |

### How to Improve

**General Tips:**
1. Complete Minecraft advancements (highest impact!)
2. Upgrade your equipment to diamond/netherite
3. Gain experience levels through gameplay
4. Participate in the economy
5. Stay active (explore, mine, fight)
6. Complete custom achievements
7. **Avoid dying** (deaths reduce your progress!)

**Check the GUI menu** for specific tips based on YOUR progress!

---

## 🔧 Admin Guide

### Basic Commands

```bash
/progress <player>                    # View player's progress (GUI)
/progress debug <player>              # Technical breakdown
/progressadmin reload                 # Reload config
/progressadmin recalculate <player>   # Force recalculation
/progressadmin set <player> <score>   # Set progress manually
/progressadmin reset <player>         # Reset player data
```

### Permissions

**Player Permissions:**
- `wdp.progress.view` - View own progress (default: true)
- `wdp.progress.view.others` - View other players (default: op)
- `wdp.progress.debug` - Use debug command (default: op)

**Admin Permissions:**
- `wdp.progress.admin` - All admin commands (default: op)
- `wdp.progress.admin.reload` - Reload config
- `wdp.progress.admin.recalculate` - Force recalculations
- `wdp.progress.admin.set` - Set progress manually
- `wdp.progress.admin.reset` - Reset player data
- `wdp.progress.admin.debug` - Use admin debug command

### Quick Configuration

Edit `plugins/WDPProgress/config.yml`:

```yaml
# Adjust category weights (must sum to 100!)
weights:
  advancements: 25.0
  experience: 15.0
  equipment: 20.0
  economy: 15.0
  statistics: 15.0
  achievements: 10.0

# Disable a category
categories:
  advancements:
    enabled: false    # Turn off advancement tracking

# Adjust death penalties
death-penalty:
  enabled: true
  permanent-penalty: 0.5      # Points lost per death (permanent)
  temporary-penalty:
    amount: 2.0               # Temporary penalty amount
    recovery-time: 3600       # Seconds to recover (1 hour)
```

**After editing:** `/progressadmin reload`

---

## 🐛 Troubleshooting

### Problem: Player's score seems wrong

**Solution:** Use debug command to investigate
```
/progress debug <player>
```
This shows:
- Raw category scores
- Weight calculations
- What's contributing to each category
- Death penalties
- Formula breakdown

### Problem: Economy shows 0

**Cause:** Vault plugin not installed or no economy plugin

**Solution:** Install Vault + economy plugin (like EssentialsX)

### Problem: Advancements not tracking

**Check:**
1. Is advancements category enabled in config?
2. Run `/progressadmin recalculate <player>`
3. Check debug output for advancement score

### Problem: GUI not opening

**Check:**
1. Player has `wdp.progress.view` permission
2. Check console for errors
3. Try `/progressadmin reload`

### Problem: Deaths not affecting score

**Check config.yml:**
```yaml
death-penalty:
  enabled: true    # Must be true
```

---

## 📊 Debug Command Guide

### When to Use Debug

Use `/progress debug <player>` when:
- Player complains about incorrect score
- Need to verify calculation accuracy
- Want to see raw numbers
- Troubleshooting category issues
- Understanding specific contributions

### Reading Debug Output

```
FINAL SCORE: 67.5/100          # Overall progress

Advancements:
  Score: 75.0/100               # Raw category score (0-100)
  Weight: 25%                   # Category importance
  Contribution: +18.8 points    # How much this adds to final
```

**Formula:**
```
Final = (Adv×25% + Exp×15% + Equip×20% + Econ×15% + Stats×15% + Ach×10%) - Deaths
```

---

## 💡 Pro Tips

### For Players
1. **Focus on Advancements** - They have the highest weight (25%)!
2. **Keep Backup Gear** - Equipment loss reduces score
3. **Don't Rush** - Progress uses diminishing returns
4. **Play Diversely** - All categories contribute
5. **Check Tips** - GUI shows personalized advice

### For Admins
1. **Monitor with Debug** - Quickly identify issues
2. **Adjust Weights** - Customize for your server's playstyle
3. **Use Recalculate** - After major changes
4. **Check History** - Track player progression over time
5. **Set Reasonable Goals** - Score 100 is VERY hard!

---

## 🎯 Common Questions

**Q: Why can't I reach 100?**  
A: Score 100 represents complete mastery of ALL aspects. It's designed to be very rare!

**Q: Why did my score go down?**  
A: Deaths apply penalties, and lost equipment reduces your score temporarily.

**Q: How often does progress update?**  
A: Automatically on relevant actions (advancement, death, etc.) and on join.

**Q: Can I see historical data?**  
A: History feature coming soon! Currently shows current snapshot only.

**Q: Does the plugin lag the server?**  
A: No! Calculations run asynchronously and use caching for performance.

**Q: Can other plugins use progress data?**  
A: Yes! WDP Progress provides a full API for other plugins.

---

## 🔗 Resources

- **Full Documentation:** `README.md`
- **API Guide:** `docs/API.md`
- **Progress Scale:** `docs/PROGRESS_SCALE.md`
- **GUI Layout:** `GUI_LAYOUT.md`
- **Update Log:** `UPDATE_LOG_1.1.0.md`

---

## 📞 Support

Need help? Check the debug command first:
```
/progress debug <player>
```

Still stuck? Review the documentation or contact your server administrator.

---

**Version:** 1.1.0  
**Minecraft:** 1.21.6  
**Java:** 21+

**Made with ❤️ for the WDP Server community**
