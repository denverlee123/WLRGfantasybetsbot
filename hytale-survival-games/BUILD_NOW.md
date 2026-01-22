# 🎮 Building the Hytale Survival Games Plugin

## ✅ You Have the Actual Hytale API!

Great news! Since you have **official Hytale Early Access**, you can build this plugin right now.

---

## 🔧 Quick Build Steps

### **Step 1: Find Your Hytale Server JAR**

You mentioned you have `server.aot`, but you also need the **JAR file**. Look in your Hytale server folder for files like:

```
C:\Path\To\Hytale\server\
├── server.aot          (You found this)
├── server.jar          (Look for this!)
├── lib/
│   └── hytale-*.jar    (Or here!)
└── mods/
```

**Try searching for:**
- `server.jar`
- `hytale-server.jar`
- Any `.jar` file in the `lib/` folder

The file you need contains `com.hypixel.hytale.*` classes.

---

### **Step 2: Update build.gradle**

Open `build.gradle` and find line 22:

```gradle
compileOnly files('hytale-server.jar')
```

**Choose ONE option:**

**Option A - Copy JAR here:**
1. Copy your `server.jar` (or whatever it's named) to the `hytale-survival-games/` folder
2. Rename it to `hytale-server.jar`
3. Keep line 22 as is

**Option B - Use full path:**
1. Comment out line 22 (add `//` at the start)
2. Uncomment line 25 and update it:
```gradle
compileOnly files('C:/Users/idark/path/to/your/server.jar')
```

⚠️ **Use forward slashes `/` even on Windows!**

---

### **Step 3: Build the Plugin**

Open PowerShell/Command Prompt in the `hytale-survival-games` folder:

```bash
# Clean previous builds
gradlew.bat clean

# Build the plugin
gradlew.bat build
```

**If it works**, you'll see:
```
BUILD SUCCESSFUL in Xs
```

**Your plugin JAR will be at:**
```
hytale-survival-games/build/libs/survival-games-1.0.0.jar
```

---

### **Step 4: Install the Plugin**

1. **Copy the JAR**:
   ```
   hytale-survival-games/build/libs/survival-games-1.0.0.jar
   ```

2. **Paste it into your Hytale mods folder**:
   ```
   C:\Path\To\Hytale\mods\survival-games-1.0.0.jar
   ```

3. **Restart your Hytale server**

4. **Check if it loaded**:
   - Look in server console for: `"Starting Survival Games plugin..."`
   - In-game, type: `/sg info`

---

## 📋 What This Version Does

This is a **working starter version** with:

✅ **Kit System** - 3 default kits (Warrior, Archer, Tank)
- `/kit` - List kits
- `/kit <name>` - Select a kit

✅ **Statistics Tracking**
- `/stats` - View your game stats
- Tracks: Games played, wins, kills, deaths, K/D, win rate

✅ **Player Events**
- Welcome messages on join
- Proper event handling

✅ **Configuration System**
- Saves to `plugins/survival-games/config.json`
- Easy to edit and add more kits

---

## 🚀 What's NOT Implemented Yet

This is a **simplified version** to get you started. Missing features:

❌ Full game logic (joining arenas, countdown, PvP)
❌ Arena management
❌ Death handling
❌ Border shrinking
❌ Spectator mode

**Why?** Because I wanted to give you something that **compiles and runs** so you can:
1. Test that the plugin loads
2. Verify commands work
3. See the event system in action

---

## 🔍 Troubleshooting

### ❌ "package com.hypixel.hytale does not exist"

**Problem**: build.gradle isn't pointing to your server JAR correctly.

**Fix**:
1. Double-check the path in build.gradle line 22 or 25
2. Make sure the file exists: `dir "C:\path\to\your\server.jar"`
3. Try extracting your JAR to check contents:
   ```cmd
   jar tf server.jar | findstr "hypixel"
   ```

If you see `com/hypixel/hytale/` classes, you have the right JAR!

---

### ❌ "Unsupported class file major version 69"

**Fix**: You already fixed this! Java 21 is required.

---

### ❌ "Could not resolve com.google.gson"

**Fix**: You need internet connection. Gradle downloads Gson library automatically.

---

### ❌ Plugin doesn't load on server

**Check:**
1. Is it in the `mods/` folder? (Or `plugins/` folder?)
2. Did the server restart fully?
3. Check server console for errors
4. Make sure `plugin.json` has correct class name: `com.survivalgames.SurvivalGames`

---

## 📝 Next Steps After Building

Once this compiles and runs:

1. **Test it**:
   ```
   /sg help
   /kit
   /kit warrior
   /stats
   ```

2. **Verify it saves**:
   - Check `plugins/survival-games/config.json` was created
   - Edit it to add custom kits
   - Restart server and confirm changes load

3. **Tell me it works!**
   - Then I can add the full game logic (arenas, PvP, countdown, etc.)

---

## 💡 Quick File Finder

Can't find your server JAR? Try this PowerShell command:

```powershell
# Search for JAR files in Hytale directory
Get-ChildItem -Path "C:\Path\To\Hytale" -Recurse -Filter *.jar |
  Where-Object { $_.Name -like "*server*" -or $_.Name -like "*hytale*" } |
  Select-Object FullName
```

Or simple directory listing:
```cmd
dir C:\Path\To\Hytale\*.jar /s
```

---

## ✅ Success Checklist

Before asking for help, verify:

- [ ] I found a `.jar` file (not just `.aot`)
- [ ] I updated `build.gradle` with the correct path
- [ ] Path uses forward slashes: `C:/Users/...`
- [ ] I ran `gradlew.bat clean build`
- [ ] Java 21 is installed
- [ ] I have internet connection (for Gradle dependencies)

---

**Ready to build? Let's go!** 🚀

If the build succeeds, you'll have a working plugin JAR ready to install! If it fails, copy the error message and let me know what went wrong.
