# Majri Grocery Store (माजरी किराना स्टोर)

Android application built with Kotlin and Jetpack Compose for local grocery ordering, product catalog, cart, orders, and owner admin management.

## 📱 Android Version Compatibility
- **Minimum Android Version**: Android 10 (API level 29) or higher
- **Target SDK**: Android 15 (API level 35)

---

## 🚀 Quick Start / ZIP से ऐप कैसे बनाएं

### 1. Export as ZIP from AI Studio
1. AI Studio के टॉप-राइट मेन्यू (Project Settings / 3-dots) पर जाएं।
2. **"Export"** या **"Download as ZIP"** चुनें।
3. डाउनलोड की गई ZIP फ़ाइल को अपने कंप्यूटर पर एक्सट्रैक्ट (Unzip) करें।

### 2. Open in Android Studio / Code Assist
1. **Android Studio** (या **Code Assist**) खोलें।
2. **Open an Existing Project** चुनें।
3. एक्सट्रैक्ट किए गए फोल्डर को सेलेक्ट करें।
4. Android Studio स्वचालित रूप से Gradle Sync शुरू करेगा।

### 3. Build APK from Terminal / Command Prompt
- **Windows (Command Prompt / PowerShell)**:
  ```cmd
  gradlew.bat assembleDebug
  ```
- **Mac / Linux / Code Assist Terminal**:
  ```bash
  ./gradlew assembleDebug
  ```

बिल्ड होने के बाद आपकी APK यहाँ तैयार मिलेगी:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🛠 Project Structure
- `app/src/main/java/com/example/`
  - `data/`: Room Database (`GroceryDatabase`), Entities, DAOs, Sample Pre-populator.
  - `ui/`: Customer screens (Home, Category, Search, Product Detail, Deals, Cart, Checkout, Order Tracking) & Admin screens (Product Inventory, Order Manager, Analytics).
  - `ui/theme/`: Material 3 high-density color system & typography.
- `gradlew` & `gradlew.bat`: Pre-configured Gradle wrapper for instant builds.
- `.env`: Environment secrets configuration.
