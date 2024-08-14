package com.example.essen.Activities.MealCountry;

import android.util.Log;

import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.MealAPI;
import com.example.essen.retrofit.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealCountryPresenter implements CountryContract.Presenter {

    private CountryContract.View view;
    private MealAPI mealAPI;

    public MealCountryPresenter(CountryContract.View view) {
        this.view = view;
        mealAPI = RetrofitInstance.getApi();
    }

    @Override
    public void loadMealsByCountry(String countryName) {
        mealAPI.getMealsByCountry(countryName).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<MainMeal> meals = response.body().getMeals();
                    Log.d("API Response", meals.toString());

                    view.showMeals(meals);
                } else {
                    view.showError("Failed to load meals");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }
}
