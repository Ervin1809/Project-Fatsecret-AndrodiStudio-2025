package com.example.fatsecret.data.network;

import com.example.fatsecret.data.model.UsdaFoodDetailResponse;
import com.example.fatsecret.data.model.UsdaSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ✅ Dynamic food search (new addition)
    @GET("foods/search")
    Call<UsdaSearchResponse> searchFoods(
            @Query("query") String query,
            @Query("api_key") String apiKey,
            @Query("pageSize") int pageSize,
            @Query("pageNumber") int pageNumber,
            @Query("dataType") String dataType
    );

    // ✅ KEEP ORIGINAL 4 ENDPOINTS (yang sudah working kemarin)
    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getCalories(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "208"
    );

    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getProtein(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "203"
    );

    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getFat(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "204"
    );

    @GET("food/{fdcId}")
    Call<UsdaFoodDetailResponse> getCarbs(
            @Path("fdcId") String fdcId,
            @Query("api_key") String apiKey,
            @Query("nutrients") String nutrients  // "205"
    );
}