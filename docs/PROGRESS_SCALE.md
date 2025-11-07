# Progress Scale Deep Dive

This document explains the WDP Progress scoring system in detail, helping server administrators understand and customize the progression experience.

## Philosophy

The WDP Progress system is designed to:

1. **Reflect True Progression**: Score represents actual game mastery, not just time played
2. **Prevent Exploitation**: Logarithmic scaling prevents grinding abuse
3. **Encourage Diversity**: Multiple categories reward well-rounded gameplay
4. **Allow Setbacks**: Death and item loss can reduce progress (but it recovers)
5. **Stay Relevant**: Progress continues to matter even at high levels

## Scoring Categories

### 1. Advancements (25% weight)

**Why it matters**: Advancements represent the core progression path Mojang designed.

**How it's calculated**:
- Each advancement category has a weight: Story (35%), End (25%), Nether (20%), Adventure (10%), Husbandry (5%)
- Completion percentage within each category is calculated
- Weighted average gives base score
- Key milestones add bonus points (up to +20)

**Key Milestones**:
| Advancement | Bonus | Why? |
|-------------|-------|------|
| Kill Ender Dragon | +20 | Ultimate vanilla goal |
| Enter the End | +15 | Major progression gate |
| Get Elytra | +15 | Game-changing item |
| Summon Wither | +12 | Boss fight mastery |
| Enter Nether | +10 | First dimension unlock |
| Mine Diamond | +8 | Major tier upgrade |
| Obtain Blaze Rod | +8 | Nether progression |

**Example**:
- Player has completed 80% of Story advancements = 80 × 0.35 = 28 points
- Plus 50% of End advancements = 50 × 0.25 = 12.5 points
- Plus milestone bonuses = +35 points
- **Total Advancement Score: 75.5/100**

### 2. Experience (15% weight)

**Why it matters**: XP levels show time investment and mob farming capability.

**How it's calculated**:
```
score = (log(level + 1) / log(max_level + 1)) × 100
```

**Why logarithmic?**
- Level 10 → 30 progress is easier than level 50 → 70
- Prevents AFKing at mob farms for max score
- Matches Minecraft's XP curve

**Level Benchmarks**:
| Level | Progress | Status |
|-------|----------|--------|
| 10 | ~30% | Early game |
| 20 | ~42% | Established |
| 30 | ~50% | Mid game |
| 50 | ~61% | Advanced |
| 75 | ~70% | Expert |
| 100 | ~77% | Master |

**Milestone Bonuses**: Additional points at specific levels (configurable).

### 3. Equipment (20% weight)

**Why it matters**: Gear quality shows resource gathering and enchanting skill.

**What's evaluated**:
- **Equipped armor** (4 pieces)
- **Inventory items** (best tools, weapons)
- **Ender chest** (safe storage)

**Material Scores**:
| Material | Base Score | Notes |
|----------|------------|-------|
| Netherite | 10.0 | Best in game |
| Diamond | 8.0 | Late game standard |
| Iron | 3.0 | Mid game |
| Gold | 2.5/1.5 | Armor/tools differ |
| Stone | 1.0 | Early game |
| Wood/Leather | 0.5-1.0 | Starting tier |

**Enchantment Multipliers**:
Each enchantment level adds: `base_multiplier (0.15) × level × enchant_value`

| Enchantment | Value | Why? |
|-------------|-------|------|
| Mending | 1.5 | Most valuable |
| Fortune | 1.4 | Resource multiplier |
| Protection | 1.3 | Essential defense |
| Silk Touch | 1.3 | Utility |
| Looting | 1.3 | Drop multiplier |
| Sharpness | 1.2 | Combat essential |
| Unbreaking | 1.2 | Durability |
| Efficiency | 1.1 | QoL improvement |

**Example Calculation**:
Diamond Sword with Sharpness V, Looting III, Unbreaking III:
```
Base: 8.0
Enchant multiplier: 1 + (0.15 × 5 × 1.2) + (0.15 × 3 × 1.3) + (0.15 × 3 × 1.2)
                  = 1 + 0.9 + 0.585 + 0.54
                  = 3.025

Final value: 8.0 × 3.025 = 24.2 points
```

**Durability Impact**:
- Items below 20% durability get 50% penalty
- Broken items don't count
- Encourages maintenance

**Special Items**:
| Item | Score | Why? |
|------|-------|------|
| Elytra | 15.0 | Game-changing mobility |
| Trident | 10.0 | Rare weapon |
| Totem of Undying | 8.0 | Second life |
| Enchanted Golden Apple | 3.0 | Powerful consumable |

### 4. Economy (15% weight)

**Why it matters**: Money shows economic participation and resource accumulation.

**How it's calculated**:
```
score = (log₁₀(balance) / log₁₀(max_balance)) × 100
```

**Why logarithmic?**
- First $100 is much harder than jumping from $900 to $1000
- Prevents inflation exploitation
- Rewards early economic activity

**Balance Curve**:
| Balance | Progress | Difficulty |
|---------|----------|------------|
| $100 | ~20% | Easy to obtain |
| $1,000 | ~30% | Early business |
| $10,000 | ~40% | Established |
| $100,000 | ~50% | Wealthy |
| $1,000,000 | 100% | Server tycoon |

**Milestone Bonuses**: Extra points at key balance thresholds.

### 5. Statistics (15% weight)

**Why it matters**: Stats show actual gameplay engagement.

**Sub-Categories**:

#### Mob Kills (30% weight)
- Base score from total kills with √ diminishing returns
- Special mob bonuses:
  - Ender Dragon: +50 points (per kill)
  - Wither: +40 points
  - Warden: +30 points
  - Elder Guardian: +15 points
  - Evoker: +10 points

