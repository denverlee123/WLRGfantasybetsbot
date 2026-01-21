# Hytale API Discovery Needed

## What I Fixed

Based on the compilation errors from your actual Hytale server JAR, I made these critical fixes:

### 1. Logger API ✅
**Problem:** `HytaleLogger` doesn't have `.info()` or `.severe()` methods
**Fix:** Replaced with `System.out.println()` and `System.err.println()`

### 2. Command API ❌
**Problem:** `com.hypixel.hytale.server.core.command.Command` doesn't exist
**Status:** Commands temporarily disabled - need to find correct API

### 3. Event API ✅
**Problem:** `PlayerChatEvent` is not an async event
**Fix:** Removed the async chat event handler

## Current Status

The plugin **should now compile successfully** on your machine! But it's in a minimal state:

### ✅ What Works
- Plugin loads and unloads properly
- Player connect/disconnect events
- Configuration system (creates default kits)
- Player data tracking (stats storage)

### ❌ What's Missing
- **All commands** (need to discover correct command API)
- Full game logic (arenas, countdown, PvP)
- Kit giving system

## Next Steps

### Step 1: Try Building Again

```cmd
cd C:\Users\idark\OneDrive\Desktop\WLRGfantasybetsbot-claude-hytale-survival-plugin-n36c1\hytale-survival-games
gradlew.bat clean build
```

If it compiles successfully, you should see:
```
BUILD SUCCESSFUL
```

And find the JAR at:
```
build/libs/survival-games-1.0.0.jar
```

### Step 2: Test on Your Server

1. **Copy the JAR** to your Hytale server's `mods/` folder
2. **Start the server**
3. **Check the console** - you should see:
   ```
   [SurvivalGames] Setting up Survival Games plugin...
   [SurvivalGames] Survival Games setup complete!
   [SurvivalGames] Starting Survival Games plugin...
   [SurvivalGames] Survival Games plugin started successfully!
   [SurvivalGames] Loaded 3 kits
   ```

4. **Join the server** - you should see:
   ```
   [SurvivalGames] Player connected: YourUsername
   ```

### Step 3: Help Me Discover the Command API

To add commands back, I need to find out how Hytale's command system actually works. Can you help by doing ONE of these:

#### Option A: Extract Server Classes (Recommended)
If you have a tool like **JD-GUI** or **IntelliJ IDEA**:

1. Open your `hytale-server.jar` with JD-GUI or IntelliJ
2. Navigate to `com.hypixel.hytale.server.core`
3. Look for any packages related to commands (like `command`, `commands`, `api.command`, etc.)
4. Take screenshots or copy the class names you find
5. Share with me

#### Option B: Use JAR Explorer
```cmd
jar tf hytale-server.jar | findstr /i "command"
```

This will list all files in the JAR containing "command" in their path.

#### Option C: Try a Decompiler Online
1. Upload `hytale-server.jar` to https://www.javadecompilers.com/
2. Search for "command" in the file tree
3. Share what you find

## What I Need to Know

To implement commands, I need answers to these questions:

1. **Where is the command API?**
   - Is it in a different package?
   - Is it called something else (like `GameCommand`, `HytaleCommand`, etc.)?

2. **How do you register commands?**
   - Is there a `CommandRegistry`?
   - Do you extend a base class or implement an interface?

3. **What's the command structure?**
   - How do you get the command sender?
   - How do you access arguments?
   - How do you send messages back?

Once we figure out the command API, I can quickly add back all the commands (sg, kit, stats, etc.) and then build out the full Survival Games functionality!

## Configuration

The plugin creates a config file at `plugins/survival-games/config.json` with 3 default kits:
- **Warrior** - Balanced fighter with sword and armor
- **Archer** - Master of ranged combat
- **Tank** - Heavy armor and survivability

You can edit this file to customize kits, player limits, countdown timers, etc.
