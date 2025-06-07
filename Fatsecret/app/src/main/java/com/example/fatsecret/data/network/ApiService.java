package com.example.fatsecret.data.network;

import com.example.fatsecret.data.model.UsdaFoodDetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Kalori (Energy) - nutrient code 208
    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getCalories(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "208"
    );

    // Protein - nutrient code 203
    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getProtein(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "203"
    );

    // Fat - nutrient code 204
    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getFat(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "204"
    );

    // Carbs - nutrient code 205
    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getCarbs(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "205"
    );
}