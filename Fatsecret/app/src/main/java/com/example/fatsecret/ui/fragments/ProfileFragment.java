package com.example.fatsecret.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fatsecret.R;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.ui.LoginActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    // Views
    private ImageView ivProfilePicture;
    private TextView tvUserName, tvUserEmail, tvMemberSince;
    private TextView tvHeight, tvWeight, tvTargetWeight, tvBMI, tvBMICategory;
    private MaterialCardView cardHealthProfile;
    private LinearLayout layoutHealthStats, layoutEmptyHealthProfile;
    private LinearLayout layoutEditProfile, layoutChangePassword, layoutLogout;

    // ViewModel
    private AuthViewModel authViewModel;

    // Data
    private User currentUser;
    private UserProfile userProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupViewModel();
        setupClickListeners();
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        // Profile header
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);

        // Health profile
        cardHealthProfile = view.findViewById(R.id.cardHealthProfile);
        layoutHealthStats = view.findViewById(R.id.layoutHealthStats);
        layoutEmptyHealthProfile = view.findViewById(R.id.layoutEmptyHealthProfile);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvTargetWeight = view.findViewById(R.id.tvTargetWeight);
        tvBMI = view.findViewById(R.id.tvBMI);
        tvBMICategory = view.findViewById(R.id.tvBMICategory);

        // Settings options
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutLogout = view.findViewById(R.id.layoutLogout);
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Observe current user
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                updateUserUI(user);
            }
        });

        // Observe user profile
        authViewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                userProfile = profile;
                updateHealthProfileUI(profile);
            } else {
                showEmptyHealthProfile();
            }
        });
    }

    private void setupClickListeners() {
        // Health profile card
        cardHealthProfile.setOnClickListener(v -> {
            if (userProfile == null) {
                // Show health profile setup dialog
                showHealthProfileDialog();
            } else {
                // Show edit health profile dialog
                showEditHealthProfileDialog();
            }
        });

        // Edit profile
        layoutEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to edit profile activity
            Toast.makeText(getContext(), "Edit Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Change password
        layoutChangePassword.setOnClickListener(v -> {
            // TODO: Navigate to change password activity
            Toast.makeText(getContext(), "Change Password - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Logout
        layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserData() {
        // Load current user if not already loaded
        if (currentUser == null) {
            authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    // Load user profile
                    authViewModel.loadUserProfile(user.getId());
                }
            });
        }
    }

    private void updateUserUI(User user) {
        tvUserName.setText(user.getName());
        tvUserEmail.setText(user.getEmail());

        // Format member since date
        String memberSince = formatMemberSinceDate(user.getCreatedAt());
        tvMemberSince.setText("Member since " + memberSince);

        // TODO: Load profile picture if available
        // For now, show default profile icon
        ivProfilePicture.setImageResource(R.drawable.ic_profile);
    }

    private void updateHealthProfileUI(UserProfile profile) {
        layoutHealthStats.setVisibility(View.VISIBLE);
        layoutEmptyHealthProfile.setVisibility(View.GONE);

        // Update health stats
        tvHeight.setText(String.format(Locale.getDefault(), "%.1f cm", profile.getHeight()));
        tvWeight.setText(String.format(Locale.getDefault(), "%.1f kg", profile.getWeight()));
        tvTargetWeight.setText(String.format(Locale.getDefault(), "%.1f kg", profile.getTargetWeight()));

        // Calculate and display BMI
        calculateAndDisplayBMI(profile.getHeight(), profile.getWeight());
    }

    private void showEmptyHealthProfile() {
        layoutHealthStats.setVisibility(View.GONE);
        layoutEmptyHealthProfile.setVisibility(View.VISIBLE);
    }

    private void calculateAndDisplayBMI(float height, float weight) {
        // Convert height from cm to meters
        float heightInMeters = height / 100f;

        // Calculate BMI
        float bmi = weight / (heightInMeters * heightInMeters);

        // Format BMI to 1 decimal place
        DecimalFormat df = new DecimalFormat("#.#");
        tvBMI.setText(df.format(bmi));

        // Determine BMI category
        String category;
        int categoryColor;

        if (bmi < 18.5) {
            category = "(Underweight)";
            categoryColor = R.color.warning;
        } else if (bmi < 25.0) {
            category = "(Normal)";
            categoryColor = R.color.success;
        } else if (bmi < 30.0) {
            category = "(Overweight)";
            categoryColor = R.color.warning;
        } else {
            category = "(Obese)";
            categoryColor = R.color.error;
        }

        tvBMICategory.setText(category);
        tvBMICategory.setTextColor(getResources().getColor(categoryColor, null));
    }

    private void showHealthProfileDialog() {
        // TODO: Create health profile setup dialog
        Toast.makeText(getContext(), "Health Profile Setup - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void showEditHealthProfileDialog() {
        // TODO: Create edit health profile dialog
        Toast.makeText(getContext(), "Edit Health Profile - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    authViewModel.logout();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private String formatMemberSinceDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "Recently";
        }
    }
}