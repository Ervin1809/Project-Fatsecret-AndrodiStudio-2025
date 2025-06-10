# Project-Fatsecret-AndrodiStudio-2025
# VinkerTrack - Personal Nutrition Tracker 🍎📱

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)
[![SQLite](https://img.shields.io/badge/Database-SQLite-blue.svg)](https://www.sqlite.org)
[![API](https://img.shields.io/badge/API-USDA-yellow.svg)](https://fdc.nal.usda.gov)

## 📖 Deskripsi Aplikasi

**VinkerTrack** adalah aplikasi mobile Android yang dirancang untuk membantu pengguna melacak dan mengelola asupan nutrisi harian mereka. Aplikasi ini mengintegrasikan data nutrisi dari USDA API untuk memberikan informasi akurat tentang kandungan gizi berbagai bahan makanan, memungkinkan pengguna untuk mencapai target kesehatan mereka dengan mudah dan efisien.

### 🎯 Tujuan Utama
- Membantu pengguna mencapai target nutrisi harian yang optimal
- Memberikan kemudahan dalam pelacakan asupan makanan
- Menyediakan insight tentang pola makan dan kebiasaan nutrisi
- Mendukung gaya hidup sehat melalui teknologi mobile

## ✨ Fitur Utama

### 🔐 **Autentikasi Pengguna**
- **Login & Registrasi**: Sistem keamanan untuk melindungi data pribadi pengguna
- **Profil Pengguna**: Penyimpanan data personal (berat badan, tinggi badan, target berat, gender, tingkat aktivitas)

### 🔍 **Pencarian & Database Makanan**
- **Search Bahan Makanan**: Pencarian real-time menggunakan USDA API
- **Database Komprehensif**: Akses ke ribuan data nutrisi bahan makanan

### 🍽️ **Manajemen Meal Planning**
- **Jadwal Makan**: Kategorisasi makanan berdasarkan waktu (Breakfast, Lunch, Dinner, Snack)
- **Perhitungan Porsi**: Kalkulasi nutrisi berdasarkan berat bahan makanan
- **Tracking Harian**: Monitoring asupan nutrisi real-time

### 📊 **Analytics & Monitoring**
- **Manajemen Nutrisi Harian**: Perhitungan otomatis kebutuhan nutrisi berdasarkan profil pengguna
- **Progress Tracking**: Visualisasi pencapaian target nutrisi harian
- **History Harian**: Riwayat lengkap asupan makanan dan nutrisi

### 🌙 **User Experience**
- **Dark Mode Otomatis**: Adaptasi tema berdasarkan pengaturan sistem device
- **Interface Intuitif**: Desain yang user-friendly dan mudah digunakan

## 🚀 Cara Penggunaan

### 📱 **Langkah-langkah Penggunaan:**

1. **Registrasi Akun**
   ```
   • Buka aplikasi VinkerTrack
   • Pilih "Daftar" jika belum memiliki akun
   • Isi data registrasi dengan lengkap
   • Akun akan otomatis ter-login setelah registrasi berhasil
   ```

2. **Setup Profil**
   ```
   • Masuk ke halaman "Profile"
   • Lengkapi biodata personal:
     - Berat badan (kg)
     - Tinggi badan (cm)
     - Target berat badan
     - Gender
     - Tingkat aktivitas harian
   • Data ini akan digunakan untuk menghitung kebutuhan nutrisi harian
   ```

3. **Eksplorasi Bahan Makanan**
   ```
   • Buka halaman "Search"
   • Gunakan fitur pencarian untuk menemukan bahan makanan
   • Lihat detail nutrisi setiap bahan makanan
   • Data nutrisi diambil dari USDA API yang terpercaya
   ```

4. **Input Makanan Harian**
   ```
   • Masuk ke halaman "Home"
   • Pilih kategori waktu makan:
     - Breakfast (Sarapan)
     - Lunch (Makan Siang)
     - Dinner (Makan Malam)
     - Snack (Camilan)
   • Tambahkan bahan makanan yang dikonsumsi
   • Atur berat/porsi makanan untuk perhitungan nutrisi akurat
   ```

5. **Monitoring Progress**
   ```
   • Lihat progress nutrisi harian di halaman "Home"
   • Cek apakah target nutrisi sudah tercapai
   • Gunakan indikator visual untuk memahami status nutrisi
   ```

6. **Review History**
   ```
   • Buka halaman "History"
   • Tinjau riwayat asupan makanan harian
   • Analisis pola makan dan tren nutrisi
   ```

## 🛠️ Implementasi Teknis

### 🏗️ **Arsitektur Aplikasi**

```
VinkerTrack/
├── app/
│   ├── src/main/java/
│   │   ├── activities/          # Activity classes
│   │   ├── adapters/           # RecyclerView adapters
│   │   ├── database/           # SQLite database helper
│   │   ├── models/             # Data models
│   │   ├── network/            # API integration
│   │   └── utils/              # Utility classes
│   ├── src/main/res/
│   │   ├── layout/             # XML layouts
│   │   ├── values/             # Colors, strings, styles
│   │   └── drawable/           # Icons and images
│   └── src/main/AndroidManifest.xml
└── gradle files
```

### 💾 **Database Design (SQLite)**

```sql
-- Tabel Users
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabel User Profiles
CREATE TABLE user_profiles (
    user_id INTEGER PRIMARY KEY,
    weight REAL NOT NULL,
    height REAL NOT NULL,
    target_weight REAL,
    gender TEXT NOT NULL,
    activity_level TEXT NOT NULL,
    daily_calories REAL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabel Food Items
CREATE TABLE food_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usda_id TEXT UNIQUE,
    name TEXT NOT NULL,
    calories_per_100g REAL,
    protein_per_100g REAL,
    carbs_per_100g REAL,
    fat_per_100g REAL
);

-- Tabel Daily Meals
CREATE TABLE daily_meals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    food_id INTEGER,
    meal_type TEXT NOT NULL, -- breakfast, lunch, dinner, snack
    portion_weight REAL NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (food_id) REFERENCES food_items(id)
);
```

### 🌐 **Integrasi API USDA**

```java
// Network Configuration
public class USDAApiService {
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";
    private static final String API_KEY = "YOUR_USDA_API_KEY";
    
    // Search food items
    public void searchFoods(String query, ApiCallback callback) {
        // Implementation untuk search makanan
    }
    
    // Get detailed nutrition info
    public void getFoodDetails(String fdcId, ApiCallback callback) {
        // Implementation untuk detail nutrisi
    }
}
```

### 🧮 **Algoritma Perhitungan Nutrisi**

```java
public class NutritionCalculator {
    
    // Menghitung BMR (Basal Metabolic Rate)
    public double calculateBMR(double weight, double height, int age, String gender) {
        if (gender.equals("male")) {
            return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
    }
    
    // Menghitung kebutuhan kalori harian
    public double calculateDailyCalories(double bmr, String activityLevel) {
        double multiplier = getActivityMultiplier(activityLevel);
        return bmr * multiplier;
    }
    
    // Menghitung nutrisi berdasarkan porsi
    public NutritionInfo calculatePortionNutrition(FoodItem food, double portionWeight) {
        double factor = portionWeight / 100.0; // Convert to per 100g basis
        return new NutritionInfo(
            food.getCalories() * factor,
            food.getProtein() * factor,
            food.getCarbs() * factor,
            food.getFat() * factor
        );
    }
}
```

### 🎨 **Dark Mode Implementation**

```java
// Automatic theme detection
public class ThemeManager {
    public static void applyTheme(Activity activity) {
        int nightModeFlags = activity.getResources().getConfiguration().uiMode 
                           & Configuration.UI_MODE_NIGHT_MASK;
        
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
```

## 🔧 Instalasi & Setup

### 📋 **Prerequisites**
- Android Studio (versi terbaru)
- Android SDK (API level 21+)
- Java Development Kit (JDK 8+)
- USDA API Key (gratis dari [FoodData Central](https://fdc.nal.usda.gov/api-guide.html))

### ⚡ **Langkah Instalasi**

1. **Clone Repository**
   ```bash
   git clone https://github.com/Ervin1809/Project-Fatsecret-AndrodiStudio-2025.git
   cd Project-Fatsecret-AndrodiStudio-2025
   ```

2. **Setup Android Studio**
   ```bash
   # Buka Android Studio
   # File → Open → Pilih folder project
   # Tunggu Gradle sync selesai
   ```

3. **Konfigurasi API Key**
   ```java
   // Buat file ApiConfig.java di package utils
   public class ApiConfig {
       public static final String USDA_API_KEY = "YOUR_API_KEY_HERE";
       public static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";
   }
   ```

4. **Build & Run**
   ```bash
   # Connect Android device atau start emulator
   # Click "Run" button di Android Studio
   # Atau gunakan command line:
   ./gradlew assembleDebug
   ```

## 📊 Tech Stack

| Kategori | Teknologi | Keterangan |
|----------|-----------|------------|
| **Platform** | Android | Mobile application |
| **Language** | Java | Primary programming language |
| **IDE** | Android Studio | Development environment |
| **Database** | SQLite | Local data storage |
| **API** | USDA FoodData Central | Nutrition data source |
| **Architecture** | MVC | Model-View-Controller pattern |
| **UI/UX** | Material Design | Modern Android design guidelines |

## 🤝 Kontribusi

Kami menyambut kontribusi dari developer lain! Berikut cara berkontribusi:

1. Fork repository ini
2. Buat branch untuk fitur baru (`git checkout -b feature/AmazingFeature`)
3. Commit perubahan (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.

## 👨‍💻 Developer

**Ervin1809**
- GitHub: [@Ervin1809](https://github.com/Ervin1809)
- Repository: [VinkerTrack Project](https://github.com/Ervin1809/Project-Fatsecret-AndrodiStudio-2025.git)

---

## 📸 Screenshots

*Coming Soon - Screenshots will be added after UI implementation*

---

## 🚀 Roadmap

- [x] Basic authentication system
- [x] User profile management
- [x] Food search integration
- [x] Meal tracking functionality
- [ ] Advanced analytics dashboard
- [ ] Export data functionality
- [ ] Social sharing features
- [ ] Meal recommendations based on AI

---

**Made with ❤️ by Ervin1809**

*Last updated: 2025-06-10*
