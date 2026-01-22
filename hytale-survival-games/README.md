# Hytale Survival Games Plugin

A fully-featured Survival Games (Hunger Games) minigame plugin for Hytale with kit selection, multiple arenas, countdown system, spectator mode, and more!

## Features

### Core Features
- **Kit Selection System**: Players can choose from multiple kits in the lobby before the game starts
- **Multiple Arena Support**: Create and manage multiple arenas with independent games
- **Join/Leave System**: Easy commands for players to join and leave games
- **Countdown System**: Configurable countdown before game starts (default: 30 seconds)
- **Player Limit**: Configurable minimum (default: 2) and maximum (default: 24) players
- **Lobby Teleportation**: Players are automatically teleported back to the lobby after death/game end
- **Easy Kit Management**: Simple commands to create and delete kits without editing config files

### Advanced Features
- **Grace Period**: Configurable grace period at game start where PvP is disabled (default: 30 seconds)
- **Border Shrinking**: Arena border shrinks over time, dealing damage to players outside it
- **Spectator Mode**: Eliminated players become spectators and can watch the rest of the game
- **Statistics Tracking**: Track player wins, kills, deaths, K/D ratio, and win rate
- **Multiple Spawn Points**: Support for multiple spawn points per arena
- **Arena Boundaries**: Define arena boundaries to contain the game
- **Admin Commands**: Full control over games with force start/stop commands

## Requirements

- **Hytale Server** (Early Access or later)
- **Java 21** or higher
- **Gradle** (included in project)

## Installation

### Step 1: Download or Build the Plugin

#### Option A: Build from Source
```bash
cd hytale-survival-games
./gradlew build
```
The compiled JAR will be in `build/libs/survival-games-1.0.0.jar`

#### Option B: Use Pre-built JAR
If you received a pre-built JAR file, skip to Step 2.

### Step 2: Install on Your Server

1. Stop your Hytale server if it's running
2. Copy `survival-games-1.0.0.jar` to your server's `plugins/` folder
3. Start your Hytale server
4. The plugin will create a `plugins/survival-games/` folder with a `config.json` file

### Step 3: Verify Installation

Once your server is running, type:
```
/sg info
```
You should see the plugin information displayed.

## Configuration

The plugin creates a `config.json` file in `plugins/survival-games/` with these settings:

```json
{
  "minPlayers": 2,              // Minimum players to start game
  "maxPlayers": 24,             // Maximum players per game
  "countdownSeconds": 30,       // Countdown before game starts
  "gracePeriodSeconds": 30,     // Grace period with no PvP
  "borderShrinkStart": 300,     // Seconds before border starts shrinking
  "borderShrinkSpeed": 1.0,     // Border shrink speed multiplier
  "chestRefillInterval": 180,   // Seconds between chest refills
  "kits": { ... }               // Kit definitions (see below)
}
```

### Default Kits

The plugin comes with 5 pre-configured kits:

1. **Warrior** - Balanced fighter with sword and armor
2. **Archer** - Master of ranged combat with extra arrows
3. **Tank** - Heavy armor and high survivability
4. **Assassin** - Fast and deadly with speed potions
5. **Berserker** - High damage with battle axe

## Setting Up Your First Arena

### 1. Create the Arena and Set Lobby Spawn

First, go to where you want the lobby to be and run:
```
/setlobby <arena_name>
```
Example: `/setlobby arena1`

This creates the arena and sets the lobby spawn point where players will wait before the game starts.

### 2. Add Player Spawn Points

Go to each location where you want players to spawn when the game starts and run:
```
/setarena <arena_name> addspawn
```
Example: `/setarena arena1 addspawn`

Add at least as many spawn points as your maximum players (recommended: 24+ spawn points).

### 3. Set Arena Boundaries

The arena needs two corner points to define its boundaries:

Stand at one corner of your arena and run:
```
/setarena <arena_name> corner1
```

Stand at the opposite corner and run:
```
/setarena <arena_name> corner2
```

Example:
```
/setarena arena1 corner1
/setarena arena1 corner2
```

### 4. Verify Arena Setup

Check if your arena is ready:
```
/sg list
```

You should see:
```
Available Arenas:
- arena1 ✓ Ready
```

If not ready, the command will tell you what's missing.

## Player Commands

