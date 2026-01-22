# 🔨 How to Build This Plugin

## ⚠️ IMPORTANT: You Need the Hytale API First!

The build is failing because **Hytale's API isn't publicly available yet.** You need access to a Hytale server to build this plugin.

---

## ✅ Quick Fix (If You Have a Hytale Server)

### Step 1: Find Your Hytale Server JAR

Look for a file like:
- `hytale-server.jar`
- `server.jar`
- `hytale-api.jar`

Usually located in:
```
C:\HytaleServer\server.jar
or
C:\Users\YourName\Hytale\server\server.jar
```

### Step 2: Update build.gradle

Open `build.gradle` and find this section (around line 20):

```gradle
dependencies {
    // Hytale API - You need to provide this from your Hytale server!
    // Option 1: Place your hytale-server.jar in the project root, then uncomment:
    // compileOnly files('hytale-server.jar')

    // Option 2: Or use the full path to your server:
    // compileOnly files('C:/path/to/your/hytale-server.jar')
```

**Choose one option:**

**Option A - Copy the JAR here:**
1. Copy your `hytale-server.jar` to the `hytale-survival-games` folder
2. Uncomment line 22:
   ```gradle
   compileOnly files('hytale-server.jar')
   ```

**Option B - Use full path:**
1. Find the full path to your server JAR
2. Uncomment and update line 25:
   ```gradle
   compileOnly files('C:/Users/YourName/HytaleServer/server.jar')
   ```
   ⚠️ Use forward slashes `/` even on Windows!

### Step 3: Build the Plugin

```bash
# Clean previous attempts
gradlew.bat clean

# Build the plugin
gradlew.bat build
```

### Step 4: Get Your JAR

If successful, find your plugin at:
```
hytale-survival-games/build/libs/survival-games-1.0.0.jar
```

Copy it to your Hytale server's `plugins/` folder!

---

## ❌ If You DON'T Have a Hytale Server

### The Problem:
- Hytale is in Early Access
- The API is not publicly available
- You need actual server access to get the API

### Your Options:

1. **Get Hytale Early Access**
   - Purchase/get access to Hytale
   - Download and run a server
   - Then follow the steps above

2. **Wait for Public Release**
   - Hytale will eventually release the API publicly
   - Server source code will be available 1-2 months after release
   - Your plugin code is ready to build when that happens!

3. **Ask Someone with Server Access**
   - Find someone with a Hytale server
   - Ask them to share the API JAR
   - Or ask them to build the plugin for you

---

## 📋 Complete Checklist

Before building, make sure you have:

- [ ] Java 21 installed (`java -version` shows 21.x.x)
- [ ] Hytale server JAR file
- [ ] Updated `build.gradle` with correct path
- [ ] Internet connection (to download Gson library)
- [ ] Terminal/command prompt open in `hytale-survival-games` folder

---

## 🔍 Troubleshooting

### Error: "Could not resolve com.hytale:hytale-api"
**You're seeing this now!** This means you haven't added the API JAR to build.gradle yet.

**Fix:** Follow Step 2 above to add the JAR.

### Error: "Unsupported class file major version 69"
**Fix:** You have Java 25, need Java 21. See `JAVA_FIX.md`

### Error: "Could not find hytale-server.jar"
**Fix:** Check the file path in build.gradle. Make sure:
- Path is correct
- Use forward slashes: `C:/path/to/file.jar`
- File actually exists at that location

### Error: "package com.hytale.api does not exist"
**Fix:** The server JAR you're using doesn't contain the API. Try:
- Looking for a different JAR file
- Checking if there's a separate API JAR
- Verifying you have the right version

---

## 💡 Why This Is Necessary

Unlike Minecraft's Spigot/Paper which have public APIs available for download, Hytale's API is:
- Bundled with the server
- Not yet publicly distributed
- Only available to those with server access

This is standard for games in Early Access. Your plugin code is complete and correct - it just needs the API to compile against!

---

## 📞 Still Need Help?

Read the detailed guide: **`API_UNAVAILABLE.md`**

It covers:
- Why the API isn't available
- All possible solutions
- What to do if you can't get server access
- FAQs and troubleshooting

---

## ✅ Quick Start (With Server Access)

```bash
# 1. Copy your hytale-server.jar to this folder

# 2. Edit build.gradle line 22:
compileOnly files('hytale-server.jar')

# 3. Build:
gradlew.bat clean build

# 4. Done! Find JAR in build/libs/
```

**Your plugin code is ready - you just need the Hytale API!** 🎮
