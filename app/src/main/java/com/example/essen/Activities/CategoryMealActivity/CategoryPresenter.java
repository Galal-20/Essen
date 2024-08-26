package com.example.essen.Activities.CategoryMealActivity;

import com.example.essen.pojo.CatygoryByMeal;
import com.example.essen.repository.MealRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryPresenter implements CategoryContract.presenter {
    public CategoryContract.View view;
    private MealRepository mealRepository;


    public CategoryPresenter(CategoryContract.View view, MealRepository mealRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
    }


    @Override
    public void getCategoryByMeal(String categoryName) {
        mealRepository.getMealsByCategory(categoryName, new Callback<CatygoryByMeal>() {
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
