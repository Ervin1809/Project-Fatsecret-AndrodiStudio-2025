package com.example.fatsecret.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fatsecret.R;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.utils.NutritionCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // ViewModels
    private AuthViewModel authViewModel;

    // UI Components
    private TextView tvUsername, tvMemberSince;
    private TextView tvHeight, tvWeight, tvTargetWeight, tvBMI, tvBMICategory;
    private TextView tvDailyCalories, tvDailyProtein, tvDailyCarbs, tvDailyFat, tvMetabolicInfo;
    private TextView tvGender, tvAge, tvActivityLevel;
    private CardView cardNutritionTargets, cardPersonalInfo;
    private MaterialButton btnEditProfile, btnCompleteProfile, btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize UI
        initializeViews(view);
        setupClickListeners();
        observeData();

        // 🧪 Test database (remove this later)
//        testNutritionCalculator();
    }

    private void initializeViews(View view) {
        // Header
        tvUsername = view.findViewById(R.id.tvUsername);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);

        // Health Stats
        tvHeight = view.findViewById(R.id.tvHeight);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvTargetWeight = view.findViewById(R.id.tvTargetWeight);
        tvBMI = view.findViewById(R.id.tvBMI);
        tvBMICategory = view.findViewById(R.id.tvBMICategory);

        // Nutrition Targets
        cardNutritionTargets = view.findViewById(R.id.cardNutritionTargets);
        tvDailyCalories = view.findViewById(R.id.tvDailyCalories);
        tvDailyProtein = view.findViewById(R.id.tvDailyProtein);
        tvDailyCarbs = view.findViewById(R.id.tvDailyCarbs);
        tvDailyFat = view.findViewById(R.id.tvDailyFat);
        tvMetabolicInfo = view.findViewById(R.id.tvMetabolicInfo);

        // Personal Info
        cardPersonalInfo = view.findViewById(R.id.cardPersonalInfo);
        tvGender = view.findViewById(R.id.tvGender);
        tvAge = view.findViewById(R.id.tvAge);
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);

        // Buttons
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnCompleteProfile = view.findViewById(R.id.btnCompleteProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            UserProfile currentProfile = authViewModel.getUserProfile().getValue();
            showHealthProfileDialog(currentProfile);
        });

        btnCompleteProfile.setOnClickListener(v -> {
            UserProfile currentProfile = authViewModel.getUserProfile().getValue();
            showHealthProfileDialog(currentProfile);
        });

        btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
            // Navigate to login (implement as needed)
        });
    }

    private void observeData() {
        // Observe current user
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            Log.d(TAG, "🔧 DEBUG: Current user observer triggered");
            if (user != null) {
                Log.d(TAG, "🔧 DEBUG: User found: " + user.getName());
                updateUserInfo(user);
                // Load user profile
                authViewModel.loadUserProfile(user.getId());
            } else {
                Log.d(TAG, "🔧 DEBUG: User is null");
            }
        });

        // ✅ FIXED: Observe user profile with extensive debugging
        authViewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            Log.d(TAG, "🔧 DEBUG: User profile observer triggered");
            if (profile != null) {
                Log.d(TAG, "🔧 DEBUG: Profile received - Height: " + profile.getHeight());
                Log.d(TAG, "🔧 DEBUG: Profile received - Weight: " + profile.getWeight());
                Log.d(TAG, "🔧 DEBUG: Profile received - Gender: " + profile.getGender());
                Log.d(TAG, "🔧 DEBUG: Profile received - Age: " + profile.getAge());
                Log.d(TAG, "🔧 DEBUG: Profile received - Activity: " + profile.getActivityLevel());
                Log.d(TAG, "🔧 DEBUG: Profile received - Daily Calories: " + profile.getDailyCaloriesTarget());
                Log.d(TAG, "🔧 DEBUG: Profile received - Daily Protein: " + profile.getDailyProteinTarget());
                Log.d(TAG, "🔧 DEBUG: Profile received - Daily Carbs: " + profile.getDailyCarbsTarget());
                Log.d(TAG, "🔧 DEBUG: Profile received - Daily Fat: " + profile.getDailyFatTarget());

                // ✅ DEBUG: Check each condition for hasCompleteNutritionData
                boolean hasGender = profile.getGender() != null && !profile.getGender().trim().isEmpty();
                boolean hasAge = profile.getAge() > 0;
                boolean hasActivity = profile.getActivityLevel() != null && !profile.getActivityLevel().trim().isEmpty();
                boolean hasCalories = profile.getDailyCaloriesTarget() > 0;
                boolean hasProtein = profile.getDailyProteinTarget() > 0;
                boolean hasCarbs = profile.getDailyCarbsTarget() > 0;
                boolean hasFat = profile.getDailyFatTarget() > 0;

                Log.d(TAG, "🔧 DEBUG: hasGender: " + hasGender);
                Log.d(TAG, "🔧 DEBUG: hasAge: " + hasAge);
                Log.d(TAG, "🔧 DEBUG: hasActivity: " + hasActivity);
                Log.d(TAG, "🔧 DEBUG: hasCalories: " + hasCalories);
                Log.d(TAG, "🔧 DEBUG: hasProtein: " + hasProtein);
                Log.d(TAG, "🔧 DEBUG: hasCarbs: " + hasCarbs);
                Log.d(TAG, "🔧 DEBUG: hasFat: " + hasFat);

                boolean hasCompleteData = profile.hasCompleteNutritionData();
                Log.d(TAG, "🔧 DEBUG: Profile has complete nutrition data: " + hasCompleteData);

                updateProfileDisplay(profile);
            } else {
                Log.d(TAG, "🔧 DEBUG: Profile is null");
                showEmptyProfileState();
            }
        });

        // ✅ TAMBAH: Observer error untuk debug
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.d(TAG, "🔧 DEBUG: Error received: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                authViewModel.clearError();
            }
        });
    }

    private void updateUserInfo(User user) {
        tvUsername.setText("Welcome, " + user.getName() + "!");
        tvMemberSince.setText("Member since: " + user.getCreatedAt().substring(0, 4));
    }

    private void updateProfileDisplay(UserProfile profile) {
        Log.d(TAG, "🔧 DEBUG: updateProfileDisplay called");

        // Update basic health stats
        tvHeight.setText(String.format("%.0f", profile.getHeight()));
        tvWeight.setText(String.format("%.1f", profile.getWeight()));
        tvTargetWeight.setText(String.format("%.1f", profile.getTargetWeight()));

        Log.d(TAG, "🔧 DEBUG: Basic stats updated");

        // Calculate and display BMI
        float bmi = profile.calculateBMI();
        if (bmi > 0) {
            tvBMI.setText(String.format("%.1f", bmi));
            tvBMICategory.setText("(" + profile.getBMICategory() + ")");
        } else {
            tvBMI.setText("--");
            tvBMICategory.setText("");
        }

        // ✅ FIXED: Always try to show nutrition data first, then check
        Log.d(TAG, "🔧 DEBUG: Checking complete nutrition data: " + profile.hasCompleteNutritionData());
        if (profile.hasCompleteNutritionData()) {
            Log.d(TAG, "🔧 DEBUG: Showing nutrition data");
            showNutritionData(profile);
        } else {
            Log.d(TAG, "🔧 DEBUG: Hiding nutrition data - trying force show anyway");
            // ✅ TEMPORARY: Force show nutrition data for debugging
            if (profile.getDailyCaloriesTarget() > 0) {
                Log.d(TAG, "🔧 DEBUG: Force showing nutrition data because calories > 0");
                showNutritionData(profile);
            } else {
                hideNutritionData();
            }
        }
    }

    private void showNutritionData(UserProfile profile) {
        Log.d(TAG, "🔧 DEBUG: showNutritionData called");

        // Show nutrition cards
        cardNutritionTargets.setVisibility(View.VISIBLE);
        cardPersonalInfo.setVisibility(View.VISIBLE);
        btnCompleteProfile.setVisibility(View.GONE);

        Log.d(TAG, "🔧 DEBUG: Cards visibility set to VISIBLE");

        // Update nutrition targets
        int calories = Math.round(profile.getDailyCaloriesTarget());
        int protein = Math.round(profile.getDailyProteinTarget());
        int carbs = Math.round(profile.getDailyCarbsTarget());
        int fat = Math.round(profile.getDailyFatTarget());

        tvDailyCalories.setText(calories + " kcal");
        tvDailyProtein.setText(protein + "g");
        tvDailyCarbs.setText(carbs + "g");
        tvDailyFat.setText(fat + "g");

        Log.d(TAG, "🔧 DEBUG: Nutrition targets set - Calories: " + calories + ", Protein: " + protein);

        // Update metabolic info
        float bmr = profile.calculateBMR();
        float tdee = profile.calculateTDEE();
        tvMetabolicInfo.setText(Math.round(bmr) + " / " + Math.round(tdee) + " kcal");

        // Update personal info
        String gender = capitalizeFirst(profile.getGender());
        String age = profile.getAge() + " years";
        String activity = getActivityLevelDisplayName(profile.getActivityLevel());

        tvGender.setText(gender);
        tvAge.setText(age);
        tvActivityLevel.setText(activity);

        Log.d(TAG, "🔧 DEBUG: Personal info updated - Gender: " + gender + ", Age: " + age + ", Activity: " + activity);
    }

    private void hideNutritionData() {
        cardNutritionTargets.setVisibility(View.GONE);
        cardPersonalInfo.setVisibility(View.GONE);
        btnCompleteProfile.setVisibility(View.VISIBLE);
    }

    private void showEmptyProfileState() {
        tvHeight.setText("--");
        tvWeight.setText("--");
        tvTargetWeight.setText("--");
        tvBMI.setText("--");
        tvBMICategory.setText("");
        hideNutritionData();
    }

    private void showHealthProfileDialog(UserProfile existingProfile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_health_profile, null);
        builder.setView(dialogView);

        // Get dialog views
        TextInputEditText etHeight = dialogView.findViewById(R.id.etHeight);
        TextInputEditText etWeight = dialogView.findViewById(R.id.etWeight);
        TextInputEditText etTargetWeight = dialogView.findViewById(R.id.etTargetWeight);
        TextInputEditText etAge = dialogView.findViewById(R.id.etAge);
        RadioGroup rgGender = dialogView.findViewById(R.id.rgGender);
        RadioButton rbMale = dialogView.findViewById(R.id.rbMale);
        RadioButton rbFemale = dialogView.findViewById(R.id.rbFemale);
        Spinner spinnerActivityLevel = dialogView.findViewById(R.id.spinnerActivityLevel);
        LinearLayout layoutCalculatedTargets = dialogView.findViewById(R.id.layoutCalculatedTargets);
        TextView tvCalculatedTargets = dialogView.findViewById(R.id.tvCalculatedTargets);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);

        // Setup Activity Level Spinner
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                NutritionCalculator.getActivityLevelDisplayNames()
        );
        spinnerActivityLevel.setAdapter(activityAdapter);

        // Fill existing data if editing
        if (existingProfile != null) {
            etHeight.setText(String.valueOf(existingProfile.getHeight()));
            etWeight.setText(String.valueOf(existingProfile.getWeight()));
            etTargetWeight.setText(String.valueOf(existingProfile.getTargetWeight()));

            if (existingProfile.getAge() > 0) {
                etAge.setText(String.valueOf(existingProfile.getAge()));
            }

            if (existingProfile.getGender() != null) {
                if ("male".equalsIgnoreCase(existingProfile.getGender())) {
                    rbMale.setChecked(true);
                } else if ("female".equalsIgnoreCase(existingProfile.getGender())) {
                    rbFemale.setChecked(true);
                }
            }

            if (existingProfile.getActivityLevel() != null) {
                String[] activityLevels = NutritionCalculator.getActivityLevelOptions();
                for (int i = 0; i < activityLevels.length; i++) {
                    if (activityLevels[i].equals(existingProfile.getActivityLevel())) {
                        spinnerActivityLevel.setSelection(i);
                        break;
                    }
                }
            }
        }

        AlertDialog dialog = builder.create();

        // Real-time calculation method
        Runnable calculateAndDisplayTargets = new Runnable() {
            @Override
            public void run() {
                try {
                    String heightStr = etHeight.getText().toString().trim();
                    String weightStr = etWeight.getText().toString().trim();
                    String targetWeightStr = etTargetWeight.getText().toString().trim();
                    String ageStr = etAge.getText().toString().trim();

                    if (heightStr.isEmpty() || weightStr.isEmpty() || targetWeightStr.isEmpty() || ageStr.isEmpty()) {
                        layoutCalculatedTargets.setVisibility(View.GONE);
                        return;
                    }

                    String gender = rbMale.isChecked() ? "male" : (rbFemale.isChecked() ? "female" : null);
                    if (gender == null) {
                        layoutCalculatedTargets.setVisibility(View.GONE);
                        return;
                    }

                    // Create temp profile for calculation
                    UserProfile tempProfile = new UserProfile();
                    tempProfile.setHeight(Float.parseFloat(heightStr));
                    tempProfile.setWeight(Float.parseFloat(weightStr));
                    tempProfile.setTargetWeight(Float.parseFloat(targetWeightStr));
                    tempProfile.setAge(Integer.parseInt(ageStr));
                    tempProfile.setGender(gender);

                    String[] activityLevels = NutritionCalculator.getActivityLevelOptions();
                    int selectedPosition = spinnerActivityLevel.getSelectedItemPosition();
                    if (selectedPosition >= 0 && selectedPosition < activityLevels.length) {
                        tempProfile.setActivityLevel(activityLevels[selectedPosition]);

                        // Calculate targets
                        NutritionCalculator.calculateAndSetNutritionTargets(tempProfile);

                        // Display results
                        String targetsText = "🎯 Daily Targets:\n" +
                                "• Calories: " + Math.round(tempProfile.getDailyCaloriesTarget()) + " kcal\n" +
                                "• Protein: " + Math.round(tempProfile.getDailyProteinTarget()) + "g\n" +
                                "• Carbs: " + Math.round(tempProfile.getDailyCarbsTarget()) + "g\n" +
                                "• Fat: " + Math.round(tempProfile.getDailyFatTarget()) + "g\n\n" +
                                "📊 BMR: " + Math.round(tempProfile.calculateBMR()) + " kcal | TDEE: " + Math.round(tempProfile.calculateTDEE()) + " kcal";

                        tvCalculatedTargets.setText(targetsText);
                        layoutCalculatedTargets.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    layoutCalculatedTargets.setVisibility(View.GONE);
                }
            }
        };

        // Real-time calculation listener
        TextWatcher calculationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateAndDisplayTargets.run();
            }
        };

        // Add listeners for real-time calculation
        etHeight.addTextChangedListener(calculationWatcher);
        etWeight.addTextChangedListener(calculationWatcher);
        etTargetWeight.addTextChangedListener(calculationWatcher);
        etAge.addTextChangedListener(calculationWatcher);

        rgGender.setOnCheckedChangeListener((group, checkedId) -> calculateAndDisplayTargets.run());
        spinnerActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAndDisplayTargets.run();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Button listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String heightStr = etHeight.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();
            String targetWeightStr = etTargetWeight.getText().toString().trim();

            if (heightStr.isEmpty() || weightStr.isEmpty() || targetWeightStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill height, weight, and target weight", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float height = Float.parseFloat(heightStr);
                float weight = Float.parseFloat(weightStr);
                float targetWeight = Float.parseFloat(targetWeightStr);

                if (height <= 0 || weight <= 0 || targetWeight <= 0) {
                    Toast.makeText(getContext(), "Please enter valid positive numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                User currentUser = authViewModel.getCurrentUser().getValue();
                if (currentUser == null) {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserProfile profile;
                if (existingProfile != null) {
                    profile = existingProfile;
                } else {
                    profile = new UserProfile();
                    profile.setUserId(currentUser.getId());
                }

                // Set basic data
                profile.setHeight(height);
                profile.setWeight(weight);
                profile.setTargetWeight(targetWeight);

                // Set optional nutrition data
                String ageStr = etAge.getText().toString().trim();
                if (!ageStr.isEmpty()) {
                    profile.setAge(Integer.parseInt(ageStr));
                }

                if (rbMale.isChecked()) {
                    profile.setGender("male");
                } else if (rbFemale.isChecked()) {
                    profile.setGender("female");
                }

                String[] activityLevels = NutritionCalculator.getActivityLevelOptions();
                int selectedPosition = spinnerActivityLevel.getSelectedItemPosition();
                if (selectedPosition >= 0 && selectedPosition < activityLevels.length) {
                    profile.setActivityLevel(activityLevels[selectedPosition]);
                }

                // Calculate nutrition targets if data is complete
                if (profile.hasCompleteNutritionData()) {
                    NutritionCalculator.calculateAndSetNutritionTargets(profile);
                    Log.d(TAG, "✅ Nutrition targets calculated and set");
                }

                // Save profile
                if (existingProfile != null) {
                    authViewModel.updateUserProfile(profile);
                } else {
                    authViewModel.createUserProfile(profile);
                }

                dialog.dismiss();

                // ✅ ADD: Manual reload after save (since we removed the observer)
//                User currentUser = authViewModel.getCurrentUser().getValue();
                if (currentUser != null) {
                    // ✅ Delay reload lebih lama dan force pada main thread
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        Log.d(TAG, "🔧 DEBUG: Manual reload triggered for user ID: " + currentUser.getId());
                        authViewModel.loadUserProfile(currentUser.getId());
                    }, 1000); // ← Ganti dari 500ms ke 1000ms
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // Helper methods
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return "Not set";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String getActivityLevelDisplayName(String activityLevel) {
        if (activityLevel == null) return "Not set";

        String[] levels = NutritionCalculator.getActivityLevelOptions();
        String[] displayNames = NutritionCalculator.getActivityLevelDisplayNames();

        for (int i = 0; i < levels.length; i++) {
            if (levels[i].equals(activityLevel)) {
                return displayNames[i];
            }
        }
        return capitalizeFirst(activityLevel);
    }

    // 🧪 Test method (remove later)
    private void testNutritionCalculator() {
        Log.d(TAG, "🧪 Testing Nutrition Calculator...");

        UserProfile testProfile = new UserProfile();
        testProfile.setHeight(175f);
        testProfile.setWeight(70f);
        testProfile.setTargetWeight(65f);
        testProfile.setGender("male");
        testProfile.setAge(25);
        testProfile.setActivityLevel("moderate");

        NutritionCalculator.calculateAndSetNutritionTargets(testProfile);

        Log.d(TAG, "🎯 Expected Results for 25yo Male, 175cm, 70kg, moderate activity:");
        Log.d(TAG, "BMR: ~1831 kcal (actual: " + testProfile.calculateBMR() + ")");
        Log.d(TAG, "TDEE: ~2838 kcal (actual: " + testProfile.calculateTDEE() + ")");
        Log.d(TAG, "Daily Calories: ~2338 kcal (deficit for weight loss, actual: " + testProfile.getDailyCaloriesTarget() + ")");
        Log.d(TAG, "Protein: ~146g (actual: " + testProfile.getDailyProteinTarget() + ")");
        Log.d(TAG, "Carbs: ~263g (actual: " + testProfile.getDailyCarbsTarget() + ")");
        Log.d(TAG, "Fat: ~78g (actual: " + testProfile.getDailyFatTarget() + ")");
    }
}