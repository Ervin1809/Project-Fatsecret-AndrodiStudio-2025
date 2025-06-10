# VinkerTrack - Personal Nutrition Tracker 🍎📱

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)
[![SQLite](https://img.shields.io/badge/Database-SQLite-blue.svg)](https://www.sqlite.org)
[![API](https://img.shields.io/badge/API-USDA%20FDC-yellow.svg)](https://fdc.nal.usda.gov)
[![Retrofit](https://img.shields.io/badge/Network-Retrofit2-blue.svg)](https://square.github.io/retrofit/)

## 📖 Deskripsi Aplikasi

**VinkerTrack** adalah aplikasi mobile Android yang dirancang untuk membantu pengguna melacak dan mengelola asupan nutrisi harian mereka secara akurat dan efisien. Aplikasi ini mengintegrasikan data nutrisi dari **USDA Food Data Central API** untuk memberikan informasi nutrisi yang terpercaya dari ribuan bahan makanan.

### 🎯 Tujuan Utama
- Membantu pengguna mencapai target nutrisi harian yang optimal berdasarkan profil personal
- Memberikan kemudahan dalam pelacakan asupan makanan dengan perhitungan nutrisi yang akurat
- Menyediakan insight tentang pola makan dan progress kesehatan harian
- Mendukung berbagai goal kesehatan (turun berat, naik berat, maintain berat)

## ✨ Fitur Utama

### 🔐 **Sistem Autentikasi**
- **Registrasi Pengguna**: Validasi email dan password (minimal 6 karakter)
- **Login Aman**: Autentikasi dengan enkripsi data pengguna
- **Session Management**: Penyimpanan sesi pengguna untuk kemudahan akses

### 👤 **Manajemen Profil Pengguna**
- **Data Personal**: Input berat badan, tinggi badan, umur, gender
- **Target Kesehatan**: Penetapan target berat badan
- **Activity Level**: 5 level aktivitas (Sedentary, Light, Moderate, Active, Very Active)
- **Auto-calculation**: Perhitungan otomatis kebutuhan nutrisi harian

### 🔍 **Pencarian & Database Makanan**
- **USDA Food Data Central**: Akses ke database nutrisi resmi pemerintah AS
- **Real-time Search**: Pencarian bahan makanan dengan response cepat
- **Nutritional Info**: Data lengkap kalori, protein, karbohidrat, dan lemak per 100g

### 🍽️ **Food Logging System**
- **Meal Time Categories**: Breakfast, Lunch, Dinner, Snack
- **Portion Control**: Input berat makanan dalam gram untuk perhitungan akurat
- **Real-time Calculation**: Perhitungan nutrisi otomatis berdasarkan porsi
- **Daily Tracking**: Monitoring progress nutrisi harian

### 📊 **Nutrition Analytics**
- **Smart Calculation**: Algoritma BMR dan TDEE berdasarkan Mifflin-St Jeor equation
- **Goal-based Adjustment**: Penyesuaian kalori untuk turun/naik/maintain berat
- **Macro Distribution**: Distribusi optimal protein (25%), karbohidrat (45%), lemak (30%)
- **Progress Monitoring**: Tracking pencapaian target nutrisi harian

### 📱 **User Experience**
- **Auto Dark Mode**: Mengikuti tema sistem perangkat secara otomatis
- **Material Design**: Interface modern dan intuitif
- **Responsive Layout**: Optimasi untuk berbagai ukuran layar Android

## 🚀 Cara Penggunaan

### 📱 **Panduan Step-by-Step:**

1. **Registrasi & Login**
   ```
   • Buka aplikasi VinkerTrack
   • Pilih "Register" untuk akun baru
   • Masukkan nama, email, dan password (min. 6 karakter)
   • Login dengan kredensial yang sudah dibuat
   ```

2. **Setup Profil Nutrisi**
   ```
   • Masuk ke halaman "Profile"
   • Input data personal:
     - Berat badan saat ini (kg)
     - Tinggi badan (cm)
     - Umur
     - Gender (Male/Female)
     - Target berat badan
     - Level aktivitas harian
   • Sistem akan otomatis menghitung kebutuhan nutrisi harian Anda
   ```

3. **Pencarian Bahan Makanan**
   ```
   • Buka halaman "Search"
   • Ketik nama bahan makanan dalam bahasa Inggris
   • Pilih dari hasil pencarian USDA database
   • Lihat informasi nutrisi lengkap per 100g
   ```

4. **Logging Makanan Harian**
   ```
   • Masuk ke halaman "Home"
   • Pilih waktu makan (Breakfast/Lunch/Dinner/Snack)
   • Tambahkan bahan makanan yang dikonsumsi
   • Input berat makanan dalam gram
   • Sistem akan menghitung nutrisi sesuai porsi
   ```

5. **Monitoring Progress**
   ```
   • Lihat summary nutrisi harian di dashboard
   • Cek progress terhadap target kalori, protein, karbs, dan lemak
   • Monitor apakah target harian sudah tercapai
   ```

6. **Review History**
   ```
   • Akses halaman "History" untuk melihat data historis
   • Analisis pola makan dan trend nutrisi
   • Evaluasi konsistensi pencapaian target
   ```

## 🛠️ Implementasi Teknis

### 🏗️ **Arsitektur Aplikasi**

```
VinkerTrack/
├── app/src/main/java/com/example/fatsecret/
│   ├── data/
│   │   ├── contract/           # Database contracts & table definitions
│   │   ├── helper/             # DatabaseHelper.java
│   │   ├── managers/           # Data managers
│   │   ├── mapper/             # MappingHelper.java
│   │   ├── model/              # Data models (User, UserProfile, etc.)
│   │   ├── network/            # API configuration & services
│   │   ├── repository/         # Data repositories
│   │   └── viewModel/          # ViewModels for UI
│   ├── ui/
│   │   ├── adapters/           # RecyclerView adapters
│   │   ├── dialogs/            # Custom dialogs
│   │   ├── fragments/          # UI fragments
│   │   ├── LoginActivity.java  # Authentication UI
│   │   └── RegisterActivity.java
│   ├── utils/
│   │   ├── MealTime/           # Meal time utilities
│   │   └── NutritionCalculator # Nutrition calculation engine
│   └── MainActivity.java       # Main application entry
```

### 💾 **Database Schema (SQLite)**

**Core Tables:**
```sql
-- Users Table
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT DEFAULT 'user',
    profile_picture TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- User Profiles Table
CREATE TABLE user_profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    height REAL NOT NULL,
    weight REAL NOT NULL,
    target_weight REAL,
    gender TEXT NOT NULL,
    age INTEGER NOT NULL,
    activity_level TEXT NOT NULL,
    daily_calories_target REAL,
    daily_protein_target REAL,
    daily_carbs_target REAL,
    daily_fat_target REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Ingredients Table (USDA Food Database)
CREATE TABLE ingredients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    fdc_id TEXT UNIQUE,
    api_source TEXT,
    calories_per_100g REAL,
    protein_per_100g REAL,
    carbs_per_100g REAL,
    fat_per_100g REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Food Logs Table
CREATE TABLE food_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    user_profile_id INTEGER,
    date DATE NOT NULL,
    meal_time TEXT NOT NULL, -- breakfast, lunch, dinner, snack
    note TEXT,
    total_calories REAL,
    total_protein REAL,
    total_carbs REAL,
    total_fat REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id)
);

-- Food Log Items Table
CREATE TABLE food_log_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    food_log_id INTEGER,
    ingredient_id INTEGER,
    weight_grams REAL NOT NULL,
    calculated_calories REAL,
    calculated_protein REAL,
    calculated_carbs REAL,
    calculated_fat REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (food_log_id) REFERENCES food_logs(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);
```

### 🌐 **USDA API Integration**

```java
// API Configuration
public class ApiConfig {
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // HTTP Logging for debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client with timeout configuration
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
```

### 🧮 **Nutrition Calculation Engine**

```java
public class NutritionCalculator {
    
    // Activity Level Multipliers (based on scientific research)
    public static final float SEDENTARY_MULTIPLIER = 1.2f;      // Minimal exercise
    public static final float LIGHT_MULTIPLIER = 1.375f;        // Light exercise 1-3x/week
    public static final float MODERATE_MULTIPLIER = 1.55f;      // Moderate exercise 3-5x/week
    public static final float ACTIVE_MULTIPLIER = 1.725f;       // Heavy exercise 6-7x/week
    public static final float VERY_ACTIVE_MULTIPLIER = 1.9f;    // Very heavy exercise + physical job

    // Macro Distribution (percentage of daily calories)
    public static final float PROTEIN_PERCENTAGE = 0.25f;       // 25% of total calories
    public static final float CARBS_PERCENTAGE = 0.45f;         // 45% of total calories
    public static final float FAT_PERCENTAGE = 0.30f;           // 30% of total calories

    /**
     * Calculate BMR using Mifflin-St Jeor equation
     */
    public static float calculateBMR(UserProfile profile) {
        float bmr;
        if ("male".equalsIgnoreCase(profile.getGender())) {
            // Male BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5
            bmr = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) + 5;
        } else if ("female".equalsIgnoreCase(profile.getGender())) {
            // Female BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161
            bmr = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) - 161;
        } else {
            // Default to average if gender is unclear
            float maleBMR = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) + 5;
            float femaleBMR = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) - 161;
            bmr = (maleBMR + femaleBMR) / 2;
        }
        return Math.max(bmr, 1000); // Minimum 1000 calories for safety
    }

    /**
     * Calculate TDEE based on BMR and activity level
     */
    public static float calculateTDEE(float bmr, String activityLevel) {
        float multiplier;
        switch (activityLevel.toLowerCase()) {
            case "sedentary": multiplier = SEDENTARY_MULTIPLIER; break;
            case "light": multiplier = LIGHT_MULTIPLIER; break;
            case "moderate": multiplier = MODERATE_MULTIPLIER; break;
            case "active": multiplier = ACTIVE_MULTIPLIER; break;
            case "very_active": multiplier = VERY_ACTIVE_MULTIPLIER; break;
            default: multiplier = MODERATE_MULTIPLIER; // Default to moderate
        }
        return bmr * multiplier;
    }

    /**
     * Adjust daily calories based on weight goal
     */
    public static float adjustCaloriesForGoal(float tdee, float currentWeight, float targetWeight) {
        float weightDiff = targetWeight - currentWeight;

        if (Math.abs(weightDiff) < 1) {
            return tdee; // Maintain weight
        } else if (weightDiff < 0) {
            // Weight loss - 500 calorie deficit (~0.5kg/week)
            return Math.max(tdee - 500, tdee * 0.8f); // Max 20% deficit
        } else {
            // Weight gain - 300 calorie surplus (~0.3kg/week)
            return tdee + 300;
        }
    }
}
```

### 🔐 **Authentication System**

```java
// User Registration with validation
public void register(String name, String email, String password, AuthCallback callback) {
    executor.execute(() -> {
        try {
            // Input validation
            if (name == null || name.trim().isEmpty()) {
                callback.onError(new Exception("Name is required"));
                return;
            }
            if (email == null || !isValidEmail(email)) {
                callback.onError(new Exception("Valid email is required"));
                return;
            }
            if (password == null || password.length() < 6) {
                callback.onError(new Exception("Password must be at least 6 characters"));
                return;
            }

            // Check if email already exists
            if (isEmailExists(email, db)) {
                callback.onError(new Exception("Email already registered"));
                return;
            }

            // Insert new user with timestamp
            ContentValues values = new ContentValues();
            values.put(UserContract.UserEntry.COLUMN_NAME, name.trim());
            values.put(UserContract.UserEntry.COLUMN_EMAIL, email.trim().toLowerCase());
            values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);
            values.put(UserContract.UserEntry.COLUMN_ROLE, "user");
            values.put(UserContract.UserEntry.COLUMN_CREATED_AT, getCurrentTimestamp());
            values.put(UserContract.UserEntry.COLUMN_UPDATED_AT, getCurrentTimestamp());

            long userId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
            
            if (userId != -1) {
                User user = getUserById((int) userId, db);
                saveUserSession(user);
                callback.onSuccess(user);
            }
        } catch (Exception e) {
            callback.onError(e);
        }
    });
}

// User Login with session management
public void login(String email, String password, AuthCallback callback) {
    executor.execute(() -> {
        try {
            // Query user with email and password
            String selection = UserContract.UserEntry.COLUMN_EMAIL + " = ? AND " +
                             UserContract.UserEntry.COLUMN_PASSWORD + " = ?";
            String[] selectionArgs = {email.trim().toLowerCase(), password};

            Cursor cursor = db.query(UserContract.UserEntry.TABLE_NAME, 
                                   projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                updateUserLastLogin(user.getId(), db);
                saveUserSession(user);
                callback.onSuccess(user);
            } else {
                callback.onError(new Exception("Invalid email or password"));
            }
        } catch (Exception e) {
            callback.onError(e);
        }
    });
}
```

### 🌙 **Automatic Dark Mode**

Aplikasi VinkerTrack secara otomatis mengikuti tema sistem perangkat:
- **Light Mode**: Ketika sistem menggunakan tema terang
- **Dark Mode**: Ketika sistem menggunakan tema gelap
- **Auto-switch**: Perubahan tema realtime mengikuti pengaturan sistem

## 🔧 Instalasi & Setup

### 📋 **Prerequisites**
- Android Studio Arctic Fox atau versi lebih baru
- Android SDK (API level 21+)
- Java Development Kit (JDK 8+)
- USDA Food Data Central API Key ([Get Free API Key](https://fdc.nal.usda.gov/api-guide.html))

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
   # Pastikan semua dependencies ter-download
   ```

3. **Konfigurasi USDA API Key**
   ```java
   // Edit file: app/src/main/java/com/example/fatsecret/data/network/ApiConfig.java
   // Ganti YOUR_API_KEY dengan API key Anda
   private static final String API_KEY = "YOUR_USDA_API_KEY_HERE";
   ```

4. **Build & Run**
   ```bash
   # Connect Android device atau start emulator (API 21+)
   # Click "Run" button di Android Studio
   # Atau gunakan command line:
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

5. **Testing**
   ```bash
   # Jalankan unit tests
   ./gradlew test
   
   # Jalankan instrumented tests
   ./gradlew connectedAndroidTest
   ```

## 📊 Tech Stack

| Kategori | Teknologi | Versi | Keterangan |
|----------|-----------|-------|------------|
| **Platform** | Android | API 21+ | Mobile application |
| **Language** | Java | 8+ | Primary programming language |
| **IDE** | Android Studio | Latest | Development environment |
| **Database** | SQLite | Built-in | Local data storage |
| **API** | USDA Food Data Central | v1 | Nutrition data source |
| **HTTP Client** | Retrofit2 | 2.9.0 | REST API integration |
| **JSON Parser** | Gson | 2.8.9 | JSON serialization |
| **Logging** | OkHttp Logging | 4.9.3 | Network request logging |
| **Architecture** | MVVM | - | Model-View-ViewModel pattern |
| **UI/UX** | Material Design | - | Modern Android design |

## 🎯 Fitur yang Aktif

### ✅ **Implemented Features**
- [x] User Registration & Login
- [x] User Profile Management
- [x] USDA Food Search Integration
- [x] Daily Nutrition Calculation
- [x] Meal Logging (Breakfast/Lunch/Dinner/Snack)
- [x] Automatic Dark Mode
- [x] SQLite Database with 5 core tables
- [x] Nutrition Target Calculation (BMR/TDEE)
- [x] Activity Level Management

### 🚧 **Not Implemented (Due to Time Constraints)**
- [ ] Menu & Menu Ingredients features
- [ ] Advanced Analytics Dashboard
- [ ] Data Export functionality
- [ ] Social sharing features

## 📱 Cara Penggunaan API

### 🔍 **USDA Food Search Example**
```java
// Search for foods
ApiService apiService = ApiConfig.getApiService();
Call<FoodSearchResponse> call = apiService.searchFoods("apple", "YOUR_API_KEY");

call.enqueue(new Callback<FoodSearchResponse>() {
    @Override
    public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            List<FoodItem> foods = response.body().getFoods();
            // Process food items
        }
    }
    
    @Override
    public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
        // Handle error
    }
});
```

## 🤝 Kontribusi

Kami menyambut kontribusi dari developer lain! Berikut cara berkontribusi:

1. **Fork** repository ini
2. **Create branch** untuk fitur baru (`git checkout -b feature/AmazingFeature`)
3. **Commit** perubahan (`git commit -m 'Add some AmazingFeature'`)
4. **Push** ke branch (`git push origin feature/AmazingFeature`)
5. **Create Pull Request**

### 📝 **Contribution Guidelines**
- Ikuti coding standards Java dan Android
- Tambahkan unit tests untuk fitur baru
- Update dokumentasi jika diperlukan
- Pastikan tidak ada breaking changes

## 📄 License

Distributed under the MIT License. See `LICENSE` file for more information.

## 👨‍💻 Developer

**Ervin1809**
- GitHub: [@Ervin1809](https://github.com/Ervin1809)
- Repository: [VinkerTrack Project](https://github.com/Ervin1809/Project-Fatsecret-AndrodiStudio-2025.git)
- Email: [Contact Developer](mailto:your-email@example.com)

---

## 📸 Screenshots

*Screenshots will be added after UI implementation completion*

---

## 🚀 Future Roadmap

### 🎯 **Phase 1 - Core Enhancements**
- [ ] Advanced analytics dashboard
- [ ] Weekly/Monthly nutrition reports
- [ ] Food favorites system
- [ ] Barcode scanning integration

### 🎯 **Phase 2 - User Experience**
- [ ] Meal planning features
- [ ] Recipe builder
- [ ] Social sharing capabilities
- [ ] Push notifications for meal reminders

### 🎯 **Phase 3 - Advanced Features**
- [ ] AI-powered meal recommendations
- [ ] Integration with fitness trackers
- [ ] Nutritionist consultation features
- [ ] Multi-language support

---

## 📞 Support

Jika Anda mengalami masalah atau memiliki pertanyaan:

1. **Check Issues**: Lihat [GitHub Issues](https://github.com/Ervin1809/Project-Fatsecret-AndrodiStudio-2025/issues) untuk masalah yang sudah diketahui
2. **Create Issue**: Buat issue baru jika belum ada yang serupa
3. **Contact Developer**: Hubungi developer melalui GitHub

---

**Made with ❤️ by Ervin1809**

*Last updated: 2025-01-10*

> **Note**: VinkerTrack adalah proyek open-source yang dikembangkan untuk membantu orang mencapai tujuan kesehatan mereka melalui tracking nutrisi yang akurat dan mudah digunakan.
