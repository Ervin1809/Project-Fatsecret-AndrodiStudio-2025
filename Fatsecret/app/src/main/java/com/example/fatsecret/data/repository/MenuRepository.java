package com.example.fatsecret.data.repository;

import android.content.Context;
import com.example.fatsecret.data.helper.MenuHelper;
import com.example.fatsecret.data.model.Menu;
import java.util.List;

public class MenuRepository {
    private final MenuHelper helper;

    public MenuRepository(Context context) {
        helper = new MenuHelper(context);
    }

    public long insert(Menu data) {
        return helper.insert(data);
    }

    public Menu getById(int id) {
        return helper.getById(id);
    }

    public List<Menu> getAll() {
        return helper.getAll();
    }

    public int update(Menu data) {
        return helper.update(data);
    }

    public int delete(int id) {
        return helper.delete(id);
    }
}