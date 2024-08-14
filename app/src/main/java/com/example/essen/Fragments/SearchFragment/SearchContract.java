package com.example.essen.Fragments.SearchFragment;

import com.example.essen.pojo.MainMeal;

import java.util.List;

public interface SearchContract {
    interface Presenter {
        void searchMeals(String query);

        void searchMealsByCountry(String country);

        void searchMealsByIngredient(String ingredient);

        void searchMealsByCategory(String category);
    }

    interface View {
        void showMeals(List<MainMeal> meals);

        void showError(String message);
    }
}
