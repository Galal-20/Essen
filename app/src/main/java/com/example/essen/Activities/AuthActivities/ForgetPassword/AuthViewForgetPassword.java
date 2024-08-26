package com.example.essen.Activities.AuthActivities.ForgetPassword;

public interface AuthViewForgetPassword {
    void showForgetPasswordSuccess(String message);

    void showForgetPasswordError(String message);

    void showLoading();

    void hideLoading();
}
