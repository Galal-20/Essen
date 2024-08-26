package com.example.essen.Activities.MealCountry;

import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.repository.MealRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealCountryPresenter implements CountryContract.Presenter {

    private final CountryContract.View view;
    private final MealRepository mealRepository;


    public MealCountryPresenter(CountryContract.View view, MealRepository mealRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
    }

    @Override
    public void loadMealsByCountry(String countryName) {
        mealRepository.searchMealsByCountry(countryName).enqueue(new Callback<MealList>() {
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
                view.showError(t.getMessage());
            }
        });
    }


}

