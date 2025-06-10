package com.example.fatsecret.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.model.FoodLog;
import com.example.fatsecret.utils.MealTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryMealAdapter extends RecyclerView.Adapter<HistoryMealAdapter.MealViewHolder> {
    private static final String TAG = "HistoryMealAdapter";

    private List<FoodLog> foodLogs = new ArrayList<>();

    public void updateLogs(List<FoodLog> logs) {
        this.foodLogs = logs != null ? logs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        FoodLog foodLog = foodLogs.get(position);

        // Get meal emoji and name
        MealTime mealTime = MealTime.fromValue(foodLog.getMealTime());
        String mealEmoji = getMealEmoji(mealTime);

        // Set meal info
        holder.mealNameText.setText(mealTime.getDisplayName());

        // Set calories
        String caloriesText = String.format(Locale.getDefault(), "%.0f cal", foodLog.getTotalCalories());
        holder.caloriesText.setText(caloriesText);

        // Set nutrition breakdown
        String nutritionText = String.format(Locale.getDefault(),
                "P: %.1fg • C: %.1fg • F: %.1fg",
                foodLog.getTotalProtein(),
                foodLog.getTotalCarbs(),
                foodLog.getTotalFat());
        holder.nutritionText.setText(nutritionText);
    }

    @Override
    public int getItemCount() {
        return foodLogs.size();
    }

    private String getMealEmoji(MealTime mealTime) {
        switch (mealTime) {
            case BREAKFAST: return "🌅";
            case LUNCH: return "🌞";
            case DINNER: return "🌆";
            case SNACK: return "🍿";
            default: return "🍽️";
        }
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealNameText;
        TextView caloriesText;
        TextView nutritionText;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameText = itemView.findViewById(R.id.tvMealName);
            caloriesText = itemView.findViewById(R.id.tvCalories);
            nutritionText = itemView.findViewById(R.id.tvNutrition);
        }
    }
}