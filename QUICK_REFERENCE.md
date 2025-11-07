# WDP Progress Quick Reference

## Progress Scale at a Glance

| Score | Tier | Equipment | Advancements | Economy | Typical Status |
|-------|------|-----------|--------------|---------|----------------|
| 1-10 | Beginner | Wood/Stone | 0-10% | $0-100 | Just joined |
| 11-20 | Beginner | Leather/Iron | 10-20% | $100-1K | First base |
| 21-30 | Novice | Iron | 20-35% | $1K-5K | Nether prep |
| 31-40 | Novice | Iron/Diamond | 35-50% | $5K-10K | Nether explorer |
| 41-50 | Intermediate | Diamond | 50-60% | $10K-25K | Established |
| 51-60 | Intermediate | Diamond+ | 60-70% | $25K-50K | End ready |
| 61-70 | Advanced | Diamond/Netherite | 70-80% | $50K-100K | End explorer |
| 71-80 | Advanced | Netherite | 80-90% | $100K-250K | Boss fighter |
| 81-90 | Expert | Netherite+ | 90-95% | $250K-500K | Near complete |
| 91-99 | Expert | Max gear | 95-99% | $500K-1M | Almost master |
| 100 | Master | Perfect | 100% | $1M+ | Complete mastery |

## Category Quick Guide

### Advancements (25%)
- **Story**: Enter nether, find stronghold, kill dragon
- **Nether**: Netherite, fortress, bastion
- **End**: Dragon, elytra, end city, shulkers
- **Adventure**: Kill mobs, visit biomes
- **Husbandry**: Breed animals, farm crops

**Key Milestones**: Dragon +20, Enter End +15, Elytra +15, Wither +12, Enter Nether +10

### Experience (15%)
- Level 10 = ~30% category score
- Level 30 = ~50% category score  
- Level 50 = ~61% category score
- Level 100 = ~77% category score
- Uses logarithmic scaling

### Equipment (20%)
**Material Values**: Netherite (10) > Diamond (8) > Iron (3) > Stone (1)

**Top Enchantments**: Mending (+50%), Fortune (+40%), Protection (+30%), Looting (+30%)

**Special Items**: Elytra (15), Trident (10), Totem (8)

### Economy (15%)
- $100 = ~20%
- $1,000 = ~30%
- $10,000 = ~40%
- $100,000 = ~50%
- $1,000,000 = 100%
- Uses logarithmic scaling

### Statistics (15%)
**Mob Kill Bonuses**: Dragon (+50), Wither (+40), Warden (+30), Elder Guardian (+15)

**Block Bonuses**: Ancient Debris (+10), Diamond (+5), Emerald (+8)

**Tracked**: Total kills, blocks mined, distance traveled, playtime

### Achievements (10%)
Custom server milestones (configurable)

## Death Penalties

1. **Permanent**: -0.5 per death (max -50)
2. **Temporary**: -2.0 (recovers over 1 hour)
3. **Item Loss**: -30% of lost item values

## Commands

**Players**: `/progress [player]`

**Admins**: `/progressadmin <reload|recalculate|set|reset|debug>`

## API (For Developers)

```java
ProgressAPI api = ((WDPProgressPlugin) Bukkit.getPluginManager()
    .getPlugin("WDPProgress")).getProgressAPI();

// Get progress
double progress = api.getPlayerProgress(player);

// Check threshold
if (api.hasProgress(player, 50.0)) { }

// Grant achievement
api.grantAchievement(player, "achievement_id");

// Listen for changes
@EventHandler
public void onProgressChange(ProgressChangeEvent event) { }
```

## Configuration Essentials

### Adjust Category Weights
```yaml
weights:
  advancements: 25.0  # Increase for survival focus
  equipment: 20.0     # Increase for gear focus
  economy: 15.0       # Increase for economy servers
  statistics: 15.0    # Increase for PvP servers
```

### Tune Death Penalties
```yaml
death-penalty:
  enabled: true
  temporary-penalty:
    amount: 2.0           # Change temporary loss
    recovery-time: 3600   # Change recovery time
  statistics:
    deaths:
      penalty-per-death: 0.5  # Change permanent penalty
```

### Add Custom Achievements
```yaml
achievements:
  custom-achievements:
    my_achievement: 5.0   # Add new achievements
```

## Typical Progression Timeline

| Time Played | Expected Progress | Status |
|-------------|-------------------|--------|
| 0-1 hours | 1-5 | Starting out |
| 1-5 hours | 5-20 | Learning basics |
| 5-10 hours | 20-35 | Iron age |
| 10-20 hours | 35-50 | Diamond tools |
| 20-40 hours | 50-65 | End access |
| 40-80 hours | 65-80 | Netherite gear |
| 80-150 hours | 80-90 | Near complete |
| 150-300+ hours | 90-100 | Mastery |

## Tips for Players

**To Increase Progress:**
1. Complete advancements (especially story, end, nether)
2. Upgrade to better equipment
3. Enchant your gear (especially Mending, Fortune, Protection)
4. Kill special mobs (Dragon, Wither, Warden)
5. Mine valuable blocks (Ancient Debris, Diamonds)
6. Earn money through economy
7. Explore and travel
8. Complete custom achievements

**Avoid Progress Loss:**
1. Don't die (death penalties)
2. Keep backup gear safe
3. Maintain equipment (repair damaged items)
4. Store valuables in ender chest

## Understanding Your Score

**Good Progress for Level**:
- New player (1 hour): 5-10
- Casual player (10 hours): 25-35
- Regular player (50 hours): 50-65
- Dedicated player (100 hours): 70-85
- Master player (300+ hours): 85-100

**If Progress Seems Low**:
1. Check `/progress` breakdown
2. Focus on lowest scoring category
3. Complete more advancements
4. Upgrade equipment
5. Earn more money

**If Progress Seems High**:
Congratulations! You're doing great!

## Common Questions

**Q: Why isn't my progress 100 yet?**  
A: 100 requires near-perfection in ALL categories. It's intentionally very difficult.

**Q: My progress went down, why?**  
A: Death penalties, lost equipment, or damaged gear can reduce progress.

**Q: How often does progress update?**  
A: Every 60 seconds automatically, plus immediately after major events.

**Q: Can I see progress history?**  
A: Yes, via API. Future updates may add UI for this.

**Q: Does AFK farming help?**  
A: No, logarithmic scaling prevents grinding abuse.

---

For full documentation, see:
- **README.md** - Overview and installation
- **API.md** - Developer integration guide  
- **PROGRESS_SCALE.md** - Deep dive into scoring
- **config.yml** - All configuration options