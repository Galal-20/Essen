package com.example.essen.Activities.MealActivity;

import com.example.essen.pojo.MealId;
import com.example.essen.retrofit.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealPresenter implements MealView.presenter {
    private MealView view;

    public MealPresenter(MealView view) {
        this.view = view;
    }

    public void loadMealData(String mealCat, String mealName, String mealThumb, String location,
                             String instructions, String youtubeLink, String textIngredient) {
        view.showLoading();

        if (mealName != null) {
            view.showMealName(mealName);
        } else {
            view.showMessage("Meal name not available");
        }

        if (mealThumb != null) {
            view.showMealImage(mealThumb);
        } else {
            view.showMessage("Meal image not available");
        }

        if (mealCat != null) {
            view.showMealCategory(mealCat);
        } else {
            view.showMessage("Meal category not available");
        }

        if (location != null) {
            view.showMealLocation(location);
        } else {
            view.showMessage("Meal location not available");
        }

        if (instructions != null) {
            view.showMealInstructions(instructions);
        } else {
            view.showMessage("Meal instructions not available");
        }
        if (textIngredient != null && !textIngredient.trim().isEmpty()) {
            view.showMeaIngredients(textIngredient);
        } else {
            view.showMessage("Meal Ingredients not available");
        }

        view.hideLoading();
    }

    @Override
    public void loadMealDetails(String mealId) {
        view.showLoading();
        RetrofitInstance.getApi().getMealDetails(mealId).enqueue(new Callback<MealId>() {
            @Override
            public void onResponse(Call<MealId> call, Response<MealId> response) {
                if (response.isSuccessful()) {
                    view.showMeals(response.body().getMeals().get(0));
                } else {
                    view.showMessage("Failed to load meal details");
                }

            }

            @Override
            public void onFailure(Call<MealId> call, Throwable t) {
                view.showMessage("Failed to load meal details");
            }
        });
    }


    public void handleYoutubeLinkClick(String youtubeLink) {
        if (youtubeLink != null && !youtubeLink.isEmpty()) {
            view.openYoutubeLink(youtubeLink);
        } else {
            view.showMessage("YouTube link not available");
        }
    }
}




