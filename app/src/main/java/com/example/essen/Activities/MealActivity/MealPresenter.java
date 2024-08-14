package com.example.essen.Activities.MealActivity;

import com.example.essen.pojo.MealList;
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
            //view.showMessage("Meal name not available");
        }

        if (mealThumb != null) {
            view.showMealImage(mealThumb);
        } else {
            //view.showMessage("Meal image not available");
        }

        if (mealCat != null) {
            view.showMealCategory(mealCat);
        } else {
            //view.showMessage("Meal category not available");
        }

        if (location != null) {
            view.showMealLocation(location);
        } else {
            // view.showMessage("Meal location not available");
        }

        if (instructions != null) {
            view.showMealInstructions(instructions);
        } else {
            //view.showMessage("Meal instructions not available");
        }
        if (textIngredient != null && !textIngredient.trim().isEmpty()) {
            view.showMeaIngredients(textIngredient);
        } else {
            //view.showMessage("Meal Ingredients not available");
        }

        view.hideLoading();
    }

    @Override
    public void loadMealDetails(String mealId) {
        view.showLoading();
        RetrofitInstance.getApi().getMealDetails(mealId).enqueue(new Callback<MealList>() {
            @Override
            public void onResponse(Call<MealList> call, Response<MealList> response) {
                view.hideLoading();
                if (response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    view.showMeals(response.body().getMeals().get(0));

                } else {
                    view.showMessage("Meal details not available.");
                }
            }

            @Override
            public void onFailure(Call<MealList> call, Throwable t) {
                view.hideLoading();
                view.showMessage("Failed to fetch meal details.");
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




/*MainMeal meal = response.body().getMeals().get(0);
                    view.showMealName(meal.getStrMeal());
                    view.showMealImage(meal.getStrMealThumb());
                    view.showMealCategory(meal.getStrCategory());
                    view.showMealLocation(meal.getStrArea());
                    view.showMealInstructions(meal.getStrInstructions());
                    view.openYoutubeLink(meal.getStrYoutube());
                    view.showMeaIngredients(
                            meal.getStrIngredient1() + "\n" + meal.getStrIngredient2() +
                                    meal.getStrIngredient3() + "\n" + meal.getStrIngredient4() +
                                    meal.getStrIngredient5() + "\n" + meal.getStrIngredient6() +
                                    meal.getStrIngredient7() + "\n" + meal.getStrIngredient8() +
                                    meal.getStrIngredient9() + "\n" + meal.getStrIngredient10() +
                                    meal.getStrIngredient11() + "\n" + meal.getStrIngredient12() +
                                    meal.getStrIngredient13() + "\n" + meal.getStrIngredient14() +
                                    meal.getStrIngredient15() + "\n"

                    );*/
