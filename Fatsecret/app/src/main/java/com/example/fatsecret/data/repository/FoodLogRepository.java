package com.example.fatsecret.data.repository;

import android.content.Context;
import com.example.fatsecret.data.helper.FoodLogHelper;
import com.example.fatsecret.data.model.FoodLog;
import java.util.List;

public class FoodLogRepository {
    private final FoodLogHelper helper;

    public FoodLogRepository(Context context) {
        helper = new FoodLogHelper(context);
    }

    public long insert(FoodLog data) {
        return helper.insert(data);
    }

    public FoodLog getById(int id) {
        return helper.getById(id);
    }

    public List<FoodLog> getAll() {
        return helper.getAll();
    }

    public int update(FoodLog data) {
        return helper.update(data);
    }

    public int delete(int id) {
        return helper.delete(id);
    }
}