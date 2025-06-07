package com.example.fatsecret.data.repository;

import android.content.Context;

import com.example.fatsecret.data.helper.FoodLogItemHelper;
import com.example.fatsecret.data.model.FoodLogItem;

import java.util.List;

public class FoodLogItemRepository {
    private final FoodLogItemHelper helper;

    public FoodLogItemRepository(Context context) {
        helper = new FoodLogItemHelper(context);
    }

    public long insert(FoodLogItem data) {
        return helper.insert(data);
    }

    public FoodLogItem getById(int id) {
        return helper.getById(id);
    }

    public List<FoodLogItem> getAll() {
        return helper.getAll();
    }

    public int update(FoodLogItem data) {
        return helper.update(data);
    }

    public int delete(int id) {
        return helper.delete(id);
    }
}