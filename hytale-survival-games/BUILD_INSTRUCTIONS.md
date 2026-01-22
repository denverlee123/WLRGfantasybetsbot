# How to Build the Hytale Survival Games Plugin

## ⚠️ Important Note

The JAR file cannot be automatically compiled without internet access because it needs to download:
1. **Hytale API** from Maven repositories
2. **Gson library** for JSON handling

## 🔨 Option 1: Build on Your Computer (Recommended)

### Requirements:
- Java 21 or higher
- Internet connection
- The source code (already in `hytale-survival-games/` folder)

### Steps:

1. **Open terminal/command prompt** in the `hytale-survival-games` folder

2. **Run the build command:**

   **On Windows:**
   ```bash
   gradlew.bat build
   ```

   **On Linux/Mac:**
   ```bash
   ./gradlew build
   ```

3. **Find your JAR file:**
   ```
   hytale-survival-games/build/libs/survival-games-1.0.0.jar
   ```

4. **Copy it to your Hytale server:**
   ```
   YourHytaleServer/plugins/survival-games-1.0.0.jar
   ```

## 🎯 Option 2: Use the Source Package

I've created a source package JAR at:
```
hytale-survival-games.jar (123KB)
```

This contains all the source code and can be extracted if needed, but it's NOT a working plugin yet - you still need to build it using Option 1.

## 🚀 Quick Build Script

Create a file called `build.sh` (Linux/Mac) or `build.bat` (Windows):

**build.sh:**
```bash
#!/bin/bash
cd hytale-survival-games
./gradlew clean build
echo "✅ Build complete! JAR file is in build/libs/"
```

**build.bat:**
```batch
@echo off
cd hytale-survival-games
gradlew.bat clean build
echo Build complete! JAR file is in build\libs\
pause
```

Then just run the script!

## ❓ Troubleshooting

### "Could not resolve dependencies"
- Make sure you have internet connection
- The build needs to download Hytale API and Gson library

### "Java version not supported"
- You need Java 21 or higher
- Download from: https://adoptium.net/

### Gradle is slow on first run
- This is normal! Gradle downloads dependencies on first build
- Subsequent builds will be much faster

## 📦 What You Get

After building, you'll have:
- `survival-games-1.0.0.jar` - The plugin file ready for your server!

## 🔄 Alternative: Manual Download

If you can't build locally, you can:
1. Download the repository as a ZIP
2. Extract it on a computer with internet
3. Follow Option 1 to build
4. Transfer the JAR to your server

## ✅ Verification

After building, verify the JAR:
```bash
jar tf build/libs/survival-games-1.0.0.jar
```

You should see:
- `plugin.json`
- `config.json`
- `com/survivalgames/` classes

## 💡 Need Help?

Check the full documentation:
- `README.md` - Complete guide
- `SETUP_GUIDE.md` - Quick start guide

---

**The source code is ready to build - you just need internet access to download the dependencies!** 🎮