### Basic Commands
- `/sg help` - Show help message
- `/sg info` - Show plugin information
- `/sg list` - List all available arenas
- `/join <arena>` - Join a game in the specified arena
- `/leave` - Leave your current game
- `/kit [kit_name]` - View available kits or select a kit
- `/stats [player]` - View your (or another player's) statistics

### How to Play

1. **Join a game**: `/join arena1`
2. **Select a kit**: `/kit warrior` (or archer, tank, assassin, berserker)
3. **Wait for countdown**: Game starts when minimum players join
4. **Survive**: Last player alive wins!
5. **Leave anytime**: `/leave`

## Admin Commands

All admin commands require the `survivalgames.admin` permission.

### Arena Management
- `/setlobby <arena>` - Set lobby spawn for an arena
- `/setarena <arena> addspawn` - Add a spawn point to the arena
- `/setarena <arena> clearspawns` - Clear all spawn points
- `/setarena <arena> corner1` - Set first corner of arena boundary
- `/setarena <arena> corner2` - Set second corner of arena boundary
- `/setarena <arena> delete` - Delete an arena

### Kit Management
- `/createkit <id> <name> <description> <icon>` - Create a new kit from your inventory
- `/deletekit <kit_id>` - Delete a kit

#### Creating a New Kit

1. Put all the items you want in the kit in your inventory
2. Run the command with kit details:
```
/createkit mage "Mage" "Master of magic" hytale:items/weapons/staff/wooden_staff
```

The kit will be saved with all items currently in your inventory!

#### Deleting a Kit

```
/deletekit mage
```

### Game Control
- `/forcestart <arena>` - Force start a game (skip countdown)
- `/forcestop <arena>` - Force stop an active game

## Permissions

- `survivalgames.admin` - Access to all admin commands
- Default: All players can use player commands

## Game Flow

1. **Waiting Phase**
   - Players join with `/join <arena>`
   - Players select kits with `/kit <name>`
   - Game starts countdown when minimum players join

2. **Countdown Phase**
   - 30 second countdown (configurable)
   - Players can still select kits
   - Players are notified when game is starting

3. **Grace Period**
   - Players spawn at random spawn points
   - Kits are given automatically
   - PvP is disabled for 30 seconds (configurable)
   - Players can gather resources

4. **Active Game**
   - PvP enabled
   - Border starts shrinking after 5 minutes (configurable)
   - Players outside border take damage
   - Last player alive wins!

5. **Game End**
   - Winner is announced
   - Statistics are updated
   - Players teleport back to lobby
   - Arena resets for next game

## Advanced Features

### Border Shrinking
- Border starts shrinking after `borderShrinkStart` seconds (default: 300)
- Shrinks every 30 seconds
- Players outside border take 2 hearts of damage
- Forces players to move toward center

### Statistics Tracking
- Games Played
- Wins
- Kills
- Deaths
- K/D Ratio
- Win Rate

View with `/stats` command!

### Spectator Mode
- Dead players become spectators
- Can watch rest of game
- Cannot interact or deal damage
- Automatically teleported to lobby at game end

## Troubleshooting

### Arena not starting?
- Check minimum players requirement (default: 2)
- Verify arena setup with `/sg list`
- Make sure lobby spawn, spawn points, and corners are set

### Players taking damage in lobby?
- Make sure you set the lobby spawn with `/setlobby`
- Check that players aren't outside arena boundaries

### Kit not working?
- Verify kit exists with `/kit` (no arguments)
- Make sure you're in a game before selecting a kit
- Check config.json for kit definitions

### Plugin not loading?
- Verify Java 21+ is installed
- Check server logs for errors
- Ensure JAR is in `plugins/` folder
- Make sure no conflicting plugins

## Item IDs

When creating custom kits, use proper Hytale item IDs. Here are some examples:

### Weapons
- `hytale:items/weapons/swords/one_handed/iron_one_handed_sword`
- `hytale:items/weapons/daggers/iron_dagger`
- `hytale:items/weapons/axes/battleaxe/iron_battleaxe`
- `hytale:items/weapons/bows/shortbow/wooden_shortbow`

### Armor
- `hytale:items/armor/helmets/iron_helmet`
- `hytale:items/armor/chestplates/iron_chestplate`
- `hytale:items/armor/leggings/iron_leggings`
- `hytale:items/armor/boots/iron_boots`

### Consumables
- `hytale:items/food/cooked_meat`
- `hytale:items/potions/health_potion`
- `hytale:items/potions/speed_potion`
- `hytale:items/potions/strength_potion`

### Ammo
- `hytale:items/ammo/arrows/iron_arrow`

## Support

For issues, questions, or contributions:
- Check server logs for errors
- Report issues on the repository
- Join the Hytale community

## Credits

**Author**: WLRGfantasybetsbot
**Version**: 1.0.0
**License**: MIT

Built with the Hytale Plugin API

---

## Quick Start Example

```bash
# 1. Set up arena
/setlobby arena1
/setarena arena1 addspawn  # Run this 24 times at different locations
/setarena arena1 corner1
/setarena arena1 corner2

# 2. Verify setup
/sg list

# 3. Players join
/join arena1
/kit warrior

# 4. Game auto-starts when 2+ players join!
# 5. Last player alive wins!
```

Enjoy your Survival Games! 🎮
