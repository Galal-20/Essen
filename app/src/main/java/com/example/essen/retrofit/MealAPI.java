package com.example.essen.retrofit;

import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.CatygoryByMeal;
import com.example.essen.pojo.Countries;
import com.example.essen.pojo.MealList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealAPI {
    @GET("random.php")
    Call<MealList> getRandom();
    @GET("categories.php")
    Call<CategoryList> getCategories();
    @GET("filter.php?")
    Call<CatygoryByMeal> getMealByCategory(@Query("c") String categoryName);
    @GET("search.php?")
    Call<MealList> getSearchMeal(@Query("f") String searchQuery);

    @GET("list.php?a=list")
    Call<Countries> getCountries(); // Fetches list of countries

    @GET("filter.php")
    Call<MealList> getMealsByCountry(@Query("a") String countryName); // Fetches meals by country

}


