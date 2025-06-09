package com.example.fatsecret.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.helper.FoodLogItemWithDetails;

import java.util.ArrayList;
import java.util.List;

public class FoodLogItemAdapter extends RecyclerView.Adapter<FoodLogItemAdapter.ViewHolder> {
    private List<FoodLogItemWithDetails> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(FoodLogItemWithDetails item);
        void onDeleteClick(FoodLogItemWithDetails item);
    }

    public FoodLogItemAdapter(List<FoodLogItemWithDetails> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodLogItemWithDetails item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<FoodLogItemWithDetails> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvWeight, tvCalories, tvNutrition;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvNutrition = itemView.findViewById(R.id.tvNutrition);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(FoodLogItemWithDetails item) {
            // Set food name
            String foodName = item.getIngredientName() != null ?
                    item.getIngredientName() : "Unknown Food";
            tvFoodName.setText(foodName);

            // Set weight
            tvWeight.setText(String.format("%.0fg", item.getFoodLogItem().getWeightGrams()));

            // Set calories
            tvCalories.setText(String.format("%.0f kcal",
                    item.getFoodLogItem().getCalculatedCalories()));

            // Set nutrition details
            String nutrition = String.format("P: %.1fg • C: %.1fg • F: %.1fg",
                    item.getFoodLogItem().getCalculatedProtein(),
                    item.getFoodLogItem().getCalculatedCarbs(),
                    item.getFoodLogItem().getCalculatedFat());
            tvNutrition.setText(nutrition);

            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(item);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}