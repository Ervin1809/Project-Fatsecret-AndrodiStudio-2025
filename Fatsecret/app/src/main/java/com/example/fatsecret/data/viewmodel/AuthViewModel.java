package com.example.fatsecret.data.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private static final String TAG = "AuthViewModel";
    private AuthRepository authRepository;

    // LiveData for UI observation
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();

    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        loadCurrentUser();
    }

    // ==================== GETTERS FOR LIVEDATA ====================

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    // ==================== AUTHENTICATION METHODS ====================

    public void login(String email, String password) {
        isLoading.setValue(true);

        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                currentUser.postValue(user);
                loginSuccess.postValue(true);
                loadUserProfile(user.getId());
                Log.d(TAG, "Login successful for: " + user.getEmail());
            }

            @Override
            public void onError(Exception error) {
                isLoading.postValue(false);
                errorMessage.postValue(error.getMessage());
                loginSuccess.postValue(false);
                Log.e(TAG, "Login failed: " + error.getMessage());
            }
        });
    }

    public void register(String name, String email, String password) {
        isLoading.setValue(true);

        authRepository.register(name, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                currentUser.postValue(user);
                registerSuccess.postValue(true);
                Log.d(TAG, "Registration successful for: " + user.getEmail());
            }

            @Override
            public void onError(Exception error) {
                isLoading.postValue(false);
                errorMessage.postValue(error.getMessage());
                registerSuccess.postValue(false);
                Log.e(TAG, "Registration failed: " + error.getMessage());
            }
        });
    }

    public void logout() {
        authRepository.logout();
        currentUser.setValue(null);
        userProfile.setValue(null);
        Log.d(TAG, "User logged out");
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    // ==================== USER PROFILE METHODS (MODERN) ====================

    /**
     * Create new user profile with full nutrition data support
     */
    public void createUserProfile(UserProfile newProfile) {
        if (newProfile == null) {
            errorMessage.setValue("Invalid profile data");
            return;
        }

        User user = currentUser.getValue();
        if (user == null) {
            errorMessage.setValue("No user logged in");
            return;
        }

        Log.d(TAG, "🏥 Creating user profile for user ID: " + newProfile.getUserId());
        isLoading.setValue(true);

        authRepository.createUserProfile(newProfile, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                isLoading.postValue(false);
                userProfile.postValue(profile);
                Log.d(TAG, "✅ User profile created successfully with ID: " + profile.getId());
            }

            @Override
            public void onError(Exception error) {
                isLoading.postValue(false);
                errorMessage.postValue(error.getMessage());
                Log.e(TAG, "❌ Failed to create user profile: " + error.getMessage());
            }
        });
    }

    /**
     * Update existing user profile with full nutrition data support
     */
    public void updateUserProfile(UserProfile updatedProfile) {
        if (updatedProfile == null) {
            errorMessage.setValue("Invalid profile data");
            return;
        }

        User user = currentUser.getValue();
        if (user == null) {
            errorMessage.setValue("No user logged in");
            return;
        }

        Log.d(TAG, "🏥 Updating user profile ID: " + updatedProfile.getId());
        isLoading.setValue(true);

        authRepository.updateUserProfile(updatedProfile, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                isLoading.postValue(false);
                userProfile.postValue(profile);
                Log.d(TAG, "✅ User profile updated successfully");
            }

            @Override
            public void onError(Exception error) {
                isLoading.postValue(false);
                errorMessage.postValue(error.getMessage());
                Log.e(TAG, "❌ Failed to update user profile: " + error.getMessage());
            }
        });
    }

    /**
     * Load user profile by user ID
     */
    public void loadUserProfile(int userId) {
        authRepository.getUserProfile(userId, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                userProfile.postValue(profile);
                Log.d(TAG, "User profile loaded successfully");
            }

            @Override
            public void onError(Exception error) {
                userProfile.postValue(null);
                Log.d(TAG, "No user profile found (this is normal for new users)");
                // Don't post error for missing profile - it's normal for new users
            }
        });
    }

    // ==================== HELPER METHODS ====================

    private void loadCurrentUser() {
        if (authRepository.isLoggedIn()) {
            authRepository.getCurrentUser(new AuthRepository.UserCallback() {
                @Override
                public void onResult(User user) {
                    if (user != null) {
                        currentUser.postValue(user);
                        loadUserProfile(user.getId());
                        Log.d(TAG, "Current user loaded: " + user.getEmail());
                    } else {
                        // User not found, logout
                        authRepository.logout();
                        Log.d(TAG, "Current user not found, logging out");
                    }
                }
            });
        }
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        authRepository.cleanup();
    }
}