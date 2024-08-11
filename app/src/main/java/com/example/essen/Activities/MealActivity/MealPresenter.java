package com.example.essen.Activities.MealActivity;

public class MealPresenter {
    private MealView view;

    public MealPresenter(MealView view) {
        this.view = view;
    }

    public void loadMealData(String mealCat, String mealName, String mealThumb, String location, String instructions, String youtubeLink) {
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

        view.hideLoading();
    }

    public void handleYoutubeLinkClick(String youtubeLink) {
        if (youtubeLink != null && !youtubeLink.isEmpty()) {
            view.openYoutubeLink(youtubeLink);
        } else {
            view.showMessage("YouTube link not available");
        }
    }
}




