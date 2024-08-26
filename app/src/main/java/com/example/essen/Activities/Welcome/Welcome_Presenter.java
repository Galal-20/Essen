package com.example.essen.Activities.Welcome;

import com.example.essen.repository.MealRepository;

public class Welcome_Presenter implements WelcomeContract.Presenter {

    private WelcomeContract.View view;
    private MealRepository mealRepository;

    public Welcome_Presenter(WelcomeContract.View view, MealRepository mealRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
    }

    public void checkLoginStatus() {
        if (mealRepository.isLoggedIn()) {
            view.navigateToMainActivity();
        }
    }

    public void setGuestMode() {
        mealRepository.setGuestMode(true);
        view.navigateToMainActivity();
    }

    public void register() {
        view.navigateToSignUp();
    }

    public void login() {
        view.navigateToLogin();
    }
}
