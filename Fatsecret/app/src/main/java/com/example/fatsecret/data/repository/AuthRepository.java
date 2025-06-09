package com.example.fatsecret.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.UserContract;
import com.example.fatsecret.data.contract.UserProfileContract;
import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static final String PREF_NAME = "FatSecretAuth";
    private static final String KEY_USER_ID = "current_user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "current_user_email";

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPrefs;
    private ExecutorService executor;

    public AuthRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        executor = Executors.newFixedThreadPool(4);
    }

    // ==================== AUTHENTICATION METHODS ====================

    // Register new user
    public void register(String name, String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = null;

            try {
                // Validate input
                if (name == null || name.trim().isEmpty()) {
                    callback.onError(new Exception("Name is required"));
                    return;
                }

                if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
                    callback.onError(new Exception("Valid email is required"));
                    return;
                }

                if (password == null || password.length() < 6) {
                    callback.onError(new Exception("Password must be at least 6 characters"));
                    return;
                }

                db = dbHelper.getWritableDatabase();

                // Check if email already exists
                if (isEmailExists(email, db)) {
                    callback.onError(new Exception("Email already registered"));
                    return;
                }

                // Insert new user
                ContentValues values = new ContentValues();
                values.put(UserContract.UserEntry.COLUMN_NAME, name.trim());
                values.put(UserContract.UserEntry.COLUMN_EMAIL, email.trim().toLowerCase());
                values.put(UserContract.UserEntry.COLUMN_PASSWORD, password); // In production, hash this!
                values.put(UserContract.UserEntry.COLUMN_ROLE, "user");

                String timestamp = getCurrentTimestamp();
                values.put(UserContract.UserEntry.COLUMN_CREATED_AT, timestamp);
                values.put(UserContract.UserEntry.COLUMN_UPDATED_AT, timestamp);

                long userId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);

                if (userId != -1) {
                    // Get the created user
                    User user = getUserById((int) userId, db);
                    if (user != null) {
                        saveUserSession(user);
                        callback.onSuccess(user);
                        Log.d(TAG, "User registered successfully: " + email);
                    } else {
                        callback.onError(new Exception("Failed to retrieve user after registration"));
                    }
                } else {
                    callback.onError(new Exception("Failed to register user"));
                }

            } catch (SQLiteException e) {
                Log.e(TAG, "Database error during registration", e);
                callback.onError(new Exception("Database error: " + e.getMessage()));
            } catch (Exception e) {
                Log.e(TAG, "Registration error", e);
                callback.onError(e);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    // Login user
    public void login(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = null;

            try {
                // Validate input
                if (email == null || email.trim().isEmpty()) {
                    callback.onError(new Exception("Email is required"));
                    return;
                }

                if (password == null || password.trim().isEmpty()) {
                    callback.onError(new Exception("Password is required"));
                    return;
                }

                db = dbHelper.getReadableDatabase();

                String[] projection = {
                        UserContract.UserEntry.COLUMN_ID,
                        UserContract.UserEntry.COLUMN_NAME,
                        UserContract.UserEntry.COLUMN_EMAIL,
                        UserContract.UserEntry.COLUMN_PASSWORD,
                        UserContract.UserEntry.COLUMN_ROLE,
                        UserContract.UserEntry.COLUMN_PROFILE_PICTURE,
                        UserContract.UserEntry.COLUMN_CREATED_AT,
                        UserContract.UserEntry.COLUMN_UPDATED_AT
                };

                String selection = UserContract.UserEntry.COLUMN_EMAIL + " = ? AND " +
                        UserContract.UserEntry.COLUMN_PASSWORD + " = ?";
                String[] selectionArgs = {email.trim().toLowerCase(), password};

                Cursor cursor = db.query(
                        UserContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    User user = cursorToUser(cursor);
                    cursor.close();

                    // Update last login time
                    updateUserLastLogin(user.getId(), db);
                    user.setUpdatedAt(getCurrentTimestamp());

                    saveUserSession(user);
                    callback.onSuccess(user);
                    Log.d(TAG, "User logged in successfully: " + email);
                } else {
                    if (cursor != null) cursor.close();
                    callback.onError(new Exception("Invalid email or password"));
                }

            } catch (SQLiteException e) {
                Log.e(TAG, "Database error during login", e);
                callback.onError(new Exception("Database error: " + e.getMessage()));
            } catch (Exception e) {
                Log.e(TAG, "Login error", e);
                callback.onError(e);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    // Logout
    public void logout() {
        try {
            sharedPrefs.edit()
                    .remove(KEY_USER_ID)
                    .remove(KEY_USER_EMAIL)
                    .putBoolean(KEY_IS_LOGGED_IN, false)
                    .apply();
            Log.d(TAG, "User logged out successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
        }
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false) &&
                sharedPrefs.getInt(KEY_USER_ID, -1) != -1;
    }

    // Get current user
    public void getCurrentUser(UserCallback callback) {
        int userId = sharedPrefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            callback.onResult(null);
            return;
        }

        executor.execute(() -> {
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getReadableDatabase();
                User user = getUserById(userId, db);
                callback.onResult(user);
            } catch (Exception e) {
                Log.e(TAG, "Error getting current user", e);
                callback.onResult(null);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    // ==================== USER PROFILE METHODS ====================

    // Save user profile
    public void saveUserProfile(int userId, float height, float weight, float targetWeight, ProfileCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = null;

            try {
                // Validate input
                if (height <= 0 || height > 300) {
                    callback.onError(new Exception("Invalid height. Must be between 1-300 cm"));
                    return;
                }

                if (weight <= 0 || weight > 1000) {
                    callback.onError(new Exception("Invalid weight. Must be between 1-1000 kg"));
                    return;
                }

                if (targetWeight <= 0 || targetWeight > 1000) {
                    callback.onError(new Exception("Invalid target weight. Must be between 1-1000 kg"));
                    return;
                }

                db = dbHelper.getWritableDatabase();

                String timestamp = getCurrentTimestamp();
                ContentValues values = new ContentValues();
                values.put(UserProfileContract.UserProfileEntry.COLUMN_USER_ID, userId);
                values.put(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT, height);
                values.put(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT, weight);
                values.put(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT, targetWeight);
                values.put(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT, timestamp);

                // Check if profile exists
                UserProfile existingProfile = getUserProfile(userId, db);

                if (existingProfile != null) {
                    // Update existing profile
                    String whereClause = UserProfileContract.UserProfileEntry.COLUMN_USER_ID + " = ?";
                    String[] whereArgs = {String.valueOf(userId)};

                    int rowsUpdated = db.update(
                            UserProfileContract.UserProfileEntry.TABLE_NAME,
                            values,
                            whereClause,
                            whereArgs
                    );

                    if (rowsUpdated > 0) {
                        UserProfile updatedProfile = getUserProfile(userId, db);
                        callback.onSuccess(updatedProfile);
                        Log.d(TAG, "User profile updated for userId: " + userId);
                    } else {
                        callback.onError(new Exception("Failed to update user profile"));
                    }
                } else {
                    // Create new profile
                    values.put(UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT, timestamp);

                    long profileId = db.insert(UserProfileContract.UserProfileEntry.TABLE_NAME, null, values);

                    if (profileId != -1) {
                        UserProfile newProfile = getUserProfile(userId, db);
                        callback.onSuccess(newProfile);
                        Log.d(TAG, "User profile created for userId: " + userId);
                    } else {
                        callback.onError(new Exception("Failed to create user profile"));
                    }
                }

            } catch (SQLiteException e) {
                Log.e(TAG, "Database error saving user profile", e);
                callback.onError(new Exception("Database error: " + e.getMessage()));
            } catch (Exception e) {
                Log.e(TAG, "Error saving user profile", e);
                callback.onError(e);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    /**
     * ✅ NEW: Create new user profile
     */
    // ✅ Pastikan createUserProfile juga save semua field:
    public void createUserProfile(UserProfile userProfile, ProfileCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = null;
            try {
                // ... validation code sama ...

                db = dbHelper.getWritableDatabase();
                String timestamp = getCurrentTimestamp();

                ContentValues values = new ContentValues();
                values.put(UserProfileContract.UserProfileEntry.COLUMN_USER_ID, userProfile.getUserId());

                // ✅ Basic health data
                values.put(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT, userProfile.getHeight());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT, userProfile.getWeight());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT, userProfile.getTargetWeight());

                // ✅ Nutrition data (bisa null untuk optional fields)
                values.put(UserProfileContract.UserProfileEntry.COLUMN_GENDER, userProfile.getGender());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_AGE, userProfile.getAge());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_ACTIVITY_LEVEL, userProfile.getActivityLevel());

                // ✅ Nutrition targets (bisa 0 jika belum di-calculate)
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_CALORIES_TARGET, userProfile.getDailyCaloriesTarget());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_PROTEIN_TARGET, userProfile.getDailyProteinTarget());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_CARBS_TARGET, userProfile.getDailyCarbsTarget());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_FAT_TARGET, userProfile.getDailyFatTarget());

                // ✅ Timestamps
                values.put(UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT, timestamp);
                values.put(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT, timestamp);

                // ... rest of the method sama ...
            } catch (Exception e) {
                // ... error handling sama ...
            }
        });
    }

    /**
     * ✅ NEW: Update existing user profile
     */
    public void updateUserProfile(UserProfile userProfile, ProfileCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = null;

            try {
                // Validate input
                if (userProfile == null) {
                    callback.onError(new Exception("User profile cannot be null"));
                    return;
                }

                if (userProfile.getId() <= 0) {
                    callback.onError(new Exception("Invalid profile ID"));
                    return;
                }

                if (userProfile.getHeight() <= 0 || userProfile.getHeight() > 300) {
                    callback.onError(new Exception("Invalid height. Must be between 1-300 cm"));
                    return;
                }

                if (userProfile.getWeight() <= 0 || userProfile.getWeight() > 1000) {
                    callback.onError(new Exception("Invalid weight. Must be between 1-1000 kg"));
                    return;
                }

                if (userProfile.getTargetWeight() <= 0 || userProfile.getTargetWeight() > 1000) {
                    callback.onError(new Exception("Invalid target weight. Must be between 1-1000 kg"));
                    return;
                }

                db = dbHelper.getWritableDatabase();

                String timestamp = getCurrentTimestamp();
                ContentValues values = new ContentValues();

                // ✅ Basic health data
                values.put(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT, userProfile.getHeight());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT, userProfile.getWeight());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT, userProfile.getTargetWeight());

                // ✅ FIXED: Add nutrition data
                values.put(UserProfileContract.UserProfileEntry.COLUMN_GENDER, userProfile.getGender());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_AGE, userProfile.getAge());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_ACTIVITY_LEVEL, userProfile.getActivityLevel());

                // ✅ FIXED: Add nutrition targets
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_CALORIES_TARGET, userProfile.getDailyCaloriesTarget());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_PROTEIN_TARGET, userProfile.getDailyProteinTarget());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_CARBS_TARGET, userProfile.getDailyCarbsTarget());
                values.put(UserProfileContract.UserProfileEntry.COLUMN_DAILY_FAT_TARGET, userProfile.getDailyFatTarget());

                // ✅ Timestamp
                values.put(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT, timestamp);

                // ✅ Debug log untuk verify data
                Log.d(TAG, "🔧 DEBUG: Updating profile with data:");
                Log.d(TAG, "Height: " + userProfile.getHeight());
                Log.d(TAG, "Weight: " + userProfile.getWeight());
                Log.d(TAG, "Gender: " + userProfile.getGender());
                Log.d(TAG, "Age: " + userProfile.getAge());
                Log.d(TAG, "Activity: " + userProfile.getActivityLevel());
                Log.d(TAG, "Daily Calories: " + userProfile.getDailyCaloriesTarget());

                String whereClause = UserProfileContract.UserProfileEntry.COLUMN_ID + " = ? AND " +
                        UserProfileContract.UserProfileEntry.COLUMN_USER_ID + " = ?";
                String[] whereArgs = {String.valueOf(userProfile.getId()), String.valueOf(userProfile.getUserId())};

                int rowsUpdated = db.update(
                        UserProfileContract.UserProfileEntry.TABLE_NAME,
                        values,
                        whereClause,
                        whereArgs
                );

                Log.d(TAG, "🔧 DEBUG: Database rows updated: " + rowsUpdated);

                if (rowsUpdated > 0) {
                    UserProfile updatedProfile = getUserProfile(userProfile.getUserId(), db);
                    if (updatedProfile != null) {
                        callback.onSuccess(updatedProfile);
                        Log.d(TAG, "✅ User profile updated successfully for ID: " + userProfile.getId());
                    } else {
                        callback.onError(new Exception("Failed to retrieve updated profile"));
                    }
                } else {
                    callback.onError(new Exception("No profile found to update or no changes made"));
                }

            } catch (SQLiteException e) {
                Log.e(TAG, "Database error updating user profile", e);
                callback.onError(new Exception("Database error: " + e.getMessage()));
            } catch (Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                callback.onError(e);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    // Get user profile
    public void getUserProfile(int userId, ProfileCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getReadableDatabase();
                UserProfile profile = getUserProfile(userId, db);
                if (profile != null) {
                    callback.onSuccess(profile);
                } else {
                    callback.onError(new Exception("User profile not found"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting user profile", e);
                callback.onError(e);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    // ==================== HELPER METHODS ====================

    private boolean isEmailExists(String email, SQLiteDatabase db) {
        String[] projection = {UserContract.UserEntry.COLUMN_ID};
        String selection = UserContract.UserEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email.trim().toLowerCase()};

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();

        return exists;
    }

    private User getUserById(int userId, SQLiteDatabase db) {
        String[] projection = {
                UserContract.UserEntry.COLUMN_ID,
                UserContract.UserEntry.COLUMN_NAME,
                UserContract.UserEntry.COLUMN_EMAIL,
                UserContract.UserEntry.COLUMN_PASSWORD,
                UserContract.UserEntry.COLUMN_ROLE,
                UserContract.UserEntry.COLUMN_PROFILE_PICTURE,
                UserContract.UserEntry.COLUMN_CREATED_AT,
                UserContract.UserEntry.COLUMN_UPDATED_AT
        };

        String selection = UserContract.UserEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return user;
    }

    private UserProfile getUserProfile(int userId, SQLiteDatabase db) {
        // ✅ FIXED: Include ALL fields including nutrition data
        String[] projection = {
                UserProfileContract.UserProfileEntry.COLUMN_ID,
                UserProfileContract.UserProfileEntry.COLUMN_USER_ID,
                UserProfileContract.UserProfileEntry.COLUMN_HEIGHT,
                UserProfileContract.UserProfileEntry.COLUMN_WEIGHT,
                UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT,
                // ✅ ADD: Nutrition fields yang missing
                UserProfileContract.UserProfileEntry.COLUMN_GENDER,
                UserProfileContract.UserProfileEntry.COLUMN_AGE,
                UserProfileContract.UserProfileEntry.COLUMN_ACTIVITY_LEVEL,
                UserProfileContract.UserProfileEntry.COLUMN_DAILY_CALORIES_TARGET,
                UserProfileContract.UserProfileEntry.COLUMN_DAILY_PROTEIN_TARGET,
                UserProfileContract.UserProfileEntry.COLUMN_DAILY_CARBS_TARGET,
                UserProfileContract.UserProfileEntry.COLUMN_DAILY_FAT_TARGET,
                // ✅ Timestamps
                UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT,
                UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT
        };

        String selection = UserProfileContract.UserProfileEntry.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(
                UserProfileContract.UserProfileEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        UserProfile profile = null;
        if (cursor != null && cursor.moveToFirst()) {
            // ✅ Debug log untuk verify data dari database
            Log.d(TAG, "🔧 DEBUG: Raw data from database:");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                Log.d(TAG, "🔧 DEBUG: " + columnName + " = " + value);
            }

            profile = cursorToUserProfile(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return profile;
    }

    private void updateUserLastLogin(int userId, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_UPDATED_AT, getCurrentTimestamp());

        String whereClause = UserContract.UserEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        db.update(UserContract.UserEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();

        try {
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_ROLE)));

            String profilePicture = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PROFILE_PICTURE));
            user.setProfilePicture(profilePicture);

            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_CREATED_AT)));
            user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_UPDATED_AT)));
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to User", e);
        }

        return user;
    }

    private UserProfile cursorToUserProfile(Cursor cursor) {
        return MappingHelper.mapCursorToUserProfile(cursor);
    }

    private void saveUserSession(User user) {
        try {
            sharedPrefs.edit()
                    .putInt(KEY_USER_ID, user.getId())
                    .putString(KEY_USER_EMAIL, user.getEmail())
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .apply();
            Log.d(TAG, "User session saved for: " + user.getEmail());
        } catch (Exception e) {
            Log.e(TAG, "Error saving user session", e);
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // ==================== CALLBACK INTERFACES ====================

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(Exception error);
    }

    public interface UserCallback {
        void onResult(User user);
    }

    public interface ProfileCallback {
        void onSuccess(UserProfile profile);
        void onError(Exception error);
    }

    // ==================== CLEANUP ====================

    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}