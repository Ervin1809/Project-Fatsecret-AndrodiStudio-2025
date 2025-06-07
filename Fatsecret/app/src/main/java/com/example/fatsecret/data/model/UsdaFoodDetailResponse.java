package com.example.fatsecret.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import kotlin.jvm.internal.SerializedIr;

public class UsdaFoodDetailResponse {
    @SerializedName("fdcId")
    private int fdcId;  // Ubah ke int, bukan String

    @SerializedName("description")
    private String description;

    @SerializedName("foodNutrients")
    private List<FoodNutrient> foodNutrients;

    public List<FoodNutrient> getFoodNutrients() {
        return foodNutrients;
    }

    public void setFoodNutrients(List<FoodNutrient> foodNutrients) {
        this.foodNutrients = foodNutrients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Update getter fdcId
    public int getFdcId() {
        return fdcId;
    }

    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    public static class FoodNutrient {
        @SerializedName("number")
        private String number;

        @SerializedName("name")
        private String name;

        @SerializedName("amount")
        private float amount;

        @SerializedName("unitName")
        private String unitName;

        @SerializedName("derivationCode")
        private String derivationCode;

        @SerializedName("derivationDescription")

        private String derivationDescription;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public String getDerivationCode() {
            return derivationCode;
        }

        public void setDerivationCode(String derivationCode) {
            this.derivationCode = derivationCode;
        }

        public String getDerivationDescription() {
            return derivationDescription;
        }

        public void setDerivationDescription(String derivationDescription) {
            this.derivationDescription = derivationDescription;
        }
    }
}