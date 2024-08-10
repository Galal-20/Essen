package com.example.essen.retrofit;

import com.example.essen.pojo.CategoryList;
import com.example.essen.pojo.CatygoryByMeal;
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
    Call<MealList> getSearchMeal(@Query("s") String searchQuery);

}
