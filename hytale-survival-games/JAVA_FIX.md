# Java Version Fix Guide

## ❌ The Problem

You're getting this error:
```
Unsupported class file major version 69
```

This means you have **Java 25** installed, but:
- The plugin needs **Java 21**
- Gradle doesn't support Java 25 yet

## ✅ Solution Options

### **Option 1: Let Gradle Download Java 21 Automatically (Easiest)**

I've updated the `build.gradle` to automatically use Java 21. Just try building again:

```bash
# Windows:
gradlew.bat clean build

# Linux/Mac:
./gradlew clean build
```

Gradle should automatically download Java 21 and use it for the build!

---

### **Option 2: Install Java 21 Manually (Recommended)**

1. **Download Java 21:**
   - Go to: https://adoptium.net/temurin/releases/?version=21
   - Download the **JDK 21** for your operating system
   - Install it

2. **Set JAVA_HOME (Windows):**
   ```cmd
   # Find where Java 21 is installed (usually):
   # C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot

   # Set environment variable (replace with your path):
   setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"

   # Close and reopen your terminal
   ```

3. **Set JAVA_HOME (Linux/Mac):**
   ```bash
   # Add to ~/.bashrc or ~/.zshrc:
   export JAVA_HOME=/path/to/java21
   export PATH=$JAVA_HOME/bin:$PATH

   # Reload:
   source ~/.bashrc
   ```

4. **Verify:**
   ```bash
   java -version
   # Should show: openjdk version "21.x.x"
   ```

5. **Build again:**
   ```bash
   gradlew.bat clean build
   ```

---

### **Option 3: Force Gradle to Use Specific Java (Quick Fix)**

If you have multiple Java versions installed:

**Windows:**
```cmd
# Find your Java 21 installation
dir "C:\Program Files\Eclipse Adoptium"

# Build with specific Java:
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot
gradlew.bat clean build
```

**Linux/Mac:**
```bash
# Find Java installations:
/usr/libexec/java_home -V

# Build with specific Java:
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
./gradlew clean build
```

---

## 🔍 Verify Your Java Version

Before building, check your Java version:

```bash
java -version
```

**You should see:**
```
openjdk version "21.0.x" or "21.x.x"
```

**If you see:**
```
openjdk version "25.x.x"  ← Too new! Won't work
openjdk version "17.x.x"  ← Too old for Hytale
openjdk version "21.x.x"  ← Perfect! ✅
```

---

## 🚀 After Fixing Java

Once you have Java 21 set up:

1. **Clean previous build attempts:**
   ```bash
   gradlew.bat clean
   ```

2. **Build the plugin:**
   ```bash
   gradlew.bat build
   ```

3. **Find your JAR:**
   ```
   build/libs/survival-games-1.0.0.jar
   ```

---

## ❓ Still Not Working?

### Error: "JAVA_HOME is not set"
**Fix:**
```bash
# Windows:
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"

# Linux/Mac:
export JAVA_HOME=/path/to/java21
```

### Error: "Could not find tools.jar"
**Fix:** Make sure you installed the **JDK** (not JRE). Download from https://adoptium.net/

### Error: "Gradle daemon disappeared"
**Fix:**
```bash
# Kill all Gradle processes:
gradlew.bat --stop

# Try again:
gradlew.bat clean build
```

### Error: "Could not resolve dependencies"
**Fix:** Make sure you have internet connection. Gradle needs to download libraries.

---

## 💡 Quick Checklist

- [ ] Java 21 is installed (not 17, not 25)
- [ ] `java -version` shows "21.x.x"
- [ ] JAVA_HOME points to Java 21
- [ ] You have internet connection
- [ ] You ran `gradlew.bat clean build` in the `hytale-survival-games` folder

---

## 📞 Last Resort: Pre-built JAR

If you absolutely cannot get Java 21 working, you can:

1. Ask someone with Java 21 to build it for you
2. Use a GitHub Action to build it automatically
3. Use an online Java IDE to compile it

But **installing Java 21 is the easiest and recommended solution!**

---

**TL;DR:**
1. Download Java 21 from https://adoptium.net/temurin/releases/?version=21
2. Install it
3. Run `gradlew.bat clean build`
4. Done! ✅
