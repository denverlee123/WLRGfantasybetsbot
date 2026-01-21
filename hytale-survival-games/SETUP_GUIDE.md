# Hytale Survival Games - Quick Setup Guide

## 📋 Prerequisites

Before starting, make sure you have:
- ✅ A running Hytale server
- ✅ Java 21 or higher installed
- ✅ Admin/OP permissions on the server

## 🚀 Installation (5 minutes)

### Step 1: Build the Plugin

Open terminal/command prompt in the plugin folder and run:

```bash
# On Linux/Mac:
./gradlew build

# On Windows:
gradlew.bat build
```

**Output**: `build/libs/survival-games-1.0.0.jar`

### Step 2: Install the Plugin

1. **Stop** your Hytale server
2. **Copy** `survival-games-1.0.0.jar` to your server's `plugins/` folder
3. **Start** your server
4. Check server console for: `"Survival Games Plugin enabled successfully!"`

### Step 3: Verify Installation

In-game, run:
```
/sg info
```

✅ If you see plugin info, you're ready to go!

## 🎮 Arena Setup (10 minutes)

Follow these steps **in order** to create your first arena:

### 1️⃣ Set Lobby Spawn
Stand where players should wait before the game starts:
```
/setlobby arena1
```
📍 **This is where players spawn when they join and after they die**

### 2️⃣ Add Spawn Points
Go to each location where players should spawn at game start:
```
/setarena arena1 addspawn
```

**Important**:
- Add at least **24 spawn points** (one for each max player)
- Spread them out evenly around your map
- Run this command 24 times at different locations

💡 **Tip**: Walk around your map and keep running the command!

### 3️⃣ Set Arena Boundaries

#### Corner 1
Stand at one corner of your arena (e.g., Northwest):
```
/setarena arena1 corner1
```

#### Corner 2
Stand at the opposite corner (e.g., Southeast):
```
/setarena arena1 corner2
```

📐 **These corners define your play area and border**

### 4️⃣ Verify Setup
```
/sg list
```

You should see:
```
Available Arenas:
- arena1 ✓ Ready
```

✅ **If it says "Ready", your arena is complete!**

❌ **If it says "Not Setup", it will tell you what's missing**

## 🎯 Testing Your Arena (2 minutes)

### Solo Test (Force Start)
```
/join arena1
/kit warrior
/forcestart arena1
```

### Full Test (2+ Players)
1. Player 1: `/join arena1` and `/kit warrior`
2. Player 2: `/join arena1` and `/kit archer`
3. Game auto-starts after 30 seconds!

## 🛠️ Creating Custom Kits

### Method 1: In-Game (Recommended)

1. **Fill your inventory** with items you want in the kit
2. Run the command:
```
/createkit sniper "Sniper" "Long range specialist" hytale:items/weapons/bows/shortbow/wooden_shortbow
```

Format:
```
/createkit <id> "<name>" "<description>" <icon_item_id>
```

### Method 2: Edit Config File

1. Stop your server
2. Open `plugins/survival-games/config.json`
3. Add your kit under `"kits"`:

```json
"sniper": {
  "name": "Sniper",
  "description": "Long range specialist",
  "icon": "hytale:items/weapons/bows/shortbow/wooden_shortbow",
  "items": [
    "hytale:items/weapons/bows/shortbow/wooden_shortbow:1",
    "hytale:items/ammo/arrows/iron_arrow:64",
    "hytale:items/armor/helmets/leather_helmet:1"
  ]
}
```

4. Save and restart server

### Deleting Kits
```
/deletekit sniper
```

## 📊 Configuration

Edit `plugins/survival-games/config.json`:

```json
{
  "minPlayers": 2,              // Min players to start
  "maxPlayers": 24,             // Max players per game
  "countdownSeconds": 30,       // Countdown duration
  "gracePeriodSeconds": 30,     // No PvP time at start
  "borderShrinkStart": 300,     // When border starts shrinking (seconds)
  "borderShrinkSpeed": 1.0,     // How fast border shrinks
  "chestRefillInterval": 180    // Chest refill time (future feature)
}
```

💡 **Changes require server restart**

## 🎮 Player Guide

Share these commands with your players:

### Essential Commands
```
/join arena1          # Join the game
/kit warrior          # Select a kit
/leave                # Leave the game
/stats                # View your statistics
```

### Available Kits (Default)
- `warrior` - Balanced fighter
- `archer` - Ranged specialist
- `tank` - Heavy armor
- `assassin` - Speed & stealth
- `berserker` - High damage

## 🔧 Common Issues & Solutions

### ❌ "Arena not properly setup"
**Solution**: Run `/sg list` to see what's missing
- Missing lobby spawn? → `/setlobby arena1`
- No spawn points? → `/setarena arena1 addspawn` (24 times)
- Missing corners? → `/setarena arena1 corner1` and `corner2`

### ❌ Game won't start
**Solution**: Check minimum players
- Default requires 2 players
- Change in config: `"minPlayers": 1` for solo testing

### ❌ "You are already in a game"
**Solution**: Leave current game first
```
/leave
```

### ❌ Kit not working
**Solution**: Make sure you're in a game
```
/join arena1
/kit warrior
```

### ❌ Players spawning in wrong location
**Solution**: Reset spawn points
```
/setarena arena1 clearspawns
/setarena arena1 addspawn   # Add them again correctly
```

## 🎯 Game Flow Overview

```
1. Players Join → 2. Select Kits → 3. Countdown (30s) →
4. Grace Period (30s, no PvP) → 5. Active Game →
6. Border Shrinks → 7. Last Player Wins!
```

## 📱 Quick Reference Card

### Admin Setup
| Command | Purpose |
|---------|---------|
| `/setlobby <arena>` | Set waiting area |
| `/setarena <arena> addspawn` | Add spawn point |
| `/setarena <arena> corner1` | Set boundary corner 1 |
| `/setarena <arena> corner2` | Set boundary corner 2 |
| `/sg list` | Check setup status |

### Game Control
| Command | Purpose |
|---------|---------|
| `/forcestart <arena>` | Start game now |
| `/forcestop <arena>` | Stop active game |

### Kit Management
| Command | Purpose |
|---------|---------|
| `/createkit ...` | Create new kit |
| `/deletekit <id>` | Remove kit |

## 🎉 You're Ready!

Your Survival Games plugin is now set up and ready for players!

**Need help?** Check the full README.md for detailed documentation.

---

**Pro Tips:**
- Test with `/forcestart` before opening to players
- Create multiple arenas for different player counts
- Customize kits to match your server theme
- Monitor first few games to adjust settings

**Good luck and have fun! 🎮**
