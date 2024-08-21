package com.example.essen.Fragments.CountriesFragment;

import com.example.essen.pojo.MainMeal;

import java.util.List;

public interface CountriesContract {

    interface View {
        void showCountries(List<MainMeal> countries);

        void showError(String message);
    }

    interface Presenter {
        void getCountries();

    }
}
