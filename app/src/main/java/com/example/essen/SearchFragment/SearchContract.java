package com.example.essen.SearchFragment;

import com.example.essen.pojo.MainMeal;

import java.util.List;

public interface SearchContract {
    interface Presenter {
        void searchMeals(String query);
    }

    interface View {
        void showMeals(List<MainMeal> meals);

        void showError(String message);
    }
}
