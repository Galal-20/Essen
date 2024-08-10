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

   /* @GET("lookup.php?")
    Call<CatygoryList> getDetailsMeal(@Query("i") String id);

    @GET("filter.php?")
    Call<CatygoryList> getPopularMeal(@Query("c") String categoryName);*/


   /*
   @GET("filter.php?")
    Call<MealsByCategoryList> getFilterMeal(@Query("c") String categoryName);

    @GET("categories.php")
    Call<CategoryList> getCategories();

    @GET("filter.php")
    Call<MealsByCategoryList> getMealsByCategory(@Query("c") String categoryName);*/
}
