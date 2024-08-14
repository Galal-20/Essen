package com.example.essen.Fragments.CountriesFragment;

import com.example.essen.pojo.Countries;
import com.example.essen.retrofit.MealAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountriesPresenter implements CountriesContract.Presenter {

    private CountriesContract.View view;
    private MealAPI mealAPI;

    public CountriesPresenter(CountriesContract.View view, MealAPI mealAPI) {
        this.view = view;
        this.mealAPI = mealAPI;
    }

    public void getCountries() {
        mealAPI.getCountries().enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Call<Countries> call, Response<Countries> response) {
                if (response.isSuccessful()) {
                    view.showCountries(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<Countries> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    /*public void getMealsByCountry(String countryName) {
        mealAPI.getMealsByCountry(countryName).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showMeals(response.body().getMeals());
                } else {
                    view.showError("Failed to fetch meals");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }*/

}

