package com.example.essen.Activities.AuthActivities.ForgetPassword;

import com.example.essen.model.AuthService;
import com.example.essen.repository.MealRepository;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPasswordPresenter {
    private AuthViewForgetPassword view;
    private MealRepository mealRepository;

    public ForgetPasswordPresenter(AuthViewForgetPassword view, MealRepository mealRepository) {
        this.view = view;
        this.mealRepository = mealRepository;
    }

    public void forgetPassword(String email) {
        view.showLoading(); // Show loading indicator
        mealRepository.resetPassword(email, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.hideLoading(); // Hide loading indicator
                view.showForgetPasswordSuccess("Reset link sent to email");
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading(); // Hide loading indicator
                view.showForgetPasswordError("Error sending reset link: " + message);
            }
        });
    }
}
