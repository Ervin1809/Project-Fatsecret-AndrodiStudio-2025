package com.example.fatsecret.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private static final String TAG = "IngredientAdapter";

    private List<Ingredient> ingredients = new ArrayList<>();
    private OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public void setOnIngredientClickListener(OnIngredientClickListener listener) {
        this.listener = listener;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        Log.d(TAG, "🔄 setIngredients called with " +
                (ingredients != null ? ingredients.size() : "null") + " items");

        if (ingredients != null) {
            this.ingredients.clear();
            this.ingredients.addAll(ingredients);

            Log.d(TAG, "✅ Adapter updated, now has " + this.ingredients.size() + " items");
            notifyDataSetChanged();

            // Debug first few items
            for (int i = 0; i < Math.min(3, this.ingredients.size()); i++) {
                Log.d(TAG, "  " + (i+1) + ". " + this.ingredients.get(i).getName());
            }
        } else {
            Log.w(TAG, "❌ setIngredients called with null list");
            this.ingredients.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "🏗️ onCreateViewHolder called");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Log.d(TAG, "🎨 onBindViewHolder called for position: " + position);

        if (position < ingredients.size()) {
            Ingredient current = ingredients.get(position);
            Log.d(TAG, "  Binding ingredient: " + current.getName());
            holder.bind(current);
        } else {
            Log.w(TAG, "❌ Invalid position: " + position + ", ingredients size: " + ingredients.size());
        }
    }

    @Override
    public int getItemCount() {
        int count = ingredients.size();
        Log.d(TAG, "📊 getItemCount: " + count);
        return count;
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        // ✅ FIXED: Update IDs to match XML layout
        private final TextView tvFoodName;          // Changed from tvIngredientName
        private final TextView tvCalories;          // Same
        private final TextView tvProtein;           // New - separate protein TextView
        private final TextView tvCarbs;             // New - separate carbs TextView
        private final TextView tvFat;               // New - separate fat TextView
        private final TextView tvFoodCategory;      // New - for API source

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            // ✅ Map to correct XML IDs
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvProtein = itemView.findViewById(R.id.tvProtein);
            tvCarbs = itemView.findViewById(R.id.tvCarbs);
            tvFat = itemView.findViewById(R.id.tvFat);
            tvFoodCategory = itemView.findViewById(R.id.tvFoodCategory);

            // ✅ Debug: Check if views are found
            Log.d(TAG, "🏗️ ViewHolder created:");
            Log.d(TAG, "  tvFoodName: " + (tvFoodName != null ? "✅" : "❌"));
            Log.d(TAG, "  tvCalories: " + (tvCalories != null ? "✅" : "❌"));
            Log.d(TAG, "  tvProtein: " + (tvProtein != null ? "✅" : "❌"));
            Log.d(TAG, "  tvCarbs: " + (tvCarbs != null ? "✅" : "❌"));
            Log.d(TAG, "  tvFat: " + (tvFat != null ? "✅" : "❌"));
            Log.d(TAG, "  tvFoodCategory: " + (tvFoodCategory != null ? "✅" : "❌"));

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    Log.d(TAG, "🔍 Item clicked at position: " + position);
                    listener.onIngredientClick(ingredients.get(position));
                }
            });
        }

        void bind(Ingredient ingredient) {
            Log.d(TAG, "🎨 Binding ingredient: " + ingredient.getName());

            try {
                // ✅ Food name
                if (tvFoodName != null) {
                    tvFoodName.setText(ingredient.getName());
                    Log.d(TAG, "  ✅ Set food name: " + ingredient.getName());
                }

                // ✅ Calories - just the number (layout already has "kcal" label)
                if (tvCalories != null) {
                    String caloriesText = String.format("%.0f", ingredient.getCaloriesPer100g());
                    tvCalories.setText(caloriesText);
                    Log.d(TAG, "  ✅ Set calories: " + caloriesText);
                }

                // ✅ Protein - just the number (layout already has "protein" label)
                if (tvProtein != null) {
                    String proteinText = String.format("%.1fg", ingredient.getProteinPer100g());
                    tvProtein.setText(proteinText);
                    Log.d(TAG, "  ✅ Set protein: " + proteinText);
                }

                // ✅ Carbs - just the number (layout already has "carbs" label)
                if (tvCarbs != null) {
                    String carbsText = String.format("%.1fg", ingredient.getCarbsPer100g());
                    tvCarbs.setText(carbsText);
                    Log.d(TAG, "  ✅ Set carbs: " + carbsText);
                }

                // ✅ Fat - just the number (layout already has "fat" label)
                if (tvFat != null) {
                    String fatText = String.format("%.1fg", ingredient.getFatPer100g());
                    tvFat.setText(fatText);
                    Log.d(TAG, "  ✅ Set fat: " + fatText);
                }

                // ✅ Source/Category
                if (tvFoodCategory != null) {
                    String source = ingredient.getApiSource();
                    String sourceText = "usda".equals(source) ? "USDA Database" : "Local Database";
                    tvFoodCategory.setText(sourceText);
                    Log.d(TAG, "  ✅ Set source: " + sourceText);
                }

                Log.d(TAG, "✅ Successfully bound ingredient: " + ingredient.getName());

            } catch (Exception e) {
                Log.e(TAG, "❌ Error binding ingredient: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}