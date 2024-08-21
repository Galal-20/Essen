package com.example.essen.Activities.CategoryMealActivity;

import com.example.essen.pojo.MealX;

import java.util.List;

public interface CategoryContract {

    interface View {
        void showCategoryMeal(List<MealX> meal);

        void showError(String message);

    }

    interface presenter {
        void getCategoryByMeal(String categoryName);

    }
}
