package com.example.fatsecret.data.repository;

import android.content.Context;
import com.example.fatsecret.data.helper.MenuIngredientHelper;
import com.example.fatsecret.data.model.MenuIngredient;
import java.util.List;

public class MenuIngredientRepository {
    private final MenuIngredientHelper helper;

    public MenuIngredientRepository(Context context) {
        helper = new MenuIngredientHelper(context);
    }

    public long insert(MenuIngredient data) {
        return helper.insert(data);
    }

    public MenuIngredient getById(int id) {
        return helper.getById(id);
    }

    public List<MenuIngredient> getAll() {
        return helper.getAll();
    }

    public int update(MenuIngredient data) {
        return helper.update(data);
    }

    public int delete(int id) {
        return helper.delete(id);
    }
}