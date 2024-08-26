package com.example.essen.Activities.AuthActivities.Login;

import android.app.Activity;
import android.content.Intent;

import com.example.essen.model.AuthService;
import com.example.essen.repository.MealRepository;
import com.example.essen.repository.MealRepositoryImpl;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter {
    private AuthViewLogin view;
    private MealRepository mealRepository;


    public LoginPresenter(AuthViewLogin view, Activity activity) {
        this.view = view;
        this.mealRepository = new MealRepositoryImpl(activity);
    }

    public void login(String email, String password) {
        mealRepository.login(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showLoginSuccess("Login Successful");
            }

            @Override
            public void onFailure(String message) {
                view.showLoginError("Login Failed: " + message);
                view.checkNetworkStatus("No Internet connection");

            }
        });
    }


    public void loginWithGoogle(Activity activity) {
        mealRepository.signInWithGoogle(activity, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showLoginSuccess("Google Login Successful");
            }

            @Override
            public void onFailure(String message) {
                view.showLoginError("Google Login Failed: " + message);
            }
        });
    }

    public void handleGoogleSignInResult(Intent data) {
        mealRepository.handleGoogleSignInResult(data, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showLoginSuccess("Google Login Successful");
            }

            @Override
            public void onFailure(String message) {
                view.showLoginError("Google Login Failed: " + message);
            }
        });
    }


}
