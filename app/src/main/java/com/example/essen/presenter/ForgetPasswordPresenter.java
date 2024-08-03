package com.example.essen.presenter;

import android.app.Activity;

import com.example.essen.model.AuthService;
import com.example.essen.view.Auth.AuthViewForgetPassword;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPasswordPresenter {
    private AuthViewForgetPassword view;
    private AuthService authService;

    public ForgetPasswordPresenter(AuthViewForgetPassword view, Activity activity) {
        this.view = view;
        this.authService = new AuthService(activity);
    }

    public void forgetPassword(String email) {
        authService.forgetPassword(email, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                view.showForgetPasswordSuccess("Reset link sent to email");
            }

            @Override
            public void onFailure(String message) {
                view.showForgetPasswordError("Error sending reset link: " + message);
            }

        });
    }
}
