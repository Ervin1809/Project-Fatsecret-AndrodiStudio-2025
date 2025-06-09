package com.example.fatsecret.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UsdaSearchResponse {
    @SerializedName("foods")
    private List<FoodSearchResult> foods;

    @SerializedName("totalHits")
    private int totalHits;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPages")
    private int totalPages;

    // Getters and setters
    public List<FoodSearchResult> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodSearchResult> foods) {
        this.foods = foods;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static class FoodSearchResult {
        @SerializedName("fdcId")
        private int fdcId;

        @SerializedName("description")
        private String description;

        @SerializedName("dataType")
        private String dataType;

        @SerializedName("foodCode")
        private String foodCode;

        @SerializedName("publishedDate")
        private String publishedDate;

        // Getters and setters
        public int getFdcId() {
            return fdcId;
        }

        public void setFdcId(int fdcId) {
            this.fdcId = fdcId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getFoodCode() {
            return foodCode;
        }

        public void setFoodCode(String foodCode) {
            this.foodCode = foodCode;
        }

        public String getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
        }
    }
}