#### Blocks Mined (25% weight)
- Total blocks with √ diminishing returns
- Valuable block bonuses:
  - Ancient Debris: +10 points (per block)
  - Diamond Ore: +5 points
  - Emerald Ore: +8 points
  - Obsidian: +2 points

#### Distance Traveled (15% weight)
- All movement types combined
- Logarithmic scaling for exploration
- Elytra travel weighted higher (1.5×)

#### Playtime (10% weight)
- Hours played with logarithmic scaling
- Prevents AFKing for progress

#### Deaths (-5% weight penalty)
- Each death: -0.5 progress points
- Max penalty: -50 points
- Encourages careful play

### 6. Custom Achievements (10% weight)

**Why it matters**: Server-specific goals tailored to your community.

**Default Achievements**:
| Achievement | Points | Trigger |
|-------------|--------|---------|
| first_join | 1.0 | Join server |
| first_death | 0.5 | Die once |
| first_pvp_kill | 3.0 | Kill a player |
| join_faction | 5.0 | Join a faction |
| complete_tutorial | 5.0 | Finish tutorial |
| master_builder | 10.0 | Build something amazing |
| legendary_fighter | 15.0 | PvP mastery |
| master_miner | 10.0 | Mining mastery |
| master_enchanter | 12.0 | Enchanting mastery |

**Adding Custom Achievements**:
1. Define in `config.yml`:
```yaml
achievements:
  custom-achievements:
    my_achievement: 5.0
```

2. Grant via API:
```java
progressAPI.grantAchievement(player, "my_achievement");
```

## Death Penalties

Death affects progress in multiple ways:

### 1. Permanent Death Count Penalty
- Each death: -0.5 points (configurable)
- Cumulative but capped at -50 max
- Never resets (encourages careful play)

### 2. Temporary Death Penalty
- Immediate: -2.0 points (configurable)
- Recovers linearly over 1 hour (configurable)
- Simulates the "respawn penalty" feeling

### 3. Item Loss Penalty
- Lost items reduce equipment score
- 30% of lost item value deducted (configurable)
- Encourages keeping backup gear

### 4. Experience Loss Impact
- XP lost on death affects experience score
- 50% impact (configurable)
- Vanilla mechanic integrated

## Practical Examples

### Example 1: New Player (Progress: 5/100)
- **Advancements**: 2/100 (only first few completed)
- **Experience**: 1/100 (level 3)
- **Equipment**: 3/100 (stone tools, leather armor)
- **Economy**: 2/100 ($50)
- **Statistics**: 1/100 (minimal activity)
- **Achievements**: 1/100 (first_join only)

**Weighted Total**: ~5/100 ✓

### Example 2: Mid-Game Player (Progress: 45/100)
- **Advancements**: 40/100 (story + some nether)
- **Experience**: 50/100 (level 30)
- **Equipment**: 55/100 (iron/diamond mix, some enchants)
- **Economy**: 35/100 ($5,000)
- **Statistics**: 45/100 (active player)
- **Achievements**: 30/100 (completed tutorial, joined faction)

**Weighted Total**: ~45/100 ✓

### Example 3: End-Game Player (Progress: 85/100)
- **Advancements**: 90/100 (most completed + milestones)
- **Experience**: 75/100 (level 75)
- **Equipment**: 95/100 (full netherite, max enchants)
- **Economy**: 80/100 ($250,000)
- **Statistics**: 85/100 (extensive play)
- **Achievements**: 90/100 (most custom achievements)
- **Death Penalty**: -5 (10 deaths)

**Weighted Total**: ~85/100 ✓

## Tuning Your Server

### Making Progress Easier
1. Lower death penalties
2. Increase equipment component weights toward inventory
3. Add more custom achievements with higher values
4. Increase economy milestone bonuses
5. Lower max_balance for economy

### Making Progress Harder
1. Increase death penalties
2. Require higher equipment standards
3. Make custom achievements rarer
4. Increase max_balance for economy
5. Increase diminishing returns factors

### Emphasizing Different Playstyles

**PvP Server**: Increase statistics weight (mob kills), add PvP achievements

**Economy Server**: Increase economy weight, lower advancement weight

**Survival Server**: Keep defaults or increase advancement weight

**Creative Building**: Add building achievements, lower statistics weight

## Configuration Tips

```yaml
# Example: PvP-focused server
weights:
  advancements: 15.0      # Less important
  experience: 15.0        # Moderate
  equipment: 25.0         # Very important (gear matters)
  economy: 10.0           # Less important
  statistics: 25.0        # Very important (kills)
  achievements: 10.0      # Moderate

# Add PvP achievements
achievements:
  custom-achievements:
    first_pvp_kill: 5.0
    kill_streak_5: 8.0
    kill_streak_10: 12.0
    legendary_fighter: 20.0
```

## Frequently Asked Questions

**Q: Why can't I reach 100 progress?**
A: You need near-perfect scores in all categories. It's designed to be very difficult.

**Q: My progress went down, why?**
A: Death penalties, lost equipment, or XP loss can decrease progress.

**Q: Does AFK farming work?**
A: No, logarithmic scaling prevents it. The formula gives diminishing returns.

**Q: How often is progress recalculated?**
A: Automatically every 60 seconds (configurable) and on major events (advancement, death, etc.)

**Q: Can progress go below 1?**
A: No, it's clamped to the minimum (usually 1).

**Q: What happens if I delete my items?**
A: Your equipment score decreases, lowering overall progress.

---

*This system is designed to be fair, balanced, and representative of true game mastery. Customize it to fit your server's unique gameplay!*