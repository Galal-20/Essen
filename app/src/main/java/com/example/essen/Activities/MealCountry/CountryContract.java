// CountryContract.java
package com.example.essen.Activities.MealCountry;

import com.example.essen.pojo.MainMeal;

import java.util.List;

public interface CountryContract {

    interface View {
        void showMeals(List<MainMeal> meals);

        void showError(String message);

        void setTitle(String title);
    }

    interface Presenter {
        void loadMealsByCountry(String countryName);
    }
}
