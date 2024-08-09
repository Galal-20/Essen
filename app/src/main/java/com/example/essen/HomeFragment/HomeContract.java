package com.example.essen.HomeFragment;

import com.example.essen.pojo.Category;
import com.example.essen.pojo.Meal;

import java.util.List;

public interface HomeContract {
    interface View {
        void showRandomMeal(Meal meal);

        void showError(String message);

        void showPopularMeals(List<Meal> meal);

        void showCategories(List<Category> categories);

    }

    interface Presenter {
        void getRandomMeal();

        void getPopularMeals();

        void getCategories();

    }
}


