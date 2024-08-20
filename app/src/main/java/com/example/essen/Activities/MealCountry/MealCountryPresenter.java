package com.example.essen.Activities.MealCountry;

import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.MealAPI;
import com.example.essen.retrofit.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealCountryPresenter implements CountryContract.Presenter {

    private final CountryContract.View view;
    private final MealAPI mealAPI;

    public MealCountryPresenter(CountryContract.View view) {
        this.view = view;
        this.mealAPI = RetrofitInstance.getApi();
    }

    @Override
    public void loadMealsByCountry(String countryName) {
        mealAPI.getMealsByCountry(countryName).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MainMeal> meals = response.body().getMeals();
                    if (meals != null && !meals.isEmpty()) {
                        view.showMeals(meals);
                    } else {
                        view.showError("No meals found");
                    }
                } else {
                    view.showError("Failed to load meals");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {

            }
        });
    }


}

