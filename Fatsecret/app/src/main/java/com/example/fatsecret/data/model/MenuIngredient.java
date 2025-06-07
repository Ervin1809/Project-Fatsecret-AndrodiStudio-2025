package com.example.fatsecret.data.model;

public class MenuIngredient {
    private int id;
    private int menuId;
    private int ingredientId;
    private float quantityInGrams;
    private String createdAt;

    // For display purposes (tidak ada di database, untuk UI saja)
    private Ingredient ingredient;
    private float quantity;    // User-friendly quantity
    private String unit;       // User-friendly unit

    // Default Constructor
    public MenuIngredient() {}

    // Constructor yang match dengan MappingHelper
    public MenuIngredient(int id, int menuId, int ingredientId, float quantityInGrams) {
        this.id = id;
        this.menuId = menuId;
        this.ingredientId = ingredientId;
        this.quantityInGrams = quantityInGrams;
    }

    // Constructor untuk create new MenuIngredient
    public MenuIngredient(int menuId, int ingredientId, float quantityInGrams) {
        this.menuId = menuId;
        this.ingredientId = ingredientId;
        this.quantityInGrams = quantityInGrams;
        this.createdAt = getCurrentTimestamp();
    }

    // Constructor dengan quantity dan unit (untuk UI convenience)
    public MenuIngredient(int menuId, int ingredientId, float quantity,
                          float quantityInGrams, String unit) {
        this(menuId, ingredientId, quantityInGrams);
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public float getQuantityInGrams() { return quantityInGrams; }
    public void setQuantityInGrams(float quantityInGrams) { this.quantityInGrams = quantityInGrams; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // UI Helper properties
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public float getQuantity() { return quantity; }
    public void setQuantity(float quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    public String getDisplayText() {
        if (ingredient != null) {
            if (quantity > 0 && unit != null) {
                return quantity + " " + unit + " " + ingredient.getName();
            } else {
                return quantityInGrams + "g " + ingredient.getName();
            }
        }
        return quantityInGrams + "g";
    }
}