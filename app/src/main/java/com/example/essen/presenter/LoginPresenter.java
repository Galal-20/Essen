package com.example.essen.presenter;

import android.app.Activity;
import android.content.Intent;

import com.example.essen.model.AuthService;
import com.example.essen.view.AuthView;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter {
    private AuthView view;
    private AuthService authService;

    public LoginPresenter(AuthView view, Activity activity) {
        this.view = view;
        this.authService = new AuthService(activity);
    }

    public void login(String email, String password) {
        authService.login(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showLoginSuccess("Login Successful");
            }

            @Override
            public void onFailure(String message) {
                view.showLoginError("Login Failed: " + message);
            }
        });
    }

    public void loginWithGoogle(Activity activity) {
        authService.signInWithGoogle(activity);
    }

    public void handleGoogleSignInResult(Intent data) {
        authService.handleGoogleSignInResult(data, new AuthService.AuthCallback() {
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
