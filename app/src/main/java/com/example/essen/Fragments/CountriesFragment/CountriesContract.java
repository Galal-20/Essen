package com.example.essen.Fragments.CountriesFragment;

import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.Meal;

import java.util.List;

public interface CountriesContract {

    interface View {
        // Method to display the list of countries
        void showCountries(List<Meal> countries);

        // Method to display the list of meals for a selected country
        void showMeals(List<MainMeal> meals);

        // Method to display an error message
        void showError(String message);
    }

    interface Presenter {
        // Method to fetch the list of countries
        void getCountries();

        // Method to fetch meals based on the selected country
        void getMealsByCountry(String countryName);
    }
}
