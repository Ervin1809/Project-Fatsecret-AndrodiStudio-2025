package com.example.fatsecret.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fatsecret.R;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvError, tvRegister;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        if (authViewModel.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupObservers() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
                btnLogin.setText("Signing In...");
            } else {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In");
            }
        });

        // Observe login success
        authViewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        // Observe errors
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);

                // Auto hide error after 5 seconds
                tvError.postDelayed(() -> {
                    tvError.setVisibility(View.GONE);
                    authViewModel.clearError();
                }, 5000);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        // Login button
        btnLogin.setOnClickListener(v -> {
            hideError();
            performLogin();
        });

        // Register link
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Hide error when user starts typing
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideError();
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideError();
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Clear errors
        etEmail.setError(null);
        etPassword.setError(null);

        // Perform login
        authViewModel.login(email, password);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}