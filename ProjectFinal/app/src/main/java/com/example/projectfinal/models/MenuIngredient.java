package com.example.projectfinal.models;

public class MenuIngredient {
    int id, menuId, ingredientId; // foreign key ke ingredients
    float quantityInGrams;

    public MenuIngredient(int id, int menuId, int ingredientId, float quantityInGrams) {
        this.id = id;
        this.menuId = menuId;
        this.ingredientId = ingredientId;
        this.quantityInGrams = quantityInGrams;
    }
    public MenuIngredient() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public float getQuantityInGrams() {
        return quantityInGrams;
    }

    public void setQuantityInGrams(float quantityInGrams) {
        this.quantityInGrams = quantityInGrams;
    }
}
