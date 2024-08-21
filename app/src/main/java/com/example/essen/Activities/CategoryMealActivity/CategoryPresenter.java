package com.example.essen.Activities.CategoryMealActivity;

import com.example.essen.pojo.CatygoryByMeal;
import com.example.essen.retrofit.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryPresenter implements CategoryContract.presenter {
    public CategoryContract.View view;

    public CategoryPresenter(CategoryContract.View view) {
        this.view = view;
    }


    @Override
    public void getCategoryByMeal(String categoryName) {
        RetrofitInstance.getApi().getMealByCategory(categoryName).enqueue(new Callback<CatygoryByMeal>() {
            @Override
            public void onResponse(Call<CatygoryByMeal> call, Response<CatygoryByMeal> response) {
                if (response.body() != null && !response.body().getMeals().isEmpty()) {
                    view.showCategoryMeal(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<CatygoryByMeal> call, Throwable t) {
                view.showError("Data not response");
            }
        });
    }
}
