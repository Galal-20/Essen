package com.example.essen.presenter;

import android.app.Activity;

import com.example.essen.model.AuthService;
import com.example.essen.view.AuthView;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPresenter {
    private AuthView view;
    private AuthService authService;

    public SignUpPresenter(AuthView view, Activity activity) {
        this.view = view;
        this.authService = new AuthService(activity);
    }

    public void signUp(String fullName, String email, String password) {
        authService.signUp(fullName, email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showSignUpSuccess("Sign Up Successful");
            }

            @Override
            public void onFailure(String message) {
                view.showSignUpError("Sign Up Failed: " + message);
            }
        });
    }
}
