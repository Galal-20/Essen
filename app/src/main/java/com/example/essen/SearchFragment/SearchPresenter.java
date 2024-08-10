package com.example.essen.SearchFragment;

import com.example.essen.pojo.MainMeal;
import com.example.essen.pojo.MealList;
import com.example.essen.retrofit.MealAPI;
import com.example.essen.retrofit.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPresenter implements SearchContract.Presenter {
    private final SearchContract.View view;
    private final MealAPI mealAPI;

    public SearchPresenter(SearchContract.View view) {
        this.view = view;
        this.mealAPI = RetrofitInstance.getApi();
    }

    @Override
    public void searchMeals(String query) {
        mealAPI.getSearchMeal(query).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
               /* if (response.isSuccessful() && response.body() != null) {
                    // Extract the list of meals from the MealList object
                    List<MainMeal> meals = response.body().getMeals();
                    view.showMeals(meals);
                } else {
                    view.showError("No results found.");
                }*/
                if (response.isSuccessful() && response.body() != null) {
                    List<MainMeal> meals = response.body().getMeals();
                    if (meals != null && !meals.isEmpty()) {
                        view.showMeals(meals);
                    } else {
                        view.showError("No results found.");
                    }
                } else {
                    view.showError("Error fetching data.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }


}



 /*@Override
    public void searchMeals(String query) {
        mealAPI.getSearchMeal(query).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.showMeals(response.body().getMeals());
                } else {
                    view.showError("No results found.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }*/