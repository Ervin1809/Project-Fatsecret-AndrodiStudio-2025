package com.example.fatsecret.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.fatsecret.R;
import com.example.fatsecret.utils.MealTime;
import com.example.fatsecret.data.model.Ingredient;
import com.google.android.material.button.MaterialButton;

public class WeightInputDialog extends DialogFragment {
    private static final String TAG = "WeightInputDialog";

    private static final String ARG_MEAL_TYPE = "meal_type";

    private Ingredient ingredient;
    private MealTime mealType;
    private OnWeightConfirmedListener listener;

    public interface OnWeightConfirmedListener {
        void onWeightConfirmed(Ingredient ingredient, MealTime mealType, double weightInGrams);
    }

    // ✅ MODIFY: newInstance method around line 30-50
    public static WeightInputDialog newInstance(Ingredient ingredient, MealTime mealType) {
        WeightInputDialog dialog = new WeightInputDialog();
        Bundle args = new Bundle();

        // ✅ FIXED: Pass as Float instead of Double
        args.putString("ingredient_name", ingredient.getName());
        args.putFloat("ingredient_calories", (float) ingredient.getCaloriesPer100g());
        args.putFloat("ingredient_protein", (float) ingredient.getProteinPer100g());
        args.putFloat("ingredient_carbs", (float) ingredient.getCarbsPer100g());
        args.putFloat("ingredient_fat", (float) ingredient.getFatPer100g());
        args.putString("ingredient_api_source", ingredient.getApiSource());
        args.putSerializable(ARG_MEAL_TYPE, mealType);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnWeightConfirmedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement OnWeightConfirmedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            // ✅ FIXED: Get as Float instead of Double
            String name = getArguments().getString("ingredient_name");
            float calories = getArguments().getFloat("ingredient_calories");
            float protein = getArguments().getFloat("ingredient_protein");
            float carbs = getArguments().getFloat("ingredient_carbs");
            float fat = getArguments().getFloat("ingredient_fat");
            String apiSource = getArguments().getString("ingredient_api_source");

            // Create ingredient object
            ingredient = new Ingredient();
            ingredient.setName(name);
            ingredient.setCaloriesPer100g(calories);
            ingredient.setProteinPer100g(protein);
            ingredient.setCarbsPer100g(carbs);
            ingredient.setFatPer100g(fat);
            ingredient.setApiSource(apiSource);

            mealType = (MealTime) getArguments().getSerializable(ARG_MEAL_TYPE);
        }

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_weight_input, null);

        setupViews(view);

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private void setupViews(View view) {
        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        TextView tvFoodName = view.findViewById(R.id.tvFoodName);
        TextView tvNutritionPreview = view.findViewById(R.id.tvNutritionPreview);
        EditText etWeight = view.findViewById(R.id.etWeight);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);

        // Set title
        String title = "Add to " + mealType.getDisplayName();
        tvTitle.setText(title);

        // Set food name
        tvFoodName.setText(ingredient.getName());

        // Default weight 100g
        etWeight.setText("100");
        updateNutritionPreview(tvNutritionPreview, 100.0);

        // ✅ FIXED: Use addTextChangedListener
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String weightText = s.toString().trim();
                if (!TextUtils.isEmpty(weightText)) {
                    try {
                        double weight = Double.parseDouble(weightText);
                        updateNutritionPreview(tvNutritionPreview, weight);
                    } catch (NumberFormatException e) {
                        tvNutritionPreview.setText("Invalid weight");
                    }
                } else {
                    tvNutritionPreview.setText("");
                }
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dismiss());

        // Add button
        btnAdd.setOnClickListener(v -> {
            String weightText = etWeight.getText().toString().trim();

            if (TextUtils.isEmpty(weightText)) {
                etWeight.setError("Please enter weight");
                return;
            }

            try {
                double weight = Double.parseDouble(weightText);

                if (weight <= 0) {
                    etWeight.setError("Weight must be greater than 0");
                    return;
                }

                if (weight > 9999) {
                    etWeight.setError("Weight too large");
                    return;
                }

                Log.d(TAG, "✅ Adding food: " + ingredient.getName() +
                        " (" + weight + "g) to " + mealType.getDisplayName());

                if (listener != null) {
                    listener.onWeightConfirmed(ingredient, mealType, weight);
                }

                dismiss();

            } catch (NumberFormatException e) {
                etWeight.setError("Invalid number");
            }
        });
    }

    private void updateNutritionPreview(TextView tvPreview, double weightInGrams) {
        if (ingredient == null) return;

        double factor = weightInGrams / 100.0;

        double calories = ingredient.getCaloriesPer100g() * factor;
        double protein = ingredient.getProteinPer100g() * factor;
        double carbs = ingredient.getCarbsPer100g() * factor;
        double fat = ingredient.getFatPer100g() * factor;

        String preview = String.format(
                "%.0f kcal • %.1fg protein • %.1fg carbs • %.1fg fat",
                calories, protein, carbs, fat
        );

        tvPreview.setText(preview);
    }
}