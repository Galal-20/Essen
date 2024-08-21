package com.example.essen.Fragments.HomeFragment;

import com.example.essen.pojo.Category;
import com.example.essen.pojo.MainMeal;

import java.util.List;

public interface HomeContract {
    interface View {
        void showRandomMeal(MainMeal meal);

        void showError(String message);

        void showPopularMeals(List<MainMeal> meal);

        void showCategories(List<Category> categories);


    }

    interface Presenter {
        void getRandomMeal();

        void getPopularMeals();

        void getCategories();


    }
}


