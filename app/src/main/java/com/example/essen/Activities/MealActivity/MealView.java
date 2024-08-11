package com.example.essen.Activities.MealActivity;

public interface MealView {
    void showMealName(String name);

    void showMealImage(String imageUrl);

    void showMealCategory(String category);

    void showMealLocation(String location);

    void showMealInstructions(String instructions);

    void showMessage(String message);

    void showLoading();

    void hideLoading();

    void openYoutubeLink(String url);  // New method to handle opening YouTube links

}
