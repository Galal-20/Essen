package com.example.essen.Activities.CategoryMealActivity;

import com.example.essen.pojo.Meal;

import java.util.List;

public interface CategoryContract {

    interface View {
        void showCategoryMeal(List<Meal> meal);

        void showError(String message);

        void showSuccess(String message);
    }

    interface presenter {
        void getCategoryByMeal(String categoryName);
    }
}
