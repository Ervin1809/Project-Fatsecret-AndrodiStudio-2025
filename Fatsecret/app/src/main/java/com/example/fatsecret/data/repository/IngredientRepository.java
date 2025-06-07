package com.example.fatsecret.data.repository;

import android.content.Context;
import com.example.fatsecret.data.helper.IngredientHelper;
import com.example.fatsecret.data.model.Ingredient;
import java.util.List;

public class IngredientRepository {
    private final IngredientHelper helper;

    public IngredientRepository(Context context) {
        helper = new IngredientHelper(context);
    }

    public long insert(Ingredient data) {
        return helper.insert(data);
    }

    public Ingredient getById(int id) {
        return helper.getById(id);
    }

    public List<Ingredient> getAll() {
        return helper.getAll();
    }

    public int update(Ingredient data) {
        return helper.update(data);
    }

    public int delete(int id) {
        return helper.delete(id);
    }
}