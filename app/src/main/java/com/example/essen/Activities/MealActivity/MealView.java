package com.example.essen.Activities.MealActivity;

import com.example.essen.pojo.AllDetailsMeal;

public interface MealView {
    void showMealName(String name);

    void showMeals(AllDetailsMeal meal);

    void showMealImage(String imageUrl);

    void showMealCategory(String category);

    void showMealLocation(String location);

    void showMealInstructions(String instructions);

    void showMeaIngredients(String ingredients);

    void showMessage(String message);

    void showLoading();

    void hideLoading();

    void openYoutubeLink(String url);

    interface presenter {
        void loadMealDetails(String mealId);
    }


}